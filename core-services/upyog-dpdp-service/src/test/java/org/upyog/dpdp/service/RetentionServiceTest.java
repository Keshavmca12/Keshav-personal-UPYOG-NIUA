package org.upyog.dpdp.service;

import org.junit.jupiter.api.Test;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.ProcessingActivity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetentionServiceTest {

    @Test
    void skipsLegalHoldAndDefaultsToSoftDelete() {
        assertTrue(RetentionService.isOnLegalHold(ProcessingActivity.builder().legalHold(true).build()));
        assertFalse(RetentionService.isOnLegalHold(ProcessingActivity.builder().legalHold(false).build()));
        assertEquals(DpdpConstants.STATUS_SOFT_DELETED, RetentionService.statusForAction("SOFT_DELETE"));
        assertEquals(DpdpConstants.STATUS_SOFT_DELETED, RetentionService.statusForAction(null));
        assertEquals(DpdpConstants.STATUS_ARCHIVED, RetentionService.statusForAction("ARCHIVE"));
        assertEquals(DpdpConstants.STATUS_HARD_DELETED, RetentionService.statusForAction("HARD_DELETE"));
    }
}
