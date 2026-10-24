package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.NominationService;
import org.upyog.dpdp.web.models.NominationRequest;
import org.upyog.dpdp.web.models.NominationResponse;

@RestController
@RequestMapping("/nomination/v1")
@Tag(name = "Nomination")
public class NominationController {

    private final NominationService nominationService;

    public NominationController(NominationService nominationService) {
        this.nominationService = nominationService;
    }

    @PostMapping("/_create")
    @Operation(summary = "Nominate a contact for posthumous rights")
    public ResponseEntity<NominationResponse> create(@Valid @RequestBody NominationRequest request) {
        return ResponseEntity.ok(nominationService.create(request));
    }

    @PostMapping("/_search")
    @Operation(summary = "Search nominees for a data principal")
    public ResponseEntity<NominationResponse> search(@Valid @RequestBody NominationRequest request) {
        return ResponseEntity.ok(nominationService.search(request));
    }
}
