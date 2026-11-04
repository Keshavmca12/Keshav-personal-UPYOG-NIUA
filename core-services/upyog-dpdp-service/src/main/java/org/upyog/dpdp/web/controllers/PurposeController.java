package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.PolicyService;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

import java.util.List;

@RestController
@RequestMapping("/purpose/v1")
@Tag(name = "Purpose")
public class PurposeController {

    private final PolicyService policyService;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;

    public PurposeController(PolicyService policyService, RequestValidator requestValidator,
                             ResponseInfoFactory responseInfoFactory) {
        this.policyService = policyService;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping("/_search")
    @Operation(summary = "Search configured purposes and processing bases")
    public ResponseEntity<GenericResponse> search(@Valid @RequestBody GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = request.getCriteria() != null && request.getCriteria().get("tenantId") != null
                ? String.valueOf(request.getCriteria().get("tenantId")) : user.getTenantId();
        requestValidator.assertTenantAccess(user, tenantId);
        return ResponseEntity.ok(GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .purposes(policyService.getPurposes(tenantId))
                .build());
    }

    @PostMapping("/_validate")
    @Operation(summary = "Validate purpose limitation and data minimization")
    @SuppressWarnings("unchecked")
    public ResponseEntity<GenericResponse> validate(@Valid @RequestBody GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        if (request.getCriteria() == null) {
            throw new CustomException("DPDP_VALIDATION", "criteria is required");
        }
        String tenantId = String.valueOf(request.getCriteria().get("tenantId"));
        requestValidator.assertTenantAccess(user, tenantId);
        String purpose = String.valueOf(request.getCriteria().get("purposeCode"));
        List<String> fields = request.getCriteria().get("collectedFields") instanceof List<?> list
                ? (List<String>) list : List.of();
        return ResponseEntity.ok(GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .minimization(policyService.evaluateMinimization(tenantId, purpose, fields))
                .build());
    }
}
