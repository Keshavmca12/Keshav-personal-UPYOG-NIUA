package org.upyog.dpdp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PiiMaskerTest {

    @Test
    void masksMobileKeepingLastFour() {
        assertEquals("******3210", PiiMasker.maskMobile("9876543210"));
    }

    @Test
    void masksEmail() {
        assertEquals("j****@ulb.gov.in", PiiMasker.maskEmail("jane@ulb.gov.in"));
    }

    @Test
    void redactsSamples() {
        assertEquals("[REDACTED]", PiiMasker.redact("9876543210"));
        assertFalse(PiiMasker.redact("secret").contains("secret"));
    }
}
