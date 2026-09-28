package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.RightsService;
import org.upyog.dpdp.web.models.RightsRequest;
import org.upyog.dpdp.web.models.RightsResponse;

@RestController
@RequestMapping("/rights/v1")
@Tag(name = "Rights")
public class RightsController {

    private final RightsService rightsService;

    public RightsController(RightsService rightsService) {
        this.rightsService = rightsService;
    }

    @PostMapping("/_create")
    @Operation(summary = "Create a data-principal rights request")
    public ResponseEntity<RightsResponse> create(@Valid @RequestBody RightsRequest request) {
        return ResponseEntity.ok(rightsService.create(request));
    }

    @PostMapping("/_search")
    @Operation(summary = "Search rights requests")
    public ResponseEntity<RightsResponse> search(@Valid @RequestBody RightsRequest request) {
        return ResponseEntity.ok(rightsService.search(request));
    }
}
