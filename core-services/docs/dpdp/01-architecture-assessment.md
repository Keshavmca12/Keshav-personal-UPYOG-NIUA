# DPDP Architecture Assessment

**Service:** `upyog-dpdp-service`  
**SDK:** `dpdp-spring-boot-starter`  
**Baseline:** Java 17, Spring Boot 3.2.2, JDBC + Flyway, Kafka via `CustomKafkaTemplate`, POST `/resource/v1/_verb`, `RequestInfo` / `ResponseInfo`.

## Reuse as-is

- Contracts: `RequestInfo`, `ResponseInfo`, `AuditDetails`, `CustomException`
- Auth/RBAC: `egov-user` OAuth2/JWT, gateway AuthFilter + RbacPreCheckFilter + `egov-accesscontrol`
- Encryption/masking: `egov-enc-service` + `enc-client` + MDMS `DataSecurity`
- Audit: `audit-service` signed CRUD logs; DPDP adds privacy-event model
- Config: MDMS module `DPDP` alongside existing `DataSecurity`
- Workflow: `egov-workflow-v2` for rights and incident lifecycles
- Notify: existing SMS/mail Kafka topics
- IDs: `egov-idgen` for display numbers; UUID primary keys
- Observability: tracer + Micrometer (no PII in labels)

## Extend (do not fork)

- Gateway route `/upyog-dpdp-service/**`
- Persister `upyog-dpdp-persister.yml`
- ACCESSCONTROL actions 91001–91020
- Helm chart `charts/core-services/upyog-dpdp-service`
- SDK masking techniques for Aadhaar / PAN / email / account

## Missing (built here)

Consent, purpose/processing-basis, classification, retention, data-principal rights orchestration, scanners, compliance rules + scoring, incidents, AI advisory SPI, DPDP SDK.
