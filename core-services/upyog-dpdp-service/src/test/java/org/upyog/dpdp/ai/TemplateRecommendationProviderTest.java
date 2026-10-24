package org.upyog.dpdp.ai;

import org.junit.jupiter.api.Test;
import org.upyog.dpdp.web.models.Finding;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateRecommendationProviderTest {

    @Test
    void isAdvisoryAndNeverEchoesRawPii() {
        TemplateRecommendationProvider provider = new TemplateRecommendationProvider();
        assertTrue(provider.recommend(List.of()).get(0).contains("No open findings"));
        List<String> recs = provider.recommend(List.of(Finding.builder()
                .ruleCode("DPDP-ENC-001")
                .severity("CRITICAL")
                .recommendation("Encrypt high-risk fields")
                .description("citizen mobile 9876543210")
                .build()));
        assertTrue(recs.stream().anyMatch(r -> r.contains("CRITICAL")));
        assertTrue(recs.stream().anyMatch(r -> r.contains("advisory")));
        assertFalse(recs.stream().anyMatch(r -> r.contains("9876543210")));
    }
}
