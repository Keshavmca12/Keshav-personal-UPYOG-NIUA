package org.upyog.dpdp.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.upyog.dpdp.engine.RuleEngine;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.ScanJob;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogScannerTest {

    @Test
    void detectsRawMobileInLogSample() {
        LogScanner scanner = new LogScanner(new RuleEngine(new ObjectMapper()));
        ComplianceRule rule = ComplianceRule.builder()
                .code("DPDP-LOG-001")
                .name("PII in logs")
                .category("AUDIT")
                .severity("CRITICAL")
                .enabled(true)
                .conditionJson("{\"all\":[{\"equals\":{\"path\":\"$.piiInLog\",\"value\":true}}]}")
                .recommendation("Mask")
                .build();
        ScanJob job = ScanJob.builder()
                .id("s1")
                .tenantId("pg.citya")
                .scanType("LOG")
                .logSamples(List.of("user mobile 9876543210 saved"))
                .build();
        List<Finding> findings = scanner.scan(job, List.of(rule));
        assertFalse(findings.isEmpty());
        assertTrue(findings.stream().noneMatch(f -> f.getDescription() != null && f.getDescription().contains("9876543210")));
    }
}
