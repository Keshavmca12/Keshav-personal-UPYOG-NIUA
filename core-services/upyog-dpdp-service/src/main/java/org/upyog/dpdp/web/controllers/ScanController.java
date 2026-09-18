package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.egov.tracer.model.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.ScanService;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

@RestController
@RequestMapping("/scan/v1")
@Tag(name = "Scan")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/_create")
    @Operation(summary = "Create and run a compliance scan")
    public ResponseEntity<GenericResponse> create(@Valid @RequestBody GenericRequest request) {
        if (request.getScanJob() == null) {
            throw new CustomException("DPDP_VALIDATION", "ScanJob is required");
        }
        return ResponseEntity.ok(scanService.create(request, request.getScanJob()));
    }

    @PostMapping("/_search")
    @Operation(summary = "Search scan jobs")
    public ResponseEntity<GenericResponse> search(@Valid @RequestBody GenericRequest request) {
        return ResponseEntity.ok(scanService.search(request));
    }
}
