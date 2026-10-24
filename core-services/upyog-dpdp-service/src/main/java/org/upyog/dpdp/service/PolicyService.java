package org.upyog.dpdp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.DataFieldPolicy;
import org.upyog.dpdp.web.models.PurposeDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PolicyService {

    private final MdmsPolicyClient mdmsPolicyClient;
    private final Map<String, List<PurposeDefinition>> purposeCache = new ConcurrentHashMap<>();

    public PolicyService(MdmsPolicyClient mdmsPolicyClient) {
        this.mdmsPolicyClient = mdmsPolicyClient;
    }

    public List<PurposeDefinition> getPurposes(String tenantId) {
        String key = StringUtils.defaultIfBlank(tenantId, "default");
        return purposeCache.computeIfAbsent(key, mdmsPolicyClient::getPurposes);
    }

    public PurposeDefinition findPurpose(String tenantId, String purposeCode) {
        if (StringUtils.isBlank(purposeCode)) {
            return null;
        }
        return getPurposes(tenantId).stream()
                .filter(p -> purposeCode.equals(p.getCode()))
                .findFirst()
                .orElse(null);
    }

    public PurposeDefinition findPurpose(String purposeCode) {
        return findPurpose(null, purposeCode);
    }

    public String resolveProcessingBasis(String tenantId, String purposeCode, String requested) {
        PurposeDefinition purpose = findPurpose(tenantId, purposeCode);
        if (purpose != null && StringUtils.isNotBlank(purpose.getProcessingBasis())) {
            return purpose.getProcessingBasis();
        }
        return StringUtils.isBlank(requested) ? DpdpConstants.BASIS_CONSENT : requested;
    }

    public boolean consentRequired(String purposeCode) {
        return DpdpConstants.BASIS_CONSENT.equals(resolveProcessingBasis(null, purposeCode, null));
    }

    public Map<String, Object> evaluateMinimization(String tenantId, String purposeCode, List<String> collectedFields) {
        PurposeDefinition purpose = findPurpose(tenantId, purposeCode);
        List<String> extra = new ArrayList<>();
        List<String> justified = new ArrayList<>();
        if (purpose != null && purpose.getDataFields() != null && collectedFields != null) {
            for (String field : collectedFields) {
                DataFieldPolicy policy = purpose.getDataFields().stream()
                        .filter(f -> f.getField() != null && f.getField().equalsIgnoreCase(field))
                        .findFirst()
                        .orElse(null);
                if (policy == null || (!policy.isRequired() && !policy.isJustified())) {
                    extra.add(field);
                } else {
                    justified.add(field);
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("purposeCode", purposeCode);
        result.put("justifiedFields", justified);
        result.put("unjustifiedFields", extra);
        result.put("finding", extra.isEmpty() ? null : "JUSTIFICATION_REQUIRED");
        return result;
    }

    public void evictCache(String tenantId) {
        purposeCache.remove(StringUtils.defaultIfBlank(tenantId, "default"));
    }
}
