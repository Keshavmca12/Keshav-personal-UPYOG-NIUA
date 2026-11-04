package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.GrievanceService;
import org.upyog.dpdp.web.models.GrievanceRequest;
import org.upyog.dpdp.web.models.GrievanceResponse;

@RestController
@RequestMapping("/grievance/v1")
@Tag(name = "Grievance")
public class GrievanceController {

    private final GrievanceService grievanceService;

    public GrievanceController(GrievanceService grievanceService) {
        this.grievanceService = grievanceService;
    }

    @PostMapping("/_create")
    @Operation(summary = "Create a privacy grievance")
    public ResponseEntity<GrievanceResponse> create(@Valid @RequestBody GrievanceRequest request) {
        return ResponseEntity.ok(grievanceService.create(request));
    }

    @PostMapping("/_search")
    @Operation(summary = "Search grievances")
    public ResponseEntity<GrievanceResponse> search(@Valid @RequestBody GrievanceRequest request) {
        return ResponseEntity.ok(grievanceService.search(request));
    }
}
