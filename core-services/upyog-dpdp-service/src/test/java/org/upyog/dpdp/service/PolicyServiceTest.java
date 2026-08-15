package org.upyog.dpdp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.upyog.dpdp.util.DpdpConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PolicyServiceTest {

    private final PolicyService policyService = new PolicyService(new ObjectMapper());

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
