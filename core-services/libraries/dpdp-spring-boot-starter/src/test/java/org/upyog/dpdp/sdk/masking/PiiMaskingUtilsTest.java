package org.upyog.dpdp.sdk.masking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PiiMaskingUtilsTest {

    @Test
    void masksSensitiveValues() {
        assertEquals("******3210", PiiMaskingUtils.maskMobile("9876543210"));
        assertEquals("XXXXXX1234", PiiMaskingUtils.maskAadhaar("1234561234"));
        assertEquals("XXXXX1234", PiiMaskingUtils.maskPan("ABCDE1234"));
        assertEquals("******9999", PiiMaskingUtils.maskAccountNumber("1234569999"));
        assertEquals("j****@ulb.gov.in", PiiMaskingUtils.maskEmail("jane@ulb.gov.in"));
    }
}
