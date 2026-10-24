package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.NoticeSearchCriteria;
import org.upyog.dpdp.web.models.PrivacyNotice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class NoticeRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<PrivacyNotice> MAPPER = (rs, rowNum) -> PrivacyNotice.builder()
            .id(rs.getString("id"))
            .tenantId(rs.getString("tenant_id"))
            .purposeCode(rs.getString("purpose_code"))
            .locale(rs.getString("locale"))
            .version(rs.getString("version"))
            .title(rs.getString("title"))
            .body(rs.getString("body"))
            .effectiveFrom(rs.getObject("effective_from") == null ? null : rs.getLong("effective_from"))
            .status(rs.getString("status"))
            .createdBy(rs.getString("createdby"))
            .createdTime(rs.getLong("createdtime"))
            .build();

    public NoticeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PrivacyNotice> search(NoticeSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ug_dpdp_notice WHERE tenant_id=?");
        List<Object> params = new ArrayList<>();
        params.add(criteria.getTenantId());
        if (criteria.getPurposeCode() != null) {
            sql.append(" AND purpose_code=?");
            params.add(criteria.getPurposeCode());
        }
        if (criteria.getLocale() != null) {
            sql.append(" AND locale=?");
            params.add(criteria.getLocale());
        }
        if (criteria.getVersion() != null) {
            sql.append(" AND version=?");
            params.add(criteria.getVersion());
        }
        sql.append(" ORDER BY createdtime DESC");
        return jdbcTemplate.query(sql.toString(), MAPPER, params.toArray());
    }

    public void upsert(PrivacyNotice notice) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_notice(id, tenant_id, purpose_code, locale, version, title, body, effective_from, status, createdby, createdtime) VALUES (?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT (tenant_id, purpose_code, locale, version) DO UPDATE SET title=EXCLUDED.title, body=EXCLUDED.body, status=EXCLUDED.status, lastmodifiedtime=EXCLUDED.createdtime",
                notice.getId(), notice.getTenantId(), notice.getPurposeCode(), notice.getLocale(), notice.getVersion(),
                notice.getTitle(), notice.getBody(), notice.getEffectiveFrom(), notice.getStatus(), notice.getCreatedBy(),
                notice.getCreatedTime());
    }

    public void acknowledge(String tenantId, String noticeId, String principalUuid, String channel, String actor) {
        long now = System.currentTimeMillis();
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_notice_ack(id, tenant_id, notice_id, data_principal_uuid, acknowledged_at, channel, createdby, createdtime) VALUES (?,?,?,?,?,?,?,?)",
                UUID.randomUUID().toString(), tenantId, noticeId, principalUuid, now, channel, actor, now);
    }
}
