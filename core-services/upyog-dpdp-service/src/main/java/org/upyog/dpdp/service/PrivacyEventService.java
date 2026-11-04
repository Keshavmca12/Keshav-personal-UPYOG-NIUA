package org.upyog.dpdp.service;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.producer.Producer;
import org.upyog.dpdp.repository.PrivacyEventRepository;
import org.upyog.dpdp.web.models.PrivacyEvent;

import java.util.UUID;

@Service
public class PrivacyEventService {

    private final PrivacyEventRepository repository;
    private final Producer producer;
    private final DpdpConfiguration configuration;

    public PrivacyEventService(PrivacyEventRepository repository, Producer producer, DpdpConfiguration configuration) {
        this.repository = repository;
        this.producer = producer;
        this.configuration = configuration;
    }

    public void emit(RequestInfo requestInfo, String tenantId, String action, String entityType, String entityId,
                     String application, String purpose, String category, String result, String topic) {
        PrivacyEvent event = PrivacyEvent.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .actorUuid(requestInfo != null && requestInfo.getUserInfo() != null
                        ? requestInfo.getUserInfo().getUuid() : null)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .applicationCode(application)
                .purposeCode(purpose)
                .dataCategory(category)
                .result(result)
                .createdTime(System.currentTimeMillis())
                .build();
        repository.insert(event);
        producer.push(topic == null ? configuration.getAuditEventTopic() : topic, event);
        if (configuration.isPersisterEnabled()) {
            producer.push(configuration.getSavePrivacyEventTopic(), java.util.Map.of("PrivacyEvent", event));
        }
    }
}
