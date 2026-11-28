package org.upyog.dpdp.service;

import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.repository.GrievanceRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.Grievance;
import org.upyog.dpdp.web.models.GrievanceRequest;
import org.upyog.dpdp.web.models.GrievanceResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class GrievanceService {

    private final GrievanceRepository repository;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;

    public GrievanceService(GrievanceRepository repository, RequestValidator requestValidator,
                            ResponseInfoFactory responseInfoFactory) {
        this.repository = repository;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
    }

    public GrievanceResponse create(GrievanceRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        Grievance grievance = request.getGrievance();
        requestValidator.assertTenantAccess(user, grievance.getTenantId());
        grievance.setDataPrincipalUuid(requestValidator.resolvePrincipal(user, grievance.getDataPrincipalUuid()));
        long now = System.currentTimeMillis();
        grievance.setId(UUID.randomUUID().toString());
        grievance.setStatus(DpdpConstants.STATUS_OPEN);
        grievance.setSlaDueAt(Instant.ofEpochMilli(now).plus(DpdpConstants.SLA_GRIEVANCE_DAYS, ChronoUnit.DAYS)
                .toEpochMilli());
        grievance.setCreatedBy(user.getUuid());
        grievance.setLastModifiedBy(user.getUuid());
        grievance.setCreatedTime(now);
        grievance.setLastModifiedTime(now);
        repository.insert(grievance);
        return GrievanceResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .grievance(grievance)
                .build();
    }

    public GrievanceResponse search(GrievanceRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        Grievance criteria = request.getGrievance();
        requestValidator.assertTenantAccess(user, criteria.getTenantId());
        String uuid = requestValidator.resolvePrincipal(user, criteria.getDataPrincipalUuid());
        return GrievanceResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .grievances(repository.search(criteria.getTenantId(), uuid))
                .build();
    }
}
