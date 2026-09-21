package org.upyog.dpdp.engine;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.ClassificationResult;
import org.upyog.dpdp.web.models.DataFieldPolicy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ClassificationEngine {

    private static final Map<String, String> DEFAULT_NAME_RULES = new LinkedHashMap<>();

    static {
        DEFAULT_NAME_RULES.put("aadhaar", DpdpConstants.CLASS_HIGH_RISK);
        DEFAULT_NAME_RULES.put("aadhar", DpdpConstants.CLASS_HIGH_RISK);
        DEFAULT_NAME_RULES.put("pan", DpdpConstants.CLASS_HIGH_RISK);
        DEFAULT_NAME_RULES.put("passport", DpdpConstants.CLASS_HIGH_RISK);
        DEFAULT_NAME_RULES.put("bankaccount", DpdpConstants.CLASS_FINANCIAL);
        DEFAULT_NAME_RULES.put("accountnumber", DpdpConstants.CLASS_FINANCIAL);
        DEFAULT_NAME_RULES.put("ifsc", DpdpConstants.CLASS_FINANCIAL);
        DEFAULT_NAME_RULES.put("mobilenumber", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("mobile", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("phone", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("email", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("address", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("fullname", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("firstname", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("lastname", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("dob", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("dateofbirth", DpdpConstants.CLASS_PERSONAL);
        DEFAULT_NAME_RULES.put("biometric", "BIOMETRIC");
        DEFAULT_NAME_RULES.put("otp", DpdpConstants.CLASS_HIGH_RISK);
        DEFAULT_NAME_RULES.put("password", DpdpConstants.CLASS_HIGH_RISK);
        DEFAULT_NAME_RULES.put("child", "CHILD_DATA");
    }

    public ClassificationResult classify(String fieldName, List<DataFieldPolicy> policies) {
        if (StringUtils.isBlank(fieldName)) {
            return ClassificationResult.builder()
                    .fieldName(fieldName)
                    .classification(DpdpConstants.CLASS_UNCLASSIFIED)
                    .build();
        }
        String normalized = fieldName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);

        if (policies != null) {
            for (DataFieldPolicy policy : policies) {
                if (policy.getField() != null && policy.getField().equalsIgnoreCase(fieldName)) {
                    return toResult(fieldName, policy.getClassification(), "metadata:" + policy.getField());
                }
                if (StringUtils.isNotBlank(policy.getPattern())
                        && Pattern.compile(policy.getPattern(), Pattern.CASE_INSENSITIVE).matcher(fieldName).find()) {
                    return toResult(fieldName, policy.getClassification(), "regex:" + policy.getPattern());
                }
            }
        }

        for (Map.Entry<String, String> entry : DEFAULT_NAME_RULES.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return toResult(fieldName, entry.getValue(), "name:" + entry.getKey());
            }
        }

        boolean looksPersonal = normalized.contains("name") || normalized.contains("user")
                || normalized.contains("citizen") || normalized.contains("owner");
        return ClassificationResult.builder()
                .fieldName(fieldName)
                .classification(looksPersonal ? DpdpConstants.CLASS_UNCLASSIFIED : null)
                .personalData(looksPersonal)
                .matchedRule(looksPersonal ? "heuristic" : null)
                .build();
    }

    public List<ClassificationResult> classifyAll(List<String> fieldNames, List<DataFieldPolicy> policies) {
        List<ClassificationResult> results = new ArrayList<>();
        if (fieldNames == null) {
            return results;
        }
        for (String fieldName : fieldNames) {
            results.add(classify(fieldName, policies));
        }
        return results;
    }

    private ClassificationResult toResult(String fieldName, String classification, String rule) {
        boolean personal = classification != null && !classification.equals("PUBLIC") && !classification.equals("INTERNAL");
        boolean highRisk = DpdpConstants.CLASS_HIGH_RISK.equals(classification)
                || "BIOMETRIC".equals(classification)
                || "CHILD_DATA".equals(classification)
                || DpdpConstants.CLASS_FINANCIAL.equals(classification);
        return ClassificationResult.builder()
                .fieldName(fieldName)
                .classification(classification)
                .personalData(personal)
                .highRiskPersonal(highRisk)
                .matchedRule(rule)
                .build();
    }
}
