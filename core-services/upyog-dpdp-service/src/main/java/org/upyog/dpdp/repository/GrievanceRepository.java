package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.Grievance;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GrievanceRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Grievance> MAPPER = (rs, rowNum) -> Grievance.builder()
            .id(rs.getString("id"))
            .tenantId(rs.getString("tenant_id"))
            .requestId(rs.getString("request_id"))
            .dataPrincipalUuid(rs.getString("data_principal_uuid"))
            .subject(rs.getString("subject"))
            .description(rs.getString("description"))
            .status(rs.getString("status"))
            .slaDueAt(rs.getObject("sla_due_at") == null ? null : rs.getLong("sla_due_at"))
            .createdBy(rs.getString("createdby"))
            .lastModifiedBy(rs.getString("lastmodifiedby"))
            .createdTime(rs.getLong("createdtime"))
            .lastModifiedTime(rs.getObject("lastmodifiedtime") == null ? null : rs.getLong("lastmodifiedtime"))
            .build();

    public GrievanceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Grievance grievance) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_grievance(id, tenant_id, request_id, data_principal_uuid, subject, description, status, sla_due_at, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                grievance.getId(), grievance.getTenantId(), grievance.getRequestId(), grievance.getDataPrincipalUuid(),
                grievance.getSubject(), grievance.getDescription(), grievance.getStatus(), grievance.getSlaDueAt(),
                grievance.getCreatedBy(), grievance.getLastModifiedBy(), grievance.getCreatedTime(),
                grievance.getLastModifiedTime());
    }

    public List<Grievance> search(String tenantId, String principalUuid) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ug_dpdp_grievance WHERE tenant_id=?");
        List<Object> params = new ArrayList<>();
        params.add(tenantId);
        if (principalUuid != null) {
            sql.append(" AND data_principal_uuid=?");
            params.add(principalUuid);
        }
        sql.append(" ORDER BY createdtime DESC");
        return jdbcTemplate.query(sql.toString(), MAPPER, params.toArray());
    }
}
