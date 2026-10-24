package org.upyog.dpdp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.PurposeDefinition;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private MdmsPolicyClient mdmsPolicyClient;

    private PolicyService policyService;

    @BeforeEach
    void setUp() throws Exception {
        List<PurposeDefinition> purposes;
        try (InputStream in = new ClassPathResource("mdms/DPDP/Purpose.json").getInputStream()) {
            purposes = new ObjectMapper().readValue(in, new TypeReference<>() {
            });
        }
        when(mdmsPolicyClient.getPurposes(anyString())).thenReturn(purposes);
        policyService = new PolicyService(mdmsPolicyClient);
    }

    @Test
    void propertyTaxIsStatutory() {
        assertEquals(DpdpConstants.BASIS_STATUTORY_FUNCTION,
                policyService.resolveProcessingBasis("pg", "PROPERTY_TAX_SERVICE", "CONSENT"));
        assertFalse(policyService.consentRequired("PROPERTY_TAX_SERVICE"));
    }

    @Test
    void optionalAlertsRequireConsent() {
        assertEquals(DpdpConstants.BASIS_CONSENT,
                policyService.resolveProcessingBasis("pg", "OPTIONAL_ALERTS", null));
        assertTrue(policyService.consentRequired("OPTIONAL_ALERTS"));
    }

    @Test
    void flagsUnjustifiedAadhaarOnPropertyTax() {
        var result = policyService.evaluateMinimization("pg", "PROPERTY_TAX_SERVICE",
                java.util.List.of("name", "aadhaar"));
        assertEquals("JUSTIFICATION_REQUIRED", result.get("finding"));
    }
}
