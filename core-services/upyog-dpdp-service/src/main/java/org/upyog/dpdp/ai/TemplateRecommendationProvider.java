package org.upyog.dpdp.ai;

import org.springframework.stereotype.Component;
import org.upyog.dpdp.web.models.Finding;

import java.util.ArrayList;
import java.util.List;

@Component
public class TemplateRecommendationProvider implements DpdpAiRecommendationProvider {

    @Override
    public List<String> recommend(List<Finding> findings) {
        List<String> recommendations = new ArrayList<>();
        if (findings == null || findings.isEmpty()) {
            recommendations.add("No open findings. Continue periodic scans and retain audit evidence.");
            return recommendations;
        }
        long critical = findings.stream().filter(f -> "CRITICAL".equals(f.getSeverity())).count();
        if (critical > 0) {
            recommendations.add("Prioritize " + critical
                    + " CRITICAL findings first. These typically indicate unauthenticated PII APIs, unprotected high-risk fields, or raw PII in logs.");
        }
        for (Finding finding : findings) {
            if (finding.getRecommendation() != null) {
                recommendations.add("[" + finding.getRuleCode() + "] " + finding.getRecommendation()
                        + " This is advisory and does not change the deterministic rule result.");
            }
        }
        recommendations.add("Do not send raw citizen PII to external models. Remediation must follow platform-approved encryption and MDMS policy.");
        return recommendations.stream().distinct().limit(20).toList();
    }
}
