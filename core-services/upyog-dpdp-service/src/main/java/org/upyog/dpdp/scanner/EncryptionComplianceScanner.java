package org.upyog.dpdp.scanner;

import org.springframework.stereotype.Component;
import org.upyog.dpdp.engine.ClassificationEngine;
import org.upyog.dpdp.engine.RuleEngine;
import org.upyog.dpdp.web.models.ClassificationResult;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.ScanJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EncryptionComplianceScanner {

    private final ClassificationEngine classificationEngine;
    private final RuleEngine ruleEngine;

    public EncryptionComplianceScanner(ClassificationEngine classificationEngine, RuleEngine ruleEngine) {
        this.classificationEngine = classificationEngine;
        this.ruleEngine = ruleEngine;
    }

    public List<Finding> scan(ScanJob job, List<ComplianceRule> rules) {
        List<Finding> findings = new ArrayList<>();
        String spec = job.getOpenApiSpec() == null ? "" : job.getOpenApiSpec();
        for (String field : List.of("aadhaar", "pan", "mobileNumber", "email", "accountNumber")) {
            if (!spec.toLowerCase().contains(field.toLowerCase()) && job.getOpenApiSpec() != null) {
                continue;
            }
            ClassificationResult classified = classificationEngine.classify(field, null);
            Map<String, Object> facts = new HashMap<>();
            facts.put("personalData", classified.isPersonalData());
            facts.put("highRiskPersonal", classified.isHighRiskPersonal());
            facts.put("encrypted", spec.toLowerCase().contains("encrypt") || spec.toLowerCase().contains("enc-client"));
            facts.put("classification", classified.getClassification());
            facts.put("fieldName", field);
            facts.put("applicationCode", job.getApplicationCode());
            findings.addAll(ruleEngine.evaluate(job.getTenantId(), job.getId(), facts, rules));
        }
        return findings;
    }
}
