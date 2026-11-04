package org.upyog.dpdp.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.upyog.dpdp.engine.ClassificationEngine;
import org.upyog.dpdp.engine.RuleEngine;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.ScanJob;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiScannerTest {

    @Test
    void flagsUnauthenticatedPersonalDataEndpoint() {
        ApiScanner scanner = new ApiScanner(new ClassificationEngine(), new RuleEngine(new ObjectMapper()));
        ComplianceRule rule = ComplianceRule.builder()
                .code("DPDP-API-001")
                .name("Auth required")
                .category("API_SECURITY")
                .severity("CRITICAL")
                .enabled(true)
                .conditionJson("{\"all\":[{\"equals\":{\"path\":\"$.personalDataEndpoint\",\"value\":true}},{\"equals\":{\"path\":\"$.authentication\",\"value\":false}}]}")
                .recommendation("Authenticate")
                .build();
        ScanJob job = ScanJob.builder()
                .id("s1")
                .tenantId("pg.citya")
                .applicationCode("PT")
                .openApiSpec("{\"paths\":{\"/property/v1/_search\":{\"get\":{\"parameters\":[{\"name\":\"mobileNumber\"}]}}}}")
                .build();
        List<Finding> findings = scanner.scan(job, List.of(rule));
        assertTrue(findings.stream().anyMatch(f -> "UNAUTHENTICATED_PII_ENDPOINT".equals(f.getRuleCode())
                || "DPDP-API-001".equals(f.getRuleCode())));
    }
}
