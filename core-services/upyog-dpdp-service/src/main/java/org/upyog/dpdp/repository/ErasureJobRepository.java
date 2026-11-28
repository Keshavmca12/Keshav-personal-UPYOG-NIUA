package org.upyog.dpdp.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ErasureJobRepository {

    private final JdbcTemplate jdbcTemplate;

    public ErasureJobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String createJob(String tenantId, String activityId, String applicationCode, String action, String actor) {
        String jobId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_erasure_job(id, tenant_id, activity_id, application_code, status, action, started_at, createdby, createdtime) VALUES (?,?,?,?,?,?,?,?,?)",
                jobId, tenantId, activityId, applicationCode, "RUNNING", action, now, actor, now);
        return jobId;
    }

    public void addTask(String jobId, String tenantId, String providerCode, String status, String message) {
        jdbcTemplate.update(
                "INSERT INTO ug_dpdp_erasure_task(id, job_id, tenant_id, provider_code, status, message, createdtime) VALUES (?,?,?,?,?,?,?)",
                UUID.randomUUID().toString(), jobId, tenantId, providerCode, status, message,
                System.currentTimeMillis());
    }

    public void completeJob(String jobId, String tenantId, String status, String error) {
        jdbcTemplate.update(
                "UPDATE ug_dpdp_erasure_job SET status=?, completed_at=?, error_message=?, lastmodifiedtime=? WHERE id=? AND tenant_id=?",
                status, System.currentTimeMillis(), error, System.currentTimeMillis(), jobId, tenantId);
    }
}
