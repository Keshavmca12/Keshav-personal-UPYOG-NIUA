package org.upyog.dpdp.engine;

import org.junit.jupiter.api.Test;
import org.upyog.dpdp.web.models.ComplianceScore;
import org.upyog.dpdp.web.models.Finding;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringServiceTest {

    private final ScoringService scoringService = new ScoringService();

    @Test
    void perfectScoreWhenNoFindings() {
        ComplianceScore score = scoringService.score("pg.citya", List.of());
        assertEquals(0, score.getOpenFindings());
        assertEquals(new BigDecimal("100.00"), score.getOverallScore());
    }

    @Test
    void criticalFindingReducesEncryptionCategory() {
        Finding finding = Finding.builder().status("OPEN").category("ENCRYPTION").severity("CRITICAL").build();
        ComplianceScore score = scoringService.score("pg", List.of(finding));
        assertEquals(1, score.getCriticalFindings());
        assertEquals(new BigDecimal("75"), score.getCategoryScores().get("ENCRYPTION"));
        assertTrue(score.getOverallScore().compareTo(new BigDecimal("100")) < 0);
    }
}
