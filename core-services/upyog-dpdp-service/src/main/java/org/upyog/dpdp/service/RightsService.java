package org.upyog.dpdp.service;

import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.adapter.DpdpDataProvider;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.repository.RightsRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.PersonalDataView;
import org.upyog.dpdp.web.models.PrincipalRequest;
import org.upyog.dpdp.web.models.RightsRequest;
import org.upyog.dpdp.web.models.RightsResponse;

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

    public RightsService(RightsRepository repository, List<DpdpDataProvider> providers,
                         RequestValidator requestValidator, ResponseInfoFactory responseInfoFactory,
                         PrivacyEventService privacyEventService, DpdpConfiguration configuration) {
        this.repository = repository;
        this.providers = providers;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
        this.privacyEventService = privacyEventService;
        this.configuration = configuration;
    }

    public RightsResponse create(RightsRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        PrincipalRequest principal = request.getPrincipalRequest();
        requestValidator.assertTenantAccess(user, principal.getTenantId());
        principal.setDataPrincipalUuid(requestValidator.resolvePrincipal(user, principal.getDataPrincipalUuid()));
        long now = System.currentTimeMillis();
        principal.setId(UUID.randomUUID().toString());
        principal.setRequestNumber("DPDP-DPR-" + now);
        principal.setStatus(DpdpConstants.STATUS_COMPLETED);
        principal.setCreatedBy(user.getUuid());
        principal.setLastModifiedBy(user.getUuid());
        principal.setCreatedTime(now);
        principal.setLastModifiedTime(now);

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
                case DpdpConstants.RIGHTS_ACCESS -> views.addAll(provider.findPersonalData(context));
                case DpdpConstants.RIGHTS_CORRECTION -> summary.append(provider.correctPersonalData(context,
                        principal.getCorrectionField(), principal.getCorrectionValue()).message()).append("; ");
                case DpdpConstants.RIGHTS_ERASURE -> summary.append(provider.deletePersonalData(context).message())
                        .append("; ");
                default -> summary.append("Grievance recorded; ");
            }
        }
        principal.setResultSummary(summary.toString());
        repository.insert(principal);
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
}
