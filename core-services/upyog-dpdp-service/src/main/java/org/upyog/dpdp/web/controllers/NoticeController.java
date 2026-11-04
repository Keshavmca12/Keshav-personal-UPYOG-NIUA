package org.upyog.dpdp.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dpdp.service.NoticeService;
import org.upyog.dpdp.web.models.NoticeRequest;
import org.upyog.dpdp.web.models.NoticeResponse;

@RestController
@RequestMapping("/notice/v1")
@Tag(name = "Notice")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/_search")
    @Operation(summary = "Search privacy notices by purpose, locale, and version")
    public ResponseEntity<NoticeResponse> search(@Valid @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(noticeService.search(request));
    }

    @PostMapping("/_acknowledge")
    @Operation(summary = "Acknowledge a privacy notice")
    public ResponseEntity<NoticeResponse> acknowledge(@Valid @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(noticeService.acknowledge(request));
    }
}
