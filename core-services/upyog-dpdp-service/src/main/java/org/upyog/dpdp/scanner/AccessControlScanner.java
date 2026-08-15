package org.upyog.dpdp.scanner;

import org.springframework.stereotype.Component;
import org.upyog.dpdp.engine.RuleEngine;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.ScanJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccessControlScanner {

    private final RuleEngine ruleEngine;

    public AccessControlScanner(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public List<Finding> scan(ScanJob job, List<ComplianceRule> rules) {
        List<Finding> findings = new ArrayList<>();
        String spec = job.getOpenApiSpec() == null ? "" : job.getOpenApiSpec();
        boolean hasAuthz = spec.contains("accesscontrol") || spec.contains("role-action") || spec.contains("authorization");
        Map<String, Object> facts = new HashMap<>();
        facts.put("personalDataEndpoint", spec.toLowerCase().contains("mobile") || spec.toLowerCase().contains("aadhaar"));
        facts.put("authorization", hasAuthz);
        facts.put("authentication", spec.toLowerCase().contains("authtoken") || spec.toLowerCase().contains("bearer"));
        facts.put("applicationCode", job.getApplicationCode());
        findings.addAll(ruleEngine.evaluate(job.getTenantId(), job.getId(), facts, rules));
        return findings;
    }
}
