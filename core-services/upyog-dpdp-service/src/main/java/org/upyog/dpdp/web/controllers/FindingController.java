package org.upyog.dpdp.web.controllers;

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
@RequestMapping("/finding/v1")
@Tag(name = "Finding")
public class FindingController {

    private final ComplianceService complianceService;

    public FindingController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @PostMapping("/_search")
    public ResponseEntity<GenericResponse> search(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(complianceService.findings(request));
    }
}
