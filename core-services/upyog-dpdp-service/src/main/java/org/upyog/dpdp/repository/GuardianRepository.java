package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.GuardianLink;

import java.util.List;

@Repository
public class GuardianRepository {

    private static final RowMapper<GuardianLink> ROW_MAPPER = (rs, rowNum) -> GuardianLink.builder()
            .id(rs.getString("id"))
            .tenantId(rs.getString("tenant_id"))
            .childUuid(rs.getString("child_uuid"))
            .guardianUuid(rs.getString("guardian_uuid"))
            .relationship(rs.getString("relationship"))
            .verificationMethod(rs.getString("verification_method"))
            .authorityOrderRef(rs.getString("authority_order_ref"))
            .verified(rs.getBoolean("verified"))
            .createdBy(rs.getString("createdby"))
            .createdTime(rs.getLong("createdtime"))
            .lastModifiedTime(rs.getLong("lastmodifiedtime"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public GuardianRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(GuardianLink link) {
        jdbcTemplate.update("""
                INSERT INTO ug_dpdp_guardian(id, tenant_id, child_uuid, guardian_uuid, relationship,
                    verification_method, authority_order_ref, verified, createdby, lastmodifiedby,
                    createdtime, lastmodifiedtime)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                link.getId(), link.getTenantId(), link.getChildUuid(), link.getGuardianUuid(),
                link.getRelationship(), link.getVerificationMethod(), link.getAuthorityOrderRef(),
                link.isVerified(), link.getCreatedBy(), link.getCreatedBy(),
                link.getCreatedTime(), link.getLastModifiedTime());
    }

    public void updateVerification(GuardianLink link) {
        jdbcTemplate.update("""
                UPDATE ug_dpdp_guardian SET verified = ?, verification_method = ?, authority_order_ref = ?,
                    lastmodifiedby = ?, lastmodifiedtime = ? WHERE id = ? AND tenant_id = ?
                """,
                link.isVerified(), link.getVerificationMethod(), link.getAuthorityOrderRef(),
                link.getCreatedBy(), link.getLastModifiedTime(), link.getId(), link.getTenantId());
    }

    public List<GuardianLink> search(String tenantId, String childUuid) {
        return jdbcTemplate.query("""
                SELECT * FROM ug_dpdp_guardian WHERE tenant_id = ? AND child_uuid = ?
                ORDER BY createdtime DESC
                """, ROW_MAPPER, tenantId, childUuid);
    }
}
