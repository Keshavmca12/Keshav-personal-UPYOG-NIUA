package org.upyog.dpdp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Component
@Getter
public class DpdpConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @Value("${dpdp.default.offset}")
    private int defaultOffset;

    @Value("${dpdp.default.limit}")
    private int defaultLimit;

    @Value("${dpdp.max.limit}")
    private int maxLimit;

    @Value("${dpdp.kafka.persister-enabled}")
    private boolean persisterEnabled;

    @Value("${persister.save.consent.topic}")
    private String saveConsentTopic;

    @Value("${persister.update.consent.topic}")
    private String updateConsentTopic;

    @Value("${persister.save.privacy.event.topic}")
    private String savePrivacyEventTopic;

    @Value("${persister.save.principal.request.topic}")
    private String savePrincipalRequestTopic;

    @Value("${persister.update.principal.request.topic}")
    private String updatePrincipalRequestTopic;

    @Value("${persister.save.scan.topic}")
    private String saveScanTopic;

    @Value("${persister.update.scan.topic}")
    private String updateScanTopic;

    @Value("${persister.save.finding.topic}")
    private String saveFindingTopic;

    @Value("${persister.save.incident.topic}")
    private String saveIncidentTopic;

    @Value("${persister.update.incident.topic}")
    private String updateIncidentTopic;

    @Value("${dpdp.event.consent.topic}")
    private String consentEventTopic;

    @Value("${dpdp.event.rights.topic}")
    private String rightsEventTopic;

    @Value("${dpdp.event.retention.topic}")
    private String retentionEventTopic;

    @Value("${dpdp.event.scan.topic}")
    private String scanEventTopic;

    @Value("${dpdp.event.finding.topic}")
    private String findingEventTopic;

    @Value("${dpdp.event.incident.topic}")
    private String incidentEventTopic;

    @Value("${dpdp.event.audit.topic}")
    private String auditEventTopic;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsSearchEndpoint;

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.search.endpoint}")
    private String userSearchEndpoint;

    @Value("${retention.default.action}")
    private String defaultRetentionAction;

    @Value("${scheduler.retention.enabled}")
    private boolean retentionSchedulerEnabled;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}
