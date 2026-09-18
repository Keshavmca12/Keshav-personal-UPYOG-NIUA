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
import java.util.regex.Pattern;

@Component
public class LogScanner {

    private static final Pattern MOBILE = Pattern.compile("(?<!\\d)\\d{10}(?!\\d)");
    private static final Pattern AADHAAR = Pattern.compile("(?<!\\d)\\d{12}(?!\\d)");
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern SECRET = Pattern.compile("(?i)(otp|password|refresh_token|access_token|authToken)\\s*[:=]\\s*\\S+");

    private final RuleEngine ruleEngine;

    public LogScanner(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public List<Finding> scan(ScanJob job, List<ComplianceRule> rules) {
        List<Finding> findings = new ArrayList<>();
        if (job.getLogSamples() == null) {
            return findings;
        }
        for (String sample : job.getLogSamples()) {
            if (sample == null) {
                continue;
            }
            boolean pii = MOBILE.matcher(sample).find() || AADHAAR.matcher(sample).find()
                    || EMAIL.matcher(sample).find() || SECRET.matcher(sample).find();
            Map<String, Object> facts = new HashMap<>();
            facts.put("piiInLog", pii);
            facts.put("assetName", "application-log");
            facts.put("applicationCode", job.getApplicationCode());
            findings.addAll(ruleEngine.evaluate(job.getTenantId(), job.getId(), facts, rules));
        }
        return findings;
    }
}
