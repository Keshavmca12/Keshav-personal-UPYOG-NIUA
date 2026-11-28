package org.upyog.dpdp.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.upyog.dpdp.web.models.Consent;

@Component
@Slf4j
public class NoOpConsentManagerAdapter implements ConsentManagerAdapter {

    @Override
    public void registerConsent(Consent consent) {
        log.debug("No-op consent manager register for consent {}", consent.getId());
    }

    @Override
    public void withdrawConsent(Consent consent) {
        log.debug("No-op consent manager withdraw for consent {}", consent.getId());
    }
}
