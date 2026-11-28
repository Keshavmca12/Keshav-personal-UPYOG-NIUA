package org.upyog.dpdp.service;

import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.repository.NomineeRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.NominationRequest;
import org.upyog.dpdp.web.models.NominationResponse;
import org.upyog.dpdp.web.models.Nominee;

import java.util.UUID;

@Service
public class NominationService {

    private final NomineeRepository repository;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;

    public NominationService(NomineeRepository repository, RequestValidator requestValidator,
                             ResponseInfoFactory responseInfoFactory) {
        this.repository = repository;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
    }

    public NominationResponse create(NominationRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        Nominee nominee = request.getNominee();
        requestValidator.assertTenantAccess(user, nominee.getTenantId());
        nominee.setDataPrincipalUuid(requestValidator.resolvePrincipal(user, nominee.getDataPrincipalUuid()));
        long now = System.currentTimeMillis();
        nominee.setId(UUID.randomUUID().toString());
        nominee.setStatus(DpdpConstants.STATUS_ACTIVE);
        nominee.setCreatedBy(user.getUuid());
        nominee.setLastModifiedBy(user.getUuid());
        nominee.setCreatedTime(now);
        nominee.setLastModifiedTime(now);
        repository.insert(nominee);
        return NominationResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .nominee(nominee)
                .build();
    }

    public NominationResponse search(NominationRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        Nominee criteria = request.getNominee();
        requestValidator.assertTenantAccess(user, criteria.getTenantId());
        String uuid = requestValidator.resolvePrincipal(user, criteria.getDataPrincipalUuid());
        return NominationResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .nominees(repository.search(criteria.getTenantId(), uuid))
                .build();
    }
}
