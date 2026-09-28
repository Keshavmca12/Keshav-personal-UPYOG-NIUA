package org.upyog.dpdp.producer;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Producer {

    private final CustomKafkaTemplate<String, Object> kafkaTemplate;

    public Producer(CustomKafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void push(String topic, Object value) {
        try {
            kafkaTemplate.send(topic, value);
        } catch (Exception ex) {
            log.warn("Failed to publish to topic {} (non-blocking): {}", topic, ex.getMessage());
        }
    }
}
