package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.Nominee;

import java.util.List;

@Repository
public class NomineeRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Nominee> MAPPER = (rs, rowNum) -> Nominee.builder()
            .id(rs.getString("id"))
            .tenantId(rs.getString("tenant_id"))
            .dataPrincipalUuid(rs.getString("data_principal_uuid"))
            .nomineeName(rs.getString("nominee_name"))
            .nomineeContact(rs.getString("nominee_contact"))
            .relationship(rs.getString("relationship"))
            .status(rs.getString("status"))
            .createdBy(rs.getString("createdby"))
            .lastModifiedBy(rs.getString("lastmodifiedby"))
            .createdTime(rs.getLong("createdtime"))
            .lastModifiedTime(rs.getObject("lastmodifiedtime") == null ? null : rs.getLong("lastmodifiedtime"))
            .build();

    public NomineeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Nominee nominee) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_nominee(id, tenant_id, data_principal_uuid, nominee_name, nominee_contact, relationship, status, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                nominee.getId(), nominee.getTenantId(), nominee.getDataPrincipalUuid(), nominee.getNomineeName(),
                nominee.getNomineeContact(), nominee.getRelationship(), nominee.getStatus(), nominee.getCreatedBy(),
                nominee.getLastModifiedBy(), nominee.getCreatedTime(), nominee.getLastModifiedTime());
    }

    public List<Nominee> search(String tenantId, String principalUuid) {
        return jdbcTemplate.query(
                "SELECT * FROM ug_dpdp_nominee WHERE tenant_id=? AND data_principal_uuid=? ORDER BY createdtime DESC",
                MAPPER, tenantId, principalUuid);
    }
}
