package org.upyog.dpdp.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.Finding;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleEngineTest {

    private final RuleEngine engine = new RuleEngine(new ObjectMapper());

    @Test
    void matchesConsentMissingRule() throws Exception {
        assertTrue(engine.matches(Map.of("processingBasis", "CONSENT", "consentPresent", false),
                "{\"all\":[{\"equals\":{\"path\":\"$.processingBasis\",\"value\":\"CONSENT\"}},{\"equals\":{\"path\":\"$.consentPresent\",\"value\":false}}]}"));
    }

    @Test
    void evaluatesToFinding() {
        ComplianceRule rule = ComplianceRule.builder()
                .code("DPDP-CNS-001")
                .name("Consent required")
                .category("CONSENT")
                .severity("HIGH")
                .enabled(true)
                .conditionJson("{\"all\":[{\"equals\":{\"path\":\"$.processingBasis\",\"value\":\"CONSENT\"}},{\"equals\":{\"path\":\"$.consentPresent\",\"value\":false}}]}")
                .build();
        List<Finding> findings = engine.evaluate("pg", "s1",
                Map.of("processingBasis", "CONSENT", "consentPresent", false), List.of(rule));
        assertFalse(findings.isEmpty());
    }
}
