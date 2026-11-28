package org.upyog.dpdp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.web.models.PrivacyIncident;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BreachNotificationService {

    private final MdmsPolicyClient mdmsPolicyClient;
    private final DpdpConfiguration configuration;

    public BreachNotificationService(MdmsPolicyClient mdmsPolicyClient, DpdpConfiguration configuration) {
        this.mdmsPolicyClient = mdmsPolicyClient;
        this.configuration = configuration;
    }

    public void notifyBoardIfDue(PrivacyIncident incident) {
        if (incident.getBoardReportDueAt() == null || incident.getBoardReportDueAt() > System.currentTimeMillis()) {
            return;
        }
        List<Map<String, Object>> contacts = mdmsPolicyClient.getFiduciaryContact(incident.getTenantId());
        String email = contacts.isEmpty() ? "board@example.org"
                : String.valueOf(contacts.get(0).getOrDefault("boardReportingEmail", "board@example.org"));
        log.warn("DPDP board breach notification due for incident {} — notify {}", incident.getId(), email);
        incident.setNotificationStatus(DpdpConstants.INCIDENT_BOARD_NOTIFIED);
        incident.setBoardInitialAt(System.currentTimeMillis());
    }

    public void flagCertInIfRequired(PrivacyIncident incident) {
        if (!Boolean.TRUE.equals(incident.getCertInRequired())) {
            return;
        }
        if (incident.getCertInFlaggedAt() != null) {
            return;
        }
        log.warn("DPDP CERT-In notification required for incident {}", incident.getId());
        incident.setCertInFlaggedAt(System.currentTimeMillis());
        incident.setNotificationStatus(DpdpConstants.INCIDENT_CERT_IN_PENDING);
    }
}
