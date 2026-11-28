package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.ComplianceService;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

@RestController
@RequestMapping("/compliance/v1")
@Tag(name = "Compliance Dashboard")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @PostMapping("/_summary")
    public ResponseEntity<GenericResponse> summary(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(complianceService.summary(request));
    }

    @PostMapping("/_score")
    public ResponseEntity<GenericResponse> score(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(complianceService.score(request));
    }

    @PostMapping("/_search")
    public ResponseEntity<GenericResponse> search(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(complianceService.findings(request));
    }

    @PostMapping("/_recommendations")
    public ResponseEntity<GenericResponse> recommendations(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(complianceService.recommendations(request));
    }

    @PostMapping("/_trends")
    public ResponseEntity<GenericResponse> trends(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(complianceService.trends(request));
    }
}
