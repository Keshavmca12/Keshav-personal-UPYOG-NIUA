ALTER TABLE ug_dpdp_consent
    ADD COLUMN IF NOT EXISTS guardian_uuid VARCHAR(64),
    ADD COLUMN IF NOT EXISTS is_child_data BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS consent_manager_id VARCHAR(128),
    ADD COLUMN IF NOT EXISTS notice_version VARCHAR(32),
    ADD COLUMN IF NOT EXISTS channel VARCHAR(32);

ALTER TABLE ug_dpdp_principal_request
    ADD COLUMN IF NOT EXISTS sla_due_at BIGINT,
    ADD COLUMN IF NOT EXISTS correction_field VARCHAR(128),
    ADD COLUMN IF NOT EXISTS correction_value VARCHAR(1024),
    ADD COLUMN IF NOT EXISTS workflow_state VARCHAR(64);

ALTER TABLE ug_dpdp_privacy_incident
    ADD COLUMN IF NOT EXISTS awareness_time BIGINT,
    ADD COLUMN IF NOT EXISTS board_initial_at BIGINT,
    ADD COLUMN IF NOT EXISTS board_report_due_at BIGINT,
    ADD COLUMN IF NOT EXISTS cert_in_flagged_at BIGINT,
    ADD COLUMN IF NOT EXISTS cert_in_required BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS cause TEXT,
    ADD COLUMN IF NOT EXISTS remediation TEXT,
    ADD COLUMN IF NOT EXISTS affected_principals_count INTEGER;

ALTER TABLE ug_dpdp_grievance
    ADD COLUMN IF NOT EXISTS sla_due_at BIGINT;

CREATE TABLE IF NOT EXISTS ug_dpdp_notice (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    purpose_code     VARCHAR(64) NOT NULL,
    locale           VARCHAR(16) NOT NULL DEFAULT 'en_IN',
    version          VARCHAR(32) NOT NULL,
    title            VARCHAR(512),
    body             TEXT,
    effective_from   BIGINT,
    status           VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT NOT NULL,
    lastmodifiedtime BIGINT,
    CONSTRAINT uq_dpdp_notice UNIQUE (tenant_id, purpose_code, locale, version)
);

CREATE TABLE IF NOT EXISTS ug_dpdp_notice_ack (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    notice_id             VARCHAR(64) NOT NULL,
    data_principal_uuid   VARCHAR(64) NOT NULL,
    acknowledged_at       BIGINT NOT NULL,
    channel               VARCHAR(32),
    createdby             VARCHAR(64) NOT NULL,
    createdtime           BIGINT NOT NULL,
    CONSTRAINT fk_dpdp_notice_ack FOREIGN KEY (notice_id) REFERENCES ug_dpdp_notice (id)
);

CREATE INDEX IF NOT EXISTS idx_dpdp_notice_ack_principal
    ON ug_dpdp_notice_ack (tenant_id, data_principal_uuid);

CREATE TABLE IF NOT EXISTS ug_dpdp_nominee (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    data_principal_uuid   VARCHAR(64) NOT NULL,
    nominee_name          VARCHAR(256) NOT NULL,
    nominee_contact       VARCHAR(128),
    relationship          VARCHAR(64),
    status                VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    createdby             VARCHAR(64) NOT NULL,
    lastmodifiedby        VARCHAR(64),
    createdtime           BIGINT NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_guardian (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    child_uuid            VARCHAR(64) NOT NULL,
    guardian_uuid         VARCHAR(64) NOT NULL,
    relationship          VARCHAR(64),
    verified              BOOLEAN NOT NULL DEFAULT FALSE,
    createdby             VARCHAR(64) NOT NULL,
    lastmodifiedby        VARCHAR(64),
    createdtime           BIGINT NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_access_log (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    actor_uuid            VARCHAR(64),
    data_principal_uuid   VARCHAR(64),
    application_code      VARCHAR(64),
    purpose_code          VARCHAR(64),
    action                VARCHAR(64) NOT NULL,
    resource              VARCHAR(256),
    source_ip             VARCHAR(64),
    createdtime           BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_dpdp_access_log_tenant
    ON ug_dpdp_access_log (tenant_id, createdtime DESC);

CREATE TABLE IF NOT EXISTS ug_dpdp_erasure_job (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    activity_id           VARCHAR(64),
    application_code      VARCHAR(64),
    status                VARCHAR(32) NOT NULL,
    action                VARCHAR(32) NOT NULL,
    started_at            BIGINT,
    completed_at          BIGINT,
    error_message         VARCHAR(1024),
    createdby             VARCHAR(64) NOT NULL,
    createdtime           BIGINT NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_erasure_task (
    id                    VARCHAR(64) PRIMARY KEY,
    job_id                VARCHAR(64) NOT NULL,
    tenant_id             VARCHAR(64) NOT NULL,
    provider_code         VARCHAR(64) NOT NULL,
    status                VARCHAR(32) NOT NULL,
    message               VARCHAR(1024),
    createdtime           BIGINT NOT NULL,
    lastmodifiedtime      BIGINT,
    CONSTRAINT fk_dpdp_erasure_task FOREIGN KEY (job_id) REFERENCES ug_dpdp_erasure_job (id)
);

CREATE TABLE IF NOT EXISTS ug_dpdp_data_inventory_validation (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    application_code      VARCHAR(64) NOT NULL,
    asset_name            VARCHAR(256),
    field_name            VARCHAR(256),
    expected_category     VARCHAR(64),
    actual_category       VARCHAR(64),
    status                VARCHAR(32) NOT NULL,
    detail                TEXT,
    validated_at          BIGINT NOT NULL,
    createdby             VARCHAR(64) NOT NULL,
    createdtime           BIGINT NOT NULL
);
