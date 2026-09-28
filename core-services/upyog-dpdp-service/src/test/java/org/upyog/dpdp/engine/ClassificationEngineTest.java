package org.upyog.dpdp.engine;

import org.junit.jupiter.api.Test;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.ClassificationResult;
import org.upyog.dpdp.web.models.DataFieldPolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassificationEngineTest {

    private final ClassificationEngine engine = new ClassificationEngine();

    @Test
    void classifiesAadhaarAsHighRisk() {
        ClassificationResult result = engine.classify("aadhaar_number", null);
        assertEquals(DpdpConstants.CLASS_HIGH_RISK, result.getClassification());
        assertTrue(result.isHighRiskPersonal());
    }

    @Test
    void classifiesMobileAsPersonal() {
        ClassificationResult result = engine.classify("mobileNumber", null);
        assertEquals(DpdpConstants.CLASS_PERSONAL, result.getClassification());
        assertTrue(result.isPersonalData());
    }

    @Test
    void prefersConfiguredMetadata() {
        ClassificationResult result = engine.classify("customField",
                List.of(DataFieldPolicy.builder().field("customField").classification("FINANCIAL").build()));
        assertEquals("FINANCIAL", result.getClassification());
    }
}
