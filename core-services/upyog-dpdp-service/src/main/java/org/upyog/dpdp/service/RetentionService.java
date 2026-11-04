package org.upyog.dpdp.service;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.adapter.DpdpDataProvider;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.repository.ErasureJobRepository;
import org.upyog.dpdp.repository.ScanRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.ProcessingActivity;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RetentionService {

    private final ScanRepository scanRepository;
    private final ErasureJobRepository erasureJobRepository;
    private final List<DpdpDataProvider> providers;
    private final PrivacyEventService privacyEventService;
    private final DpdpConfiguration configuration;
    private final MeterRegistry meterRegistry;

    public RetentionService(ScanRepository scanRepository, ErasureJobRepository erasureJobRepository,
                            List<DpdpDataProvider> providers, PrivacyEventService privacyEventService,
                            DpdpConfiguration configuration, MeterRegistry meterRegistry) {
        this.scanRepository = scanRepository;
        this.erasureJobRepository = erasureJobRepository;
        this.providers = providers;
        this.privacyEventService = privacyEventService;
        this.configuration = configuration;
        this.meterRegistry = meterRegistry;
    }

    public void registerActivity(String tenantId, String application, String purpose, String basis,
                                 String categories, String retentionCode, Long expiresAt, String actor) {
        scanRepository.insertActivity(ProcessingActivity.builder()
                .id(java.util.UUID.randomUUID().toString())
                .tenantId(tenantId)
                .applicationCode(application)
                .purposeCode(purpose)
                .processingBasis(basis)
                .dataCategories(categories)
                .retentionPolicyCode(retentionCode)
                .legalHold(false)
                .status(DpdpConstants.STATUS_ACTIVE)
                .expiresAt(expiresAt)
                .createdBy(actor)
                .createdTime(System.currentTimeMillis())
                .build());
    }

    public List<ProcessingActivity> evaluateExpired(RequestInfo requestInfo) {
        List<ProcessingActivity> expired = scanRepository.findExpired(System.currentTimeMillis());
        List<ProcessingActivity> processed = new ArrayList<>();
        for (ProcessingActivity activity : expired) {
            if (isOnLegalHold(activity)) {
                log.info("Skipping retention for {} due to legal hold", activity.getId());
                continue;
            }
            String action = configuration.getDefaultRetentionAction();
            runErasure(activity, action, requestInfo);
            String status = statusForAction(action);
            scanRepository.updateActivityStatus(activity.getId(), activity.getTenantId(), status,
                    System.currentTimeMillis());
            activity.setStatus(status);
            processed.add(activity);
            privacyEventService.emit(requestInfo, activity.getTenantId(), "DPDP_RETENTION_EXPIRED",
                    "PROCESSING_ACTIVITY", activity.getId(), activity.getApplicationCode(),
                    activity.getPurposeCode(), activity.getDataCategories(), "SUCCESS",
                    configuration.getRetentionEventTopic());
            meterRegistry.counter("dpdp.deletion.completed").increment();
        }
        return processed;
    }

    private void runErasure(ProcessingActivity activity, String action, RequestInfo requestInfo) {
        String jobId = erasureJobRepository.createJob(activity.getTenantId(), activity.getId(),
                activity.getApplicationCode(), action, "system");
        DpdpDataProvider.DataPrincipalContext context = new DpdpDataProvider.DataPrincipalContext(
                activity.getTenantId(), null, activity.getApplicationCode());
        for (DpdpDataProvider provider : providers) {
            if (activity.getApplicationCode() != null
                    && !provider.applicationCode().equals(activity.getApplicationCode())
                    && !"GENERIC".equals(provider.applicationCode())) {
                continue;
            }
            DpdpDataProvider.DeletionResult result = "ARCHIVE".equals(action)
                    ? provider.anonymisePersonalData(context)
                    : provider.deletePersonalData(context);
            erasureJobRepository.addTask(jobId, activity.getTenantId(), provider.applicationCode(),
                    result.deleted() ? DpdpConstants.STATUS_COMPLETED : DpdpConstants.STATUS_PENDING,
                    result.message());
        }
        erasureJobRepository.completeJob(jobId, activity.getTenantId(), DpdpConstants.STATUS_COMPLETED, null);
    }

    static boolean isOnLegalHold(ProcessingActivity activity) {
        return activity != null && activity.isLegalHold();
    }

    static String statusForAction(String action) {
        if (action == null) {
            return DpdpConstants.STATUS_SOFT_DELETED;
        }
        return switch (action) {
            case "HARD_DELETE" -> DpdpConstants.STATUS_HARD_DELETED;
            case "ARCHIVE" -> DpdpConstants.STATUS_ARCHIVED;
            default -> DpdpConstants.STATUS_SOFT_DELETED;
        };
    }
}
