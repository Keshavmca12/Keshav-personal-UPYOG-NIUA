package org.upyog.dpdp.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.upyog.dpdp.web.models.ComplianceRule;
import org.upyog.dpdp.web.models.ComplianceScore;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.PrivacyIncident;
import org.upyog.dpdp.web.models.ProcessingActivity;
import org.upyog.dpdp.web.models.ScanJob;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ScanRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ScanRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    private static final RowMapper<ProcessingActivity> ACTIVITY_MAPPER = (rs, rowNum) -> ProcessingActivity.builder()
            .id(rs.getString("id"))
            .tenantId(rs.getString("tenant_id"))
            .applicationCode(rs.getString("application_code"))
            .purposeCode(rs.getString("purpose_code"))
            .processingBasis(rs.getString("processing_basis"))
            .dataCategories(rs.getString("data_categories"))
            .retentionPolicyCode(rs.getString("retention_policy_code"))
            .legalHold(rs.getBoolean("legal_hold"))
            .status(rs.getString("status"))
            .expiresAt(rs.getObject("expires_at") == null ? null : rs.getLong("expires_at"))
            .createdBy(rs.getString("createdby"))
            .createdTime(rs.getLong("createdtime"))
            .build();

    private static final RowMapper<Finding> FINDING_MAPPER = (rs, rowNum) -> Finding.builder()
            .id(rs.getString("id"))
            .tenantId(rs.getString("tenant_id"))
            .scanId(rs.getString("scan_id"))
            .ruleCode(rs.getString("rule_code"))
            .category(rs.getString("category"))
            .severity(rs.getString("severity"))
            .status(rs.getString("status"))
            .applicationCode(rs.getString("application_code"))
            .assetName(rs.getString("asset_name"))
            .fieldName(rs.getString("field_name"))
            .classification(rs.getString("classification"))
            .title(rs.getString("title"))
            .description(rs.getString("description"))
            .recommendation(rs.getString("recommendation"))
            .createdTime(rs.getLong("createdtime"))
            .build();

    private static final RowMapper<ScanJob> SCAN_MAPPER = (rs, rowNum) -> ScanJob.builder()
            .id(rs.getString("id"))
            .tenantId(rs.getString("tenant_id"))
            .scanType(rs.getString("scan_type"))
            .applicationCode(rs.getString("application_code"))
            .status(rs.getString("status"))
            .startedAt(rs.getObject("started_at") == null ? null : rs.getLong("started_at"))
            .completedAt(rs.getObject("completed_at") == null ? null : rs.getLong("completed_at"))
            .findingCount(rs.getInt("finding_count"))
            .errorMessage(rs.getString("error_message"))
            .createdBy(rs.getString("createdby"))
            .createdTime(rs.getLong("createdtime"))
            .build();

    private static final RowMapper<PrivacyIncident> INCIDENT_MAPPER = (rs, rowNum) -> PrivacyIncident.builder()
            .id(rs.getString("id"))
            .incidentNumber(rs.getString("incident_number"))
            .tenantId(rs.getString("tenant_id"))
            .applicationCode(rs.getString("application_code"))
            .dataCategory(rs.getString("data_category"))
            .incidentType(rs.getString("incident_type"))
            .status(rs.getString("status"))
            .severity(rs.getString("severity"))
            .description(rs.getString("description"))
            .createdBy(rs.getString("createdby"))
            .createdTime(rs.getLong("createdtime"))
            .lastModifiedTime(rs.getObject("lastmodifiedtime") == null ? null : rs.getLong("lastmodifiedtime"))
            .build();

    public void insertActivity(ProcessingActivity activity) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_processing_activity(id, tenant_id, application_code, purpose_code, processing_basis, data_categories, retention_policy_code, legal_hold, status, expires_at, createdby, createdtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                activity.getId(), activity.getTenantId(), activity.getApplicationCode(), activity.getPurposeCode(),
                activity.getProcessingBasis(), activity.getDataCategories(), activity.getRetentionPolicyCode(),
                activity.isLegalHold(), activity.getStatus(), activity.getExpiresAt(), activity.getCreatedBy(),
                activity.getCreatedTime());
    }

    public List<ProcessingActivity> findExpired(long now) {
        return jdbcTemplate.query(
                "SELECT * FROM ug_dpdp_processing_activity WHERE status='ACTIVE' AND expires_at IS NOT NULL AND expires_at < ?",
                ACTIVITY_MAPPER, now);
    }

    public List<ProcessingActivity> searchActivities(String tenantId) {
        return jdbcTemplate.query("SELECT * FROM ug_dpdp_processing_activity WHERE tenant_id=? ORDER BY createdtime DESC",
                ACTIVITY_MAPPER, tenantId);
    }

    public void updateActivityStatus(String id, String tenantId, String status, long now) {
        jdbcTemplate.update(
                "UPDATE ug_dpdp_processing_activity SET status=?, lastmodifiedtime=? WHERE id=? AND tenant_id=?",
                status, now, id, tenantId);
    }

    public void insertScan(ScanJob job) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_scan(id, tenant_id, scan_type, application_code, asset_id, status, started_at, completed_at, finding_count, error_message, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                job.getId(), job.getTenantId(), job.getScanType(), job.getApplicationCode(), job.getAssetId(),
                job.getStatus(), job.getStartedAt(), job.getCompletedAt(), job.getFindingCount(), job.getErrorMessage(),
                job.getCreatedBy(), job.getCreatedBy(), job.getCreatedTime(), job.getCreatedTime());
    }

    public void updateScan(ScanJob job) {
        jdbcTemplate.update(
                "UPDATE ug_dpdp_scan SET status=?, completed_at=?, finding_count=?, error_message=?, lastmodifiedtime=? WHERE id=? AND tenant_id=?",
                job.getStatus(), job.getCompletedAt(), job.getFindingCount(), job.getErrorMessage(),
                System.currentTimeMillis(), job.getId(), job.getTenantId());
    }

    public void insertFinding(Finding finding) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_finding(id, tenant_id, scan_id, rule_code, category, severity, status, application_code, asset_name, field_name, classification, title, description, recommendation, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                finding.getId(), finding.getTenantId(), finding.getScanId(), finding.getRuleCode(), finding.getCategory(),
                finding.getSeverity(), finding.getStatus(), finding.getApplicationCode(), finding.getAssetName(),
                finding.getFieldName(), finding.getClassification(), finding.getTitle(), finding.getDescription(),
                finding.getRecommendation(), finding.getCreatedBy(), finding.getCreatedBy(), finding.getCreatedTime(),
                finding.getCreatedTime());
    }

    public List<Finding> searchFindings(String tenantId, String status) {
        if (status == null) {
            return jdbcTemplate.query("SELECT * FROM ug_dpdp_finding WHERE tenant_id=? ORDER BY createdtime DESC",
                    FINDING_MAPPER, tenantId);
        }
        return jdbcTemplate.query("SELECT * FROM ug_dpdp_finding WHERE tenant_id=? AND status=? ORDER BY createdtime DESC",
                FINDING_MAPPER, tenantId, status);
    }

    public List<ScanJob> searchScans(String tenantId) {
        return jdbcTemplate.query("SELECT * FROM ug_dpdp_scan WHERE tenant_id=? ORDER BY createdtime DESC",
                SCAN_MAPPER, tenantId);
    }

    public List<ComplianceRule> loadRules(String tenantId) {
        return jdbcTemplate.query(
                "SELECT rule_code, name, category, severity, enabled, condition_json, recommendation, reference FROM ug_dpdp_compliance_rule WHERE tenant_id=? AND enabled=TRUE",
                (rs, rowNum) -> ComplianceRule.builder()
                        .code(rs.getString("rule_code"))
                        .name(rs.getString("name"))
                        .category(rs.getString("category"))
                        .severity(rs.getString("severity"))
                        .enabled(rs.getBoolean("enabled"))
                        .conditionJson(rs.getString("condition_json"))
                        .recommendation(rs.getString("recommendation"))
                        .reference(rs.getString("reference"))
                        .build(),
                tenantId);
    }

    public void saveScoreSnapshot(ComplianceScore score) {
        String day = java.time.LocalDate.now(java.time.ZoneOffset.UTC).toString();
        String categories;
        try {
            categories = objectMapper.writeValueAsString(score.getCategoryScores());
        } catch (Exception ex) {
            categories = "{}";
        }
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_score_snapshot(id, tenant_id, snapshot_day, overall_score, category_scores, open_findings, critical_findings, high_findings, createdtime) VALUES (?,?,?,?,?,?,?,?,?) ON CONFLICT (tenant_id, snapshot_day) DO UPDATE SET overall_score=EXCLUDED.overall_score, category_scores=EXCLUDED.category_scores, open_findings=EXCLUDED.open_findings, critical_findings=EXCLUDED.critical_findings, high_findings=EXCLUDED.high_findings",
                UUID.randomUUID().toString(), score.getTenantId(), day, score.getOverallScore(), categories,
                score.getOpenFindings(), score.getCriticalFindings(), score.getHighRiskFindings(),
                System.currentTimeMillis());
    }

    public List<ComplianceScore> trends(String tenantId) {
        return jdbcTemplate.query(
                "SELECT tenant_id, snapshot_day, overall_score, open_findings, critical_findings, high_findings FROM ug_dpdp_score_snapshot WHERE tenant_id=? ORDER BY snapshot_day DESC LIMIT 30",
                (rs, rowNum) -> ComplianceScore.builder()
                        .tenantId(rs.getString("tenant_id"))
                        .snapshotDay(rs.getString("snapshot_day"))
                        .overallScore(rs.getBigDecimal("overall_score"))
                        .openFindings(rs.getInt("open_findings"))
                        .criticalFindings(rs.getInt("critical_findings"))
                        .highRiskFindings(rs.getInt("high_findings"))
                        .build(),
                tenantId);
    }

    public void insertIncident(PrivacyIncident incident) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_privacy_incident(id, incident_number, tenant_id, application_code, data_category, incident_type, status, severity, discovery_time, affected_records, containment_action, notification_status, description, createdby, lastmodifiedby, createdtime, lastmodifiedtime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                incident.getId(), incident.getIncidentNumber(), incident.getTenantId(), incident.getApplicationCode(),
                incident.getDataCategory(), incident.getIncidentType(), incident.getStatus(), incident.getSeverity(),
                incident.getDiscoveryTime(), incident.getAffectedRecords(), incident.getContainmentAction(),
                incident.getNotificationStatus(), incident.getDescription(), incident.getCreatedBy(),
                incident.getCreatedBy(), incident.getCreatedTime(), incident.getLastModifiedTime());
    }

    public void updateIncident(PrivacyIncident incident) {
        jdbcTemplate.update(
                "UPDATE ug_dpdp_privacy_incident SET status=?, containment_action=?, notification_status=?, lastmodifiedtime=? WHERE id=? AND tenant_id=?",
                incident.getStatus(), incident.getContainmentAction(), incident.getNotificationStatus(),
                incident.getLastModifiedTime(), incident.getId(), incident.getTenantId());
    }

    public List<PrivacyIncident> searchIncidents(String tenantId) {
        return jdbcTemplate.query("SELECT * FROM ug_dpdp_privacy_incident WHERE tenant_id=? ORDER BY createdtime DESC",
                INCIDENT_MAPPER, tenantId);
    }
}
