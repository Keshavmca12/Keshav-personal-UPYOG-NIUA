CREATE TABLE IF NOT EXISTS ug_dpdp_consent (
    id                    VARCHAR(64)  PRIMARY KEY,
    consent_number        VARCHAR(64),
    tenant_id             VARCHAR(64)  NOT NULL,
    data_principal_uuid   VARCHAR(64)  NOT NULL,
    fiduciary             VARCHAR(128),
    application_code      VARCHAR(64),
    data_category         VARCHAR(64),
    purpose_code          VARCHAR(64)  NOT NULL,
    processing_basis      VARCHAR(64)  NOT NULL,
    status                VARCHAR(32)  NOT NULL,
    version               INTEGER      NOT NULL DEFAULT 1,
    source                VARCHAR(64),
    language              VARCHAR(16),
    granted_time          BIGINT,
    withdrawal_time       BIGINT,
    expiry_time           BIGINT,
    notice_ref            VARCHAR(256),
    createdby             VARCHAR(64)  NOT NULL,
    lastmodifiedby        VARCHAR(64),
    createdtime           BIGINT       NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE INDEX IF NOT EXISTS idx_dpdp_consent_tenant_principal
    ON ug_dpdp_consent (tenant_id, data_principal_uuid, status);
CREATE INDEX IF NOT EXISTS idx_dpdp_consent_purpose
    ON ug_dpdp_consent (tenant_id, purpose_code, data_category);

CREATE TABLE IF NOT EXISTS ug_dpdp_consent_purpose (
    id             VARCHAR(64) PRIMARY KEY,
    consent_id     VARCHAR(64) NOT NULL,
    tenant_id      VARCHAR(64) NOT NULL,
    purpose_code   VARCHAR(64) NOT NULL,
    data_category  VARCHAR(64),
    CONSTRAINT fk_dpdp_consent_purpose FOREIGN KEY (consent_id) REFERENCES ug_dpdp_consent (id)
);

CREATE TABLE IF NOT EXISTS ug_dpdp_retention_policy (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    code             VARCHAR(64) NOT NULL,
    name             VARCHAR(256),
    retention_days   INTEGER     NOT NULL,
    retention_basis  VARCHAR(64),
    action           VARCHAR(32) NOT NULL DEFAULT 'SOFT_DELETE',
    legal_hold       BOOLEAN     NOT NULL DEFAULT FALSE,
    archive_policy   VARCHAR(64),
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT,
    CONSTRAINT uq_dpdp_retention UNIQUE (tenant_id, code)
);

CREATE TABLE IF NOT EXISTS ug_dpdp_processing_activity (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    application_code      VARCHAR(64) NOT NULL,
    purpose_code          VARCHAR(64) NOT NULL,
    processing_basis      VARCHAR(64) NOT NULL,
    data_categories       VARCHAR(512),
    retention_policy_code VARCHAR(64),
    legal_hold            BOOLEAN     NOT NULL DEFAULT FALSE,
    status                VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    expires_at            BIGINT,
    createdby             VARCHAR(64) NOT NULL,
    lastmodifiedby        VARCHAR(64),
    createdtime           BIGINT      NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE INDEX IF NOT EXISTS idx_dpdp_activity_expiry
    ON ug_dpdp_processing_activity (tenant_id, status, expires_at);

CREATE TABLE IF NOT EXISTS ug_dpdp_principal_request (
    id                    VARCHAR(64) PRIMARY KEY,
    request_number        VARCHAR(64),
    tenant_id             VARCHAR(64) NOT NULL,
    data_principal_uuid   VARCHAR(64) NOT NULL,
    request_type          VARCHAR(32) NOT NULL,
    purpose_code          VARCHAR(64),
    application_code      VARCHAR(64),
    status                VARCHAR(32) NOT NULL,
    justification         VARCHAR(1024),
    process_instance_id   VARCHAR(64),
    result_summary        TEXT,
    createdby             VARCHAR(64) NOT NULL,
    lastmodifiedby        VARCHAR(64),
    createdtime           BIGINT      NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE INDEX IF NOT EXISTS idx_dpdp_rights_tenant
    ON ug_dpdp_principal_request (tenant_id, data_principal_uuid, request_type);

CREATE TABLE IF NOT EXISTS ug_dpdp_grievance (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    request_id            VARCHAR(64),
    data_principal_uuid   VARCHAR(64) NOT NULL,
    subject               VARCHAR(256),
    description           TEXT,
    status                VARCHAR(32) NOT NULL,
    createdby             VARCHAR(64) NOT NULL,
    lastmodifiedby        VARCHAR(64),
    createdtime           BIGINT      NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_privacy_event (
    id                    VARCHAR(64) PRIMARY KEY,
    tenant_id             VARCHAR(64) NOT NULL,
    actor_uuid            VARCHAR(64),
    action                VARCHAR(64) NOT NULL,
    entity_type           VARCHAR(64),
    entity_id             VARCHAR(64),
    application_code      VARCHAR(64),
    purpose_code          VARCHAR(64),
    data_category         VARCHAR(64),
    result                VARCHAR(32),
    source_ip             VARCHAR(64),
    detail                TEXT,
    createdtime           BIGINT      NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_dpdp_privacy_event_tenant
    ON ug_dpdp_privacy_event (tenant_id, createdtime DESC);

CREATE TABLE IF NOT EXISTS ug_dpdp_privacy_incident (
    id                    VARCHAR(64) PRIMARY KEY,
    incident_number       VARCHAR(64),
    tenant_id             VARCHAR(64) NOT NULL,
    application_code      VARCHAR(64),
    data_category         VARCHAR(64),
    incident_type         VARCHAR(64),
    status                VARCHAR(32) NOT NULL,
    severity              VARCHAR(16) NOT NULL,
    discovery_time        BIGINT,
    affected_records      INTEGER,
    containment_action    VARCHAR(512),
    notification_status   VARCHAR(64),
    description           TEXT,
    createdby             VARCHAR(64) NOT NULL,
    lastmodifiedby        VARCHAR(64),
    createdtime           BIGINT      NOT NULL,
    lastmodifiedtime      BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_compliance_rule (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    rule_code        VARCHAR(64) NOT NULL,
    name             VARCHAR(256),
    category         VARCHAR(64) NOT NULL,
    severity         VARCHAR(16) NOT NULL,
    enabled          BOOLEAN     NOT NULL DEFAULT TRUE,
    condition_json   TEXT        NOT NULL,
    recommendation   TEXT,
    reference        VARCHAR(256),
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT,
    CONSTRAINT uq_dpdp_rule UNIQUE (tenant_id, rule_code)
);

CREATE TABLE IF NOT EXISTS ug_dpdp_application (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    application_code VARCHAR(64) NOT NULL,
    service_name     VARCHAR(128),
    base_url         VARCHAR(512),
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT,
    CONSTRAINT uq_dpdp_application UNIQUE (tenant_id, application_code)
);

CREATE TABLE IF NOT EXISTS ug_dpdp_data_asset (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    application_code VARCHAR(64),
    asset_type       VARCHAR(32) NOT NULL,
    name             VARCHAR(256) NOT NULL,
    jdbc_url         VARCHAR(512),
    schema_name      VARCHAR(128),
    contract_ref     VARCHAR(512),
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_scan (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    scan_type        VARCHAR(32) NOT NULL,
    application_code VARCHAR(64),
    asset_id         VARCHAR(64),
    status           VARCHAR(32) NOT NULL,
    started_at       BIGINT,
    completed_at     BIGINT,
    finding_count    INTEGER DEFAULT 0,
    error_message    VARCHAR(1024),
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT
);

CREATE INDEX IF NOT EXISTS idx_dpdp_scan_tenant ON ug_dpdp_scan (tenant_id, status, createdtime DESC);

CREATE TABLE IF NOT EXISTS ug_dpdp_finding (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    scan_id          VARCHAR(64),
    rule_code        VARCHAR(64),
    category         VARCHAR(64) NOT NULL,
    severity         VARCHAR(16) NOT NULL,
    status           VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    application_code VARCHAR(64),
    asset_name       VARCHAR(256),
    field_name       VARCHAR(256),
    classification   VARCHAR(64),
    title            VARCHAR(512) NOT NULL,
    description      TEXT,
    recommendation   TEXT,
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT
);

CREATE INDEX IF NOT EXISTS idx_dpdp_finding_tenant
    ON ug_dpdp_finding (tenant_id, status, severity, category);

CREATE TABLE IF NOT EXISTS ug_dpdp_risk (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    finding_id       VARCHAR(64),
    category         VARCHAR(64),
    severity         VARCHAR(16) NOT NULL,
    title            VARCHAR(512) NOT NULL,
    status           VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_remediation (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    finding_id       VARCHAR(64),
    source           VARCHAR(32) NOT NULL,
    recommendation   TEXT        NOT NULL,
    priority         VARCHAR(16),
    createdby        VARCHAR(64) NOT NULL,
    lastmodifiedby   VARCHAR(64),
    createdtime      BIGINT      NOT NULL,
    lastmodifiedtime BIGINT
);

CREATE TABLE IF NOT EXISTS ug_dpdp_score_snapshot (
    id               VARCHAR(64) PRIMARY KEY,
    tenant_id        VARCHAR(64) NOT NULL,
    snapshot_day     VARCHAR(16) NOT NULL,
    overall_score    NUMERIC(5,2) NOT NULL,
    category_scores  TEXT,
    open_findings    INTEGER,
    critical_findings INTEGER,
    high_findings    INTEGER,
    createdtime      BIGINT      NOT NULL,
    CONSTRAINT uq_dpdp_score_day UNIQUE (tenant_id, snapshot_day)
);

CREATE TABLE IF NOT EXISTS shedlock (
    name       VARCHAR(64)  NOT NULL PRIMARY KEY,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL
);
