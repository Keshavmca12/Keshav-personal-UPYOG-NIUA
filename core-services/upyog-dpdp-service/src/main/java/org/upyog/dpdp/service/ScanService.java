package org.upyog.dpdp.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.engine.ScoringService;
import org.upyog.dpdp.repository.ScanRepository;
import org.upyog.dpdp.scanner.AccessControlScanner;
import org.upyog.dpdp.scanner.ApiScanner;
import org.upyog.dpdp.scanner.DatabaseScanner;
import org.upyog.dpdp.scanner.EncryptionComplianceScanner;
import org.upyog.dpdp.scanner.LogScanner;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.ComplianceScore;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;
import org.upyog.dpdp.web.models.ScanJob;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ScanService {

    private final ScanRepository repository;
    private final DatabaseScanner databaseScanner;
    private final ApiScanner apiScanner;
    private final LogScanner logScanner;
    private final EncryptionComplianceScanner encryptionScanner;
    private final AccessControlScanner accessControlScanner;
    private final ScoringService scoringService;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;
    private final PrivacyEventService privacyEventService;
    private final DpdpConfiguration configuration;
    private final MeterRegistry meterRegistry;

    public ScanService(ScanRepository repository, DatabaseScanner databaseScanner, ApiScanner apiScanner,
                       LogScanner logScanner, EncryptionComplianceScanner encryptionScanner,
                       AccessControlScanner accessControlScanner, ScoringService scoringService,
                       RequestValidator requestValidator, ResponseInfoFactory responseInfoFactory,
                       PrivacyEventService privacyEventService, DpdpConfiguration configuration,
                       MeterRegistry meterRegistry) {
        this.repository = repository;
        this.databaseScanner = databaseScanner;
        this.apiScanner = apiScanner;
        this.logScanner = logScanner;
        this.encryptionScanner = encryptionScanner;
        this.accessControlScanner = accessControlScanner;
        this.scoringService = scoringService;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
        this.privacyEventService = privacyEventService;
        this.configuration = configuration;
        this.meterRegistry = meterRegistry;
    }

    public GenericResponse create(GenericRequest request, ScanJob job) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        requestValidator.assertTenantAccess(user, job.getTenantId());
        long now = System.currentTimeMillis();
        job.setId(UUID.randomUUID().toString());
        job.setStatus(DpdpConstants.SCAN_RUNNING);
        job.setStartedAt(now);
        job.setCreatedBy(user.getUuid());
        job.setCreatedTime(now);
        repository.insertScan(job);
        Timer.Sample sample = Timer.start(meterRegistry);
        List<Finding> findings = new ArrayList<>();
        try {
            List<ComplianceRule> rules = repository.loadRules(job.getTenantId().contains(".")
                    ? job.getTenantId().split("\\.")[0] : job.getTenantId());
            String type = job.getScanType();
            if (DpdpConstants.SCAN_DATABASE.equals(type) || DpdpConstants.SCAN_FULL.equals(type)) {
                findings.addAll(databaseScanner.scan(job, rules));
            }
            if (DpdpConstants.SCAN_API.equals(type) || DpdpConstants.SCAN_FULL.equals(type)) {
                findings.addAll(apiScanner.scan(job, rules));
            }
            if (DpdpConstants.SCAN_LOG.equals(type) || DpdpConstants.SCAN_FULL.equals(type)) {
                findings.addAll(logScanner.scan(job, rules));
            }
            if (DpdpConstants.SCAN_ENCRYPTION.equals(type) || DpdpConstants.SCAN_FULL.equals(type)) {
                findings.addAll(encryptionScanner.scan(job, rules));
            }
            if (DpdpConstants.SCAN_ACCESS.equals(type) || DpdpConstants.SCAN_FULL.equals(type)) {
                findings.addAll(accessControlScanner.scan(job, rules));
            }
            for (Finding finding : findings) {
                if (finding.getCreatedBy() == null) {
                    finding.setCreatedBy(user.getUuid());
                }
                repository.insertFinding(finding);
            }
            job.setStatus(DpdpConstants.SCAN_COMPLETED);
            job.setFindingCount(findings.size());
            job.setCompletedAt(System.currentTimeMillis());
            ComplianceScore score = scoringService.score(job.getTenantId(), findings);
            repository.saveScoreSnapshot(score);
            meterRegistry.counter("dpdp.scan.findings").increment(findings.size());
        } catch (Exception ex) {
            job.setStatus(DpdpConstants.SCAN_FAILED);
            job.setErrorMessage(ex.getMessage());
            job.setCompletedAt(System.currentTimeMillis());
        } finally {
            repository.updateScan(job);
            sample.stop(meterRegistry.timer("dpdp.scan.duration"));
        }
        privacyEventService.emit(request.getRequestInfo(), job.getTenantId(), "DPDP_SCAN_COMPLETED",
                "SCAN", job.getId(), job.getApplicationCode(), null, null, job.getStatus(),
                configuration.getScanEventTopic());
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .scans(List.of(job))
                .findings(findings)
                .build();
    }

    public GenericResponse search(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = String.valueOf(request.getCriteria().get("tenantId"));
        requestValidator.assertTenantAccess(user, tenantId);
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .scans(repository.searchScans(tenantId))
                .build();
    }
}
