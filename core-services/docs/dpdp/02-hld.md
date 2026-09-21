# DPDP High-Level Design

DPDP is a platform capability (like MDMS, workflow, enc), not a municipal business module.

## Hybrid enforcement

1. **SDK interceptor** in the calling service for purpose / consent / PII-log protection (cached policy; not a remote call on every request).
2. **Core service** for consent records, rights, scans, scoring, incidents, dashboard APIs.
3. **Kafka** for audit, scan jobs, findings, retention, notifications.
4. **MDMS** for all State/ULB/module policy.

## Processing basis

Consent is required only when MDMS `processingBasis = CONSENT`. Statutory municipal workflows use `STATUTORY_FUNCTION` / `LEGAL_OBLIGATION` / `OTHER_PERMITTED_USE`. No hard-coded legal advice.

## Write / read path

- Writes: service validates → JDBC (source of truth). Optional Kafka persister when `dpdp.kafka.persister-enabled=true`.
- Domain events always published: `dpdp.consent.v1`, `dpdp.rights.v1`, `dpdp.retention.v1`, `dpdp.scan.v1`, `dpdp.compliance-finding.v1`, `dpdp.incident.v1`, `dpdp.audit.v1`.
- Reads: JDBC QueryBuilder + RowMapper.

## Tenancy and identity

Every row has `tenant_id`. Citizens are `egov-user` / `individual` (`userUuid` + `tenantId`). DPDP does not create a citizen master.
