package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.ConsentService;
import org.upyog.dpdp.web.models.ConsentRequest;
import org.upyog.dpdp.web.models.ConsentResponse;
import org.upyog.dpdp.web.models.ConsentSearchRequest;

@RestController
@RequestMapping("/consent/v1")
@Tag(name = "Consent")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PostMapping("/_create")
    @Operation(summary = "Grant consent")
    public ResponseEntity<ConsentResponse> create(@Valid @RequestBody ConsentRequest request) {
        return ResponseEntity.ok(consentService.create(request));
    }

    @PostMapping("/_search")
    @Operation(summary = "Search consents")
    public ResponseEntity<ConsentResponse> search(@Valid @RequestBody ConsentSearchRequest request) {
        return ResponseEntity.ok(consentService.search(request));
    }

    @PostMapping("/_withdraw")
    @Operation(summary = "Withdraw consent")
    public ResponseEntity<ConsentResponse> withdraw(@Valid @RequestBody ConsentRequest request) {
        return ResponseEntity.ok(consentService.withdraw(request));
    }

    @PostMapping("/_verify")
    @Operation(summary = "Verify whether consent is required and present")
    public ResponseEntity<ConsentResponse> verify(@Valid @RequestBody ConsentSearchRequest request) {
        return ResponseEntity.ok(consentService.verify(request));
    }
}
