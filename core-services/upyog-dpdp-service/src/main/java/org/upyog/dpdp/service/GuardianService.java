package org.upyog.dpdp.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.repository.GuardianRepository;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.GuardianLink;
import org.upyog.dpdp.web.models.GuardianRequest;
import org.upyog.dpdp.web.models.GuardianResponse;

import java.util.List;
import java.util.UUID;

@Service
public class GuardianService {

    private final GuardianRepository repository;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;
    private final PrivacyEventService privacyEventService;
    private final DpdpConfiguration configuration;

    public GuardianService(GuardianRepository repository, RequestValidator requestValidator,
                           ResponseInfoFactory responseInfoFactory, PrivacyEventService privacyEventService,
                           DpdpConfiguration configuration) {
        this.repository = repository;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
        this.privacyEventService = privacyEventService;
        this.configuration = configuration;
    }

    public GuardianResponse create(GuardianRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        GuardianLink link = request.getGuardianLink();
        requestValidator.assertTenantAccess(user, link.getTenantId());
        long now = System.currentTimeMillis();
        link.setId(UUID.randomUUID().toString());
        link.setCreatedBy(user.getUuid());
        link.setCreatedTime(now);
        link.setLastModifiedTime(now);
        link.setVerified(link.isVerified() && StringUtils.isNotBlank(link.getVerificationMethod()));
        repository.insert(link);
        privacyEventService.emit(request.getRequestInfo(), link.getTenantId(), "DPDP_GUARDIAN_LINKED",
                "GUARDIAN", link.getId(), null, null, null, "SUCCESS", configuration.getAuditEventTopic());
        return GuardianResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .guardianLink(link)
                .build();
    }

    public GuardianResponse verify(GuardianRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        GuardianLink link = request.getGuardianLink();
        requestValidator.assertTenantAccess(user, link.getTenantId());
        link.setLastModifiedTime(System.currentTimeMillis());
        link.setVerified(StringUtils.isNotBlank(link.getVerificationMethod())
                || StringUtils.isNotBlank(link.getAuthorityOrderRef()));
        repository.updateVerification(link);
        return GuardianResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .guardianLink(link)
                .build();
    }

    public GuardianResponse search(GuardianRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        GuardianLink criteria = request.getGuardianLink();
        requestValidator.assertTenantAccess(user, criteria.getTenantId());
        String childUuid = requestValidator.resolvePrincipal(user, criteria.getChildUuid());
        List<GuardianLink> links = repository.search(criteria.getTenantId(), childUuid);
        return GuardianResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .guardianLinks(links)
                .build();
    }
}
