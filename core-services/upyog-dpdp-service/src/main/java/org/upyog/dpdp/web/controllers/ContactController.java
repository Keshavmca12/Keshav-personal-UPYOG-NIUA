package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.MdmsPolicyClient;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contact/v1")
@Tag(name = "Contact")
public class ContactController {

    private final MdmsPolicyClient mdmsPolicyClient;
    private final ResponseInfoFactory responseInfoFactory;

    public ContactController(MdmsPolicyClient mdmsPolicyClient, ResponseInfoFactory responseInfoFactory) {
        this.mdmsPolicyClient = mdmsPolicyClient;
        this.responseInfoFactory = responseInfoFactory;
    }

    @PostMapping("/_search")
    @Operation(summary = "Public fiduciary / DPO contact details from MDMS")
    public ResponseEntity<GenericResponse> search(@RequestBody GenericRequest request) {
        String tenantId = request.getCriteria() == null ? "pg"
                : String.valueOf(request.getCriteria().getOrDefault("tenantId", "pg"));
        List<Map<String, Object>> contacts = mdmsPolicyClient.getFiduciaryContact(tenantId);
        return ResponseEntity.ok(GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .summary(Map.of("contacts", contacts))
                .build());
    }
}
