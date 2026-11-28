package org.upyog.dpdp.engine;

import org.springframework.stereotype.Service;
import org.upyog.dpdp.web.models.ComplianceScore;
import org.upyog.dpdp.web.models.Finding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoringService {

    public static final Map<String, Integer> SEVERITY_WEIGHTS = Map.of(
            "CRITICAL", 25, "HIGH", 15, "MEDIUM", 8, "LOW", 3);
    public static final Map<String, BigDecimal> CATEGORY_WEIGHTS = new LinkedHashMap<>();

    static {
        CATEGORY_WEIGHTS.put("CLASSIFICATION", new BigDecimal("0.10"));
        CATEGORY_WEIGHTS.put("CONSENT", new BigDecimal("0.10"));
        CATEGORY_WEIGHTS.put("PURPOSE", new BigDecimal("0.08"));
        CATEGORY_WEIGHTS.put("MINIMIZATION", new BigDecimal("0.08"));
        CATEGORY_WEIGHTS.put("RETENTION", new BigDecimal("0.10"));
        CATEGORY_WEIGHTS.put("ENCRYPTION", new BigDecimal("0.12"));
        CATEGORY_WEIGHTS.put("API_SECURITY", new BigDecimal("0.12"));
        CATEGORY_WEIGHTS.put("ACCESS_CONTROL", new BigDecimal("0.12"));
        CATEGORY_WEIGHTS.put("AUDIT", new BigDecimal("0.10"));
        CATEGORY_WEIGHTS.put("INCIDENT", new BigDecimal("0.08"));
    }

    public ComplianceScore score(String tenantId, List<Finding> openFindings) {
        Map<String, BigDecimal> categoryScores = new LinkedHashMap<>();
        int open = 0;
        int critical = 0;
        int high = 0;
        for (String category : CATEGORY_WEIGHTS.keySet()) {
            int penalty = 0;
            if (openFindings != null) {
                for (Finding finding : openFindings) {
                    if (finding == null || !"OPEN".equals(finding.getStatus())
                            || !category.equals(finding.getCategory())) {
                        continue;
                    }
                    penalty += SEVERITY_WEIGHTS.getOrDefault(finding.getSeverity(), 0);
                }
            }
            categoryScores.put(category, BigDecimal.valueOf(Math.max(0, 100 - penalty)));
        }
        if (openFindings != null) {
            for (Finding finding : openFindings) {
                if (finding == null || !"OPEN".equals(finding.getStatus())) {
                    continue;
                }
                open++;
                if ("CRITICAL".equals(finding.getSeverity())) {
                    critical++;
                } else if ("HIGH".equals(finding.getSeverity())) {
                    high++;
                }
            }
        }
        BigDecimal weighted = BigDecimal.ZERO;
        BigDecimal weightSum = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : CATEGORY_WEIGHTS.entrySet()) {
            weighted = weighted.add(categoryScores.get(entry.getKey()).multiply(entry.getValue()));
            weightSum = weightSum.add(entry.getValue());
        }
        BigDecimal overall = weightSum.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : weighted.divide(weightSum, 2, RoundingMode.HALF_UP);
        return ComplianceScore.builder()
                .tenantId(tenantId)
                .overallScore(overall)
                .categoryScores(categoryScores)
                .openFindings(open)
                .criticalFindings(critical)
                .highRiskFindings(high)
                .formula("categoryScore=max(0,100-sum(count(severity)*weight)); overall=weightedMean(categoryScores)")
                .build();
    }
}
