package org.upyog.dpdp.service;

import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.adapter.DpdpDataProvider;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.repository.RightsRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.Grievance;
import org.upyog.dpdp.web.models.GrievanceRequest;
import org.upyog.dpdp.web.models.PersonalDataView;
import org.upyog.dpdp.web.models.PrincipalRequest;
import org.upyog.dpdp.web.models.RightsRequest;
import org.upyog.dpdp.web.models.RightsResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RightsService {

    private final RightsRepository repository;
    private final List<DpdpDataProvider> providers;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;
    private final PrivacyEventService privacyEventService;
    private final DpdpConfiguration configuration;
    private final GrievanceService grievanceService;
    private final AccessLogService accessLogService;
    private final WorkflowIntegrationService workflowIntegrationService;

    public RightsService(RightsRepository repository, List<DpdpDataProvider> providers,
                         RequestValidator requestValidator, ResponseInfoFactory responseInfoFactory,
                         PrivacyEventService privacyEventService, DpdpConfiguration configuration,
                         GrievanceService grievanceService, AccessLogService accessLogService,
                         WorkflowIntegrationService workflowIntegrationService) {
        this.repository = repository;
        this.providers = providers;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
        this.privacyEventService = privacyEventService;
        this.configuration = configuration;
        this.grievanceService = grievanceService;
        this.accessLogService = accessLogService;
        this.workflowIntegrationService = workflowIntegrationService;
    }

    public RightsResponse create(RightsRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        PrincipalRequest principal = request.getPrincipalRequest();
        requestValidator.assertTenantAccess(user, principal.getTenantId());
        principal.setDataPrincipalUuid(requestValidator.resolvePrincipal(user, principal.getDataPrincipalUuid()));
        long now = System.currentTimeMillis();
        principal.setId(UUID.randomUUID().toString());
        principal.setRequestNumber("DPDP-DPR-" + now);
        principal.setStatus(DpdpConstants.STATUS_PENDING);
        principal.setWorkflowState(DpdpConstants.WORKFLOW_RECEIVED);
        principal.setSlaDueAt(slaDueAt(principal.getRequestType(), now));
        principal.setCreatedBy(user.getUuid());
        principal.setLastModifiedBy(user.getUuid());
        principal.setCreatedTime(now);
        principal.setLastModifiedTime(now);

        if (DpdpConstants.RIGHTS_GRIEVANCE.equals(principal.getRequestType())) {
            repository.insert(principal);
            grievanceService.create(GrievanceRequest.builder()
                    .requestInfo(request.getRequestInfo())
                    .grievance(Grievance.builder()
                            .tenantId(principal.getTenantId())
                            .requestId(principal.getId())
                            .dataPrincipalUuid(principal.getDataPrincipalUuid())
                            .subject("Data principal grievance")
                            .description(principal.getJustification())
                            .build())
                    .build());
            privacyEventService.emit(request.getRequestInfo(), principal.getTenantId(),
                    "DPDP_RIGHTS_" + principal.getRequestType(), "PRINCIPAL_REQUEST", principal.getId(),
                    principal.getApplicationCode(), principal.getPurposeCode(), null, "SUCCESS",
                    configuration.getRightsEventTopic());
            return RightsResponse.builder()
                    .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                    .principalRequest(principal)
                    .build();
        }

        String processInstanceId = workflowIntegrationService.startRightsWorkflow(request.getRequestInfo(), principal);
        principal.setProcessInstanceId(processInstanceId);
        repository.insert(principal);
        privacyEventService.emit(request.getRequestInfo(), principal.getTenantId(), "DPDP_RIGHTS_" + principal.getRequestType(),
                "PRINCIPAL_REQUEST", principal.getId(), principal.getApplicationCode(), principal.getPurposeCode(),
                null, "SUCCESS", configuration.getRightsEventTopic());
        return RightsResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .principalRequest(principal)
                .build();
    }

    public RightsResponse update(RightsRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        PrincipalRequest incoming = request.getPrincipalRequest();
        requestValidator.assertTenantAccess(user, incoming.getTenantId());
        PrincipalRequest existing = repository.findById(incoming.getId(), incoming.getTenantId());
        if (existing == null) {
            throw new CustomException(DpdpConstants.ERR_NOT_FOUND, "Rights request not found");
        }
        existing.setStatus(incoming.getStatus() == null ? existing.getStatus() : incoming.getStatus());
        existing.setWorkflowState(incoming.getWorkflowState() == null ? existing.getWorkflowState()
                : incoming.getWorkflowState());
        existing.setResultSummary(incoming.getResultSummary());
        existing.setLastModifiedBy(user.getUuid());
        existing.setLastModifiedTime(System.currentTimeMillis());
        if (DpdpConstants.STATUS_IN_PROGRESS.equals(existing.getStatus())
                || DpdpConstants.STATUS_COMPLETED.equals(existing.getStatus())
                || DpdpConstants.WORKFLOW_IN_REVIEW.equals(existing.getWorkflowState())) {
            return processInternal(request, existing, user);
        }
        repository.update(existing);
        return RightsResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .principalRequest(existing)
                .build();
    }

    private RightsResponse processInternal(RightsRequest request, PrincipalRequest principal, User user) {
        DpdpDataProvider.DataPrincipalContext context = new DpdpDataProvider.DataPrincipalContext(
                principal.getTenantId(), principal.getDataPrincipalUuid(), principal.getApplicationCode());
        List<PersonalDataView> views = new ArrayList<>();
        StringBuilder summary = new StringBuilder();
        for (DpdpDataProvider provider : providers) {
            if (principal.getApplicationCode() != null
                    && !"GENERIC".equals(provider.applicationCode())
                    && !provider.applicationCode().equals(principal.getApplicationCode())
                    && !"USER".equals(provider.applicationCode())) {
                continue;
            }
            switch (principal.getRequestType()) {
                case DpdpConstants.RIGHTS_ACCESS -> {
                    views.addAll(provider.findPersonalData(context));
                    accessLogService.logAccess(principal.getTenantId(), user.getUuid(),
                            principal.getDataPrincipalUuid(), provider.applicationCode(), principal.getPurposeCode(),
                            "ACCESS", "personal-data", null);
                }
                case DpdpConstants.RIGHTS_CORRECTION -> summary.append(provider.correctPersonalData(context,
                        principal.getCorrectionField(), principal.getCorrectionValue()).message()).append("; ");
                case DpdpConstants.RIGHTS_ERASURE -> summary.append(provider.deletePersonalData(context).message())
                        .append("; ");
                default -> summary.append("Recorded; ");
            }
        }
        principal.setResultSummary(summary.toString());
        principal.setStatus(DpdpConstants.STATUS_COMPLETED);
        principal.setWorkflowState(DpdpConstants.WORKFLOW_FULFILLED);
        principal.setLastModifiedTime(System.currentTimeMillis());
        repository.update(principal);
        privacyEventService.emit(request.getRequestInfo(), principal.getTenantId(), "DPDP_RIGHTS_" + principal.getRequestType(),
                "PRINCIPAL_REQUEST", principal.getId(), principal.getApplicationCode(), principal.getPurposeCode(),
                null, "SUCCESS", configuration.getRightsEventTopic());
        return RightsResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .principalRequest(principal)
                .personalData(views)
                .build();
    }

    public RightsResponse search(RightsRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        PrincipalRequest criteria = request.getPrincipalRequest();
        requestValidator.assertTenantAccess(user, criteria.getTenantId());
        String uuid = requestValidator.resolvePrincipal(user, criteria.getDataPrincipalUuid());
        return RightsResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .principalRequests(repository.search(criteria.getTenantId(), uuid, criteria.getRequestType()))
                .build();
    }

    private long slaDueAt(String requestType, long now) {
        long days = DpdpConstants.SLA_ACCESS_DAYS;
        if (DpdpConstants.RIGHTS_CORRECTION.equals(requestType)) {
            days = DpdpConstants.SLA_CORRECTION_DAYS;
        }
        return Instant.ofEpochMilli(now).plus(days, ChronoUnit.DAYS).toEpochMilli();
    }
}
