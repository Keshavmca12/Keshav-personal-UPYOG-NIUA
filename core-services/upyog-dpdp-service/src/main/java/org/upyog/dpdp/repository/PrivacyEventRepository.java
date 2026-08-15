package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.PrivacyEvent;

@Repository
public class PrivacyEventRepository {

    private final JdbcTemplate jdbcTemplate;

    public PrivacyEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(PrivacyEvent event) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_privacy_event(id, tenant_id, actor_uuid, action, entity_type, entity_id, application_code, purpose_code, data_category, result, source_ip, detail, createdtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                event.getId(), event.getTenantId(), event.getActorUuid(), event.getAction(), event.getEntityType(),
                event.getEntityId(), event.getApplicationCode(), event.getPurposeCode(), event.getDataCategory(),
                event.getResult(), event.getSourceIp(), event.getDetail(), event.getCreatedTime());
    }
}
