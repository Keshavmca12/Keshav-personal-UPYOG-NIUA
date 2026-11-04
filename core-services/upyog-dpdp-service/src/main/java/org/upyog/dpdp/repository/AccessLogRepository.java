package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class AccessLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccessLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(String tenantId, String actorUuid, String principalUuid, String applicationCode,
                       String purposeCode, String action, String resource, String sourceIp) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_access_log(id, tenant_id, actor_uuid, data_principal_uuid, application_code, purpose_code, action, resource, source_ip, createdtime) VALUES (?,?,?,?,?,?,?,?,?,?)",
                UUID.randomUUID().toString(), tenantId, actorUuid, principalUuid, applicationCode, purposeCode,
                action, resource, sourceIp, System.currentTimeMillis());
    }
}
