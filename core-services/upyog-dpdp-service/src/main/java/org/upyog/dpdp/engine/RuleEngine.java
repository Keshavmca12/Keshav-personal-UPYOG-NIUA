package org.upyog.dpdp.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.Finding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class RuleEngine {

    private final ObjectMapper objectMapper;

    public RuleEngine(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Finding> evaluate(String tenantId, String scanId, Map<String, Object> facts, List<ComplianceRule> rules) {
        List<Finding> findings = new ArrayList<>();
        if (rules == null) {
            return findings;
        }
        for (ComplianceRule rule : rules) {
            if (rule == null || !rule.isEnabled()) {
                continue;
            }
            try {
                if (matches(facts, rule.getConditionJson())) {
                    findings.add(Finding.builder()
                            .id(UUID.randomUUID().toString())
                            .tenantId(tenantId)
                            .scanId(scanId)
                            .ruleCode(rule.getCode())
                            .category(rule.getCategory())
                            .severity(rule.getSeverity())
                            .status("OPEN")
                            .title(rule.getName())
                            .description("Rule " + rule.getCode() + " matched configured condition")
                            .recommendation(rule.getRecommendation())
                            .fieldName(asString(facts.get("fieldName")))
                            .classification(asString(facts.get("classification")))
                            .applicationCode(asString(facts.get("applicationCode")))
                            .assetName(asString(facts.get("assetName")))
                            .createdTime(System.currentTimeMillis())
                            .build());
                }
            } catch (Exception ex) {
                log.warn("Rule {} evaluation failed: {}", rule.getCode(), ex.getMessage());
            }
        }
        return findings;
    }

    boolean matches(Map<String, Object> facts, String conditionJson) throws Exception {
        JsonNode root = objectMapper.readTree(conditionJson);
        return evalNode(facts, root);
    }

    private boolean evalNode(Map<String, Object> facts, JsonNode node) {
        if (node.has("all")) {
            for (JsonNode child : node.get("all")) {
                if (!evalNode(facts, child)) {
                    return false;
                }
            }
            return true;
        }
        if (node.has("any")) {
            for (JsonNode child : node.get("any")) {
                if (evalNode(facts, child)) {
                    return true;
                }
            }
            return false;
        }
        if (node.has("equals")) {
            return compare(facts, node.get("equals"), true);
        }
        if (node.has("notEquals")) {
            return compare(facts, node.get("notEquals"), false);
        }
        if (node.has("isNull")) {
            Object value = read(facts, node.get("isNull").path("path").asText());
            return value == null;
        }
        if (node.has("isTrue")) {
            return Boolean.TRUE.equals(read(facts, node.get("isTrue").path("path").asText()));
        }
        if (node.has("isFalse")) {
            return Boolean.FALSE.equals(read(facts, node.get("isFalse").path("path").asText()));
        }
        if (node.has("exists")) {
            return read(facts, node.get("exists").path("path").asText()) != null;
        }
        return false;
    }

    private boolean compare(Map<String, Object> facts, JsonNode spec, boolean equals) {
        Object actual = read(facts, spec.path("path").asText());
        JsonNode expectedNode = spec.get("value");
        Object expected = expectedNode == null || expectedNode.isNull() ? null
                : expectedNode.isBoolean() ? expectedNode.asBoolean()
                : expectedNode.isNumber() ? expectedNode.numberValue()
                : expectedNode.asText();
        boolean match = actual == null ? expected == null : String.valueOf(actual).equals(String.valueOf(expected))
                || actual.equals(expected);
        if (actual instanceof Boolean && expected instanceof Boolean) {
            match = actual.equals(expected);
        }
        return equals == match;
    }

    private Object read(Map<String, Object> facts, String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        try {
            return JsonPath.read(facts, path);
        } catch (Exception ex) {
            return null;
        }
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
