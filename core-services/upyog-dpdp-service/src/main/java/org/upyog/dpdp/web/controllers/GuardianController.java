package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.GuardianService;
import org.upyog.dpdp.web.models.GuardianRequest;
import org.upyog.dpdp.web.models.GuardianResponse;

@RestController
@RequestMapping("/guardian/v1")
@Tag(name = "Guardian")
public class GuardianController {

    private final GuardianService guardianService;

    public GuardianController(GuardianService guardianService) {
        this.guardianService = guardianService;
    }

    @PostMapping("/_create")
    @Operation(summary = "Link lawful guardian for child or person with disability")
    public ResponseEntity<GuardianResponse> create(@Valid @RequestBody GuardianRequest request) {
        return ResponseEntity.ok(guardianService.create(request));
    }

    @PostMapping("/_verify")
    @Operation(summary = "Record guardian verification evidence")
    public ResponseEntity<GuardianResponse> verify(@Valid @RequestBody GuardianRequest request) {
        return ResponseEntity.ok(guardianService.verify(request));
    }

    @PostMapping("/_search")
    @Operation(summary = "Search guardian links for a child principal")
    public ResponseEntity<GuardianResponse> search(@Valid @RequestBody GuardianRequest request) {
        return ResponseEntity.ok(guardianService.search(request));
    }
}
