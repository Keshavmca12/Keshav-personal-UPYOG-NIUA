# DPDP Data Model

Prefix `ug_dpdp_`. Every row has `tenant_id` and audit columns. Flyway: `V20260815190000__create_dpdp_tables.sql`.

| Table | Purpose |
|-------|---------|
| `ug_dpdp_consent` | Versioned GRANT / WITHDRAW / EXPIRE / REVOKE |
| `ug_dpdp_consent_purpose` | Purpose/category link |
| `ug_dpdp_processing_activity` | Retention clock + legal hold |
| `ug_dpdp_retention_policy` | Tenant retention codes |
| `ug_dpdp_principal_request` | ACCESS / CORRECTION / ERASURE / GRIEVANCE |
| `ug_dpdp_grievance` | Grievance detail |
| `ug_dpdp_privacy_event` | Immutable insert-only privacy audit |
| `ug_dpdp_privacy_incident` | Breach / incident |
| `ug_dpdp_compliance_rule` | Seed + MDMS overlay |
| `ug_dpdp_application` / `ug_dpdp_data_asset` | Scan registry |
| `ug_dpdp_scan` / `ug_dpdp_finding` | Scan jobs and findings |
| `ug_dpdp_risk` / `ug_dpdp_remediation` | Risk and advisory |
| `ug_dpdp_score_snapshot` | Daily overall score |
| `shedlock` | Scheduler lock |

Do not create `dpdp_data_principal`. Reuse `userUuid`.
