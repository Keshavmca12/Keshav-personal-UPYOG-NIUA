package org.upyog.dpdp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.DataFieldPolicy;
import org.upyog.dpdp.web.models.PurposeDefinition;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PolicyService {

    private final List<PurposeDefinition> purposes;

    public PolicyService(ObjectMapper objectMapper) {
        List<PurposeDefinition> loaded = new ArrayList<>();
        try (InputStream in = new ClassPathResource("mdms/DPDP/Purpose.json").getInputStream()) {
            loaded = objectMapper.readValue(in, new TypeReference<List<PurposeDefinition>>() {
            });
        } catch (Exception ex) {
            log.warn("Could not load classpath DPDP Purpose.json: {}", ex.getMessage());
            loaded.add(PurposeDefinition.builder()
                    .code("PROPERTY_TAX_SERVICE")
                    .processingBasis(DpdpConstants.BASIS_STATUTORY_FUNCTION)
                    .applicationCode("PT")
                    .build());
            loaded.add(PurposeDefinition.builder()
                    .code("OPTIONAL_ALERTS")
                    .processingBasis(DpdpConstants.BASIS_CONSENT)
                    .applicationCode("PGR")
                    .build());
        }
        this.purposes = loaded;
    }

    public List<PurposeDefinition> getPurposes(String tenantId) {
        return purposes;
    }

    public PurposeDefinition findPurpose(String purposeCode) {
        if (StringUtils.isBlank(purposeCode)) {
            return null;
        }
        return purposes.stream()
                .filter(p -> purposeCode.equals(p.getCode()))
                .findFirst()
                .orElse(null);
    }

    public String resolveProcessingBasis(String tenantId, String purposeCode, String requested) {
        PurposeDefinition purpose = findPurpose(purposeCode);
        if (purpose != null && StringUtils.isNotBlank(purpose.getProcessingBasis())) {
            return purpose.getProcessingBasis();
        }
        return StringUtils.isBlank(requested) ? DpdpConstants.BASIS_CONSENT : requested;
    }

    public boolean consentRequired(String purposeCode) {
        return DpdpConstants.BASIS_CONSENT.equals(resolveProcessingBasis(null, purposeCode, null));
    }

    public Map<String, Object> evaluateMinimization(String tenantId, String purposeCode, List<String> collectedFields) {
        PurposeDefinition purpose = findPurpose(purposeCode);
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
}
