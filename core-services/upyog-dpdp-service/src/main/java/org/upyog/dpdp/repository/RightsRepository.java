package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.PrincipalRequest;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RightsRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<PrincipalRequest> MAPPER = (rs, rowNum) -> PrincipalRequest.builder()
            .id(rs.getString("id"))
            .requestNumber(rs.getString("request_number"))
            .tenantId(rs.getString("tenant_id"))
            .dataPrincipalUuid(rs.getString("data_principal_uuid"))
            .requestType(rs.getString("request_type"))
            .purposeCode(rs.getString("purpose_code"))
            .applicationCode(rs.getString("application_code"))
            .status(rs.getString("status"))
            .justification(rs.getString("justification"))
            .processInstanceId(rs.getString("process_instance_id"))
            .resultSummary(rs.getString("result_summary"))
            .createdBy(rs.getString("createdby"))
            .lastModifiedBy(rs.getString("lastmodifiedby"))
            .createdTime(rs.getLong("createdtime"))
            .lastModifiedTime(rs.getObject("lastmodifiedtime") == null ? null : rs.getLong("lastmodifiedtime"))
            .build();

    public RightsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(PrincipalRequest request) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_principal_request(id, request_number, tenant_id, data_principal_uuid, request_type, purpose_code, application_code, status, justification, process_instance_id, result_summary, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                request.getId(), request.getRequestNumber(), request.getTenantId(), request.getDataPrincipalUuid(),
                request.getRequestType(), request.getPurposeCode(), request.getApplicationCode(), request.getStatus(),
                request.getJustification(), request.getProcessInstanceId(), request.getResultSummary(),
                request.getCreatedBy(), request.getLastModifiedBy(), request.getCreatedTime(), request.getLastModifiedTime());
    }

    public List<PrincipalRequest> search(String tenantId, String principalUuid, String type) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ug_dpdp_principal_request WHERE tenant_id=?");
        List<Object> params = new ArrayList<>();
        params.add(tenantId);
        if (principalUuid != null) {
            sql.append(" AND data_principal_uuid=?");
            params.add(principalUuid);
        }
        if (type != null) {
            sql.append(" AND request_type=?");
            params.add(type);
        }
        sql.append(" ORDER BY createdtime DESC");
        return jdbcTemplate.query(sql.toString(), MAPPER, params.toArray());
    }
}
