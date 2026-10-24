package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class DataInventoryValidationRepository {

    private final JdbcTemplate jdbcTemplate;

    public DataInventoryValidationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String tenantId, String applicationCode, String assetName, String fieldName,
                     String expectedCategory, String actualCategory, String status, String detail, String actor) {
        long now = System.currentTimeMillis();
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_data_inventory_validation(id, tenant_id, application_code, asset_name, field_name, expected_category, actual_category, status, detail, validated_at, createdby, createdtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                UUID.randomUUID().toString(), tenantId, applicationCode, assetName, fieldName, expectedCategory,
                actualCategory, status, detail, now, actor, now);
    }
}
