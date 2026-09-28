package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.egov.common.contract.request.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.repository.ScanRepository;
import org.upyog.dpdp.service.RetentionService;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

@RestController
@RequestMapping("/retention/v1")
@Tag(name = "Retention")
public class RetentionController {

    private final RetentionService retentionService;
    private final ScanRepository scanRepository;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;

    public RetentionController(RetentionService retentionService, ScanRepository scanRepository,
                               RequestValidator requestValidator, ResponseInfoFactory responseInfoFactory) {
        this.retentionService = retentionService;
        this.scanRepository = scanRepository;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping("/_search")
    @Operation(summary = "Search processing activities")
    public ResponseEntity<GenericResponse> search(@Valid @RequestBody GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = String.valueOf(request.getCriteria().get("tenantId"));
        requestValidator.assertTenantAccess(user, tenantId);
        return ResponseEntity.ok(GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .activities(scanRepository.searchActivities(tenantId))
                .build());
    }

    @PostMapping("/_evaluate")
    @Operation(summary = "Evaluate expired retention activities")
    public ResponseEntity<GenericResponse> evaluate(@Valid @RequestBody GenericRequest request) {
        requestValidator.requireUser(request.getRequestInfo());
        return ResponseEntity.ok(GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .activities(retentionService.evaluateExpired(request.getRequestInfo()))
                .build());
    }
}
