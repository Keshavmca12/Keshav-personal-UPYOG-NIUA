package org.upyog.dpdp.service;

import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.repository.ScanRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;
import org.upyog.dpdp.web.models.PrivacyIncident;

import java.util.List;
import java.util.UUID;

@Service
public class IncidentService {

    private final ScanRepository repository;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;
    private final PrivacyEventService privacyEventService;
    private final DpdpConfiguration configuration;

    public IncidentService(ScanRepository repository, RequestValidator requestValidator,
                           ResponseInfoFactory responseInfoFactory, PrivacyEventService privacyEventService,
                           DpdpConfiguration configuration) {
        this.repository = repository;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
        this.privacyEventService = privacyEventService;
        this.configuration = configuration;
    }

    public GenericResponse create(GenericRequest request, PrivacyIncident incident) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        requestValidator.assertTenantAccess(user, incident.getTenantId());
        long now = System.currentTimeMillis();
        incident.setId(UUID.randomUUID().toString());
        incident.setIncidentNumber("DPDP-INC-" + now);
        incident.setStatus(incident.getStatus() == null ? DpdpConstants.STATUS_OPEN : incident.getStatus());
        incident.setCreatedBy(user.getUuid());
        incident.setCreatedTime(now);
        incident.setLastModifiedTime(now);
        repository.insertIncident(incident);
        privacyEventService.emit(request.getRequestInfo(), incident.getTenantId(), "DPDP_INCIDENT_CREATED",
                "INCIDENT", incident.getId(), incident.getApplicationCode(), null, incident.getDataCategory(),
                "SUCCESS", configuration.getIncidentEventTopic());
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .incidents(List.of(incident))
                .build();
    }

    public GenericResponse search(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = String.valueOf(request.getCriteria().get("tenantId"));
        requestValidator.assertTenantAccess(user, tenantId);
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .incidents(repository.searchIncidents(tenantId))
                .build();
    }

    public GenericResponse update(GenericRequest request, PrivacyIncident incident) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        requestValidator.assertTenantAccess(user, incident.getTenantId());
        incident.setLastModifiedTime(System.currentTimeMillis());
        repository.updateIncident(incident);
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .incidents(List.of(incident))
                .build();
    }
}
