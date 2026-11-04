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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ApiScanner {

    private static final Pattern PATH_PATTERN = Pattern.compile("(/[a-zA-Z0-9\\-_/]+)");
    private static final Pattern FIELD_PATTERN = Pattern.compile("\"([a-zA-Z0-9_]+)\"\\s*:");

    private final ClassificationEngine classificationEngine;
    private final RuleEngine ruleEngine;

    public ApiScanner(ClassificationEngine classificationEngine, RuleEngine ruleEngine) {
        this.classificationEngine = classificationEngine;
        this.ruleEngine = ruleEngine;
    }

    public List<Finding> scan(ScanJob job, List<ComplianceRule> rules) {
        List<Finding> findings = new ArrayList<>();
        String spec = job.getOpenApiSpec() == null ? "" : job.getOpenApiSpec();
        boolean authenticated = spec.toLowerCase().contains("bearer") || spec.toLowerCase().contains("authtoken")
                || spec.toLowerCase().contains("securityschemes");
        Matcher paths = PATH_PATTERN.matcher(spec);
        while (paths.find()) {
            String path = paths.group(1);
            Matcher fields = FIELD_PATTERN.matcher(spec);
            boolean personalEndpoint = false;
            while (fields.find()) {
                ClassificationResult classified = classificationEngine.classify(fields.group(1), null);
                if (classified.isPersonalData()) {
                    personalEndpoint = true;
                    Map<String, Object> facts = new HashMap<>();
                    facts.put("personalData", true);
                    facts.put("personalDataEndpoint", true);
                    facts.put("authentication", authenticated);
                    facts.put("authorization", authenticated);
                    facts.put("classification", classified.getClassification());
                    facts.put("fieldName", fields.group(1));
                    facts.put("assetName", path);
                    facts.put("applicationCode", job.getApplicationCode());
                    findings.addAll(ruleEngine.evaluate(job.getTenantId(), job.getId(), facts, rules));
                }
            }
            if (personalEndpoint && !authenticated) {
                findings.add(Finding.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .tenantId(job.getTenantId())
                        .scanId(job.getId())
                        .ruleCode("UNAUTHENTICATED_PII_ENDPOINT")
                        .category("API_SECURITY")
                        .severity("CRITICAL")
                        .status("OPEN")
                        .assetName(path)
                        .title("Personal data endpoint appears unauthenticated")
                        .recommendation("Require gateway authentication and accesscontrol action mapping.")
                        .createdTime(System.currentTimeMillis())
                        .build());
            }
        }
        return findings;
    }
}
