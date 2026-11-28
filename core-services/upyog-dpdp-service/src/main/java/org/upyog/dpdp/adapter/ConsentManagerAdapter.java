package org.upyog.dpdp.adapter;

import org.upyog.dpdp.web.models.Consent;

public interface ConsentManagerAdapter {

    void registerConsent(Consent consent);

    void withdrawConsent(Consent consent);
}
