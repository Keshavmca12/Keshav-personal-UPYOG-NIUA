# upyog-dpdp-service

DPDP core capability for UPYOG: consent, purpose, rights orchestration, retention, scanning, scoring, and incidents.

| Item | Value |
|------|-------|
| Port | 8096 |
| Context | `/upyog-dpdp-service` |
| Database | PostgreSQL / Flyway `ug_dpdp_*` |
| Auth | Gateway + `RequestInfo.userInfo` |
| Docs | `core-services/docs/dpdp/` |

Controllers: ConsentController, PurposeController, RightsController, RetentionController, ScanController, ComplianceController, FindingController, IncidentController.
