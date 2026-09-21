package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.egov.tracer.model.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.IncidentService;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

@RestController
@RequestMapping("/incident/v1")
@Tag(name = "Incident")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping("/_create")
    public ResponseEntity<GenericResponse> create(@Valid @RequestBody GenericRequest request) {
        if (request.getIncident() == null) {
            throw new CustomException("DPDP_VALIDATION", "PrivacyIncident is required");
        }
        return ResponseEntity.ok(incidentService.create(request, request.getIncident()));
    }

    @PostMapping("/_search")
    public ResponseEntity<GenericResponse> search(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(incidentService.search(request));
    }

    @PostMapping("/_update")
    public ResponseEntity<GenericResponse> update(@Valid @RequestBody GenericRequest request) {
        if (request.getIncident() == null) {
            throw new CustomException("DPDP_VALIDATION", "PrivacyIncident is required");
        }
        return ResponseEntity.ok(incidentService.update(request, request.getIncident()));
    }
}
