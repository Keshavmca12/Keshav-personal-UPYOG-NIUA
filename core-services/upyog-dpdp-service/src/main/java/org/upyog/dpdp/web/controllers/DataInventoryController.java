package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.DataInventoryService;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

@RestController
@RequestMapping("/inventory/v1")
@Tag(name = "DataInventory")
public class DataInventoryController {

    private final DataInventoryService dataInventoryService;

    public DataInventoryController(DataInventoryService dataInventoryService) {
        this.dataInventoryService = dataInventoryService;
    }

    @PostMapping("/_validate")
    @Operation(summary = "Validate field classifications against MDMS data inventory")
    public ResponseEntity<GenericResponse> validate(@RequestBody GenericRequest request) {
        return ResponseEntity.ok(dataInventoryService.validate(request));
    }
}
