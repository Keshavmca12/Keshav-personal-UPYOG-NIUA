package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.Consent;
import org.upyog.dpdp.web.models.ConsentSearchCriteria;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConsentRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Consent> MAPPER = (rs, rowNum) -> Consent.builder()
            .id(rs.getString("id"))
            .consentNumber(rs.getString("consent_number"))
            .tenantId(rs.getString("tenant_id"))
            .dataPrincipalUuid(rs.getString("data_principal_uuid"))
            .fiduciary(rs.getString("fiduciary"))
            .applicationCode(rs.getString("application_code"))
            .dataCategory(rs.getString("data_category"))
            .purposeCode(rs.getString("purpose_code"))
            .processingBasis(rs.getString("processing_basis"))
            .status(rs.getString("status"))
            .version(rs.getInt("version"))
            .source(rs.getString("source"))
            .language(rs.getString("language"))
            .grantedTime(rs.getObject("granted_time") == null ? null : rs.getLong("granted_time"))
            .withdrawalTime(rs.getObject("withdrawal_time") == null ? null : rs.getLong("withdrawal_time"))
            .expiryTime(rs.getObject("expiry_time") == null ? null : rs.getLong("expiry_time"))
            .noticeRef(rs.getString("notice_ref"))
            .createdBy(rs.getString("createdby"))
            .lastModifiedBy(rs.getString("lastmodifiedby"))
            .createdTime(rs.getLong("createdtime"))
            .lastModifiedTime(rs.getObject("lastmodifiedtime") == null ? null : rs.getLong("lastmodifiedtime"))
            .build();

    public ConsentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Consent consent) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_consent(id, consent_number, tenant_id, data_principal_uuid, fiduciary, application_code, data_category, purpose_code, processing_basis, status, version, source, language, granted_time, withdrawal_time, expiry_time, notice_ref, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                consent.getId(), consent.getConsentNumber(), consent.getTenantId(), consent.getDataPrincipalUuid(),
                consent.getFiduciary(), consent.getApplicationCode(), consent.getDataCategory(), consent.getPurposeCode(),
                consent.getProcessingBasis(), consent.getStatus(), consent.getVersion(), consent.getSource(),
                consent.getLanguage(), consent.getGrantedTime(), consent.getWithdrawalTime(), consent.getExpiryTime(),
                consent.getNoticeRef(), consent.getCreatedBy(), consent.getLastModifiedBy(), consent.getCreatedTime(),
                consent.getLastModifiedTime());
    }

    public void update(Consent consent) {
        jdbcTemplate.update(
                "UPDATE ug_dpdp_consent SET status=?, withdrawal_time=?, lastmodifiedby=?, lastmodifiedtime=? WHERE id=? AND tenant_id=?",
                consent.getStatus(), consent.getWithdrawalTime(), consent.getLastModifiedBy(),
                consent.getLastModifiedTime(), consent.getId(), consent.getTenantId());
    }

    public Consent findById(String id, String tenantId) {
        List<Consent> rows = jdbcTemplate.query(
                "SELECT * FROM ug_dpdp_consent WHERE id=? AND tenant_id=?", MAPPER, id, tenantId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<Consent> search(ConsentSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ug_dpdp_consent WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (criteria.getTenantId() != null) {
            sql.append(" AND tenant_id=?");
            params.add(criteria.getTenantId());
        }
        if (criteria.getDataPrincipalUuid() != null) {
            sql.append(" AND data_principal_uuid=?");
            params.add(criteria.getDataPrincipalUuid());
        }
        if (criteria.getPurposeCode() != null) {
            sql.append(" AND purpose_code=?");
            params.add(criteria.getPurposeCode());
        }
        if (criteria.getStatus() != null) {
            sql.append(" AND status=?");
            params.add(criteria.getStatus());
        }
        sql.append(" ORDER BY createdtime DESC");
        return jdbcTemplate.query(sql.toString(), MAPPER, params.toArray());
    }
}
