# DPDP Low-Level Design

| Item | Value |
|------|-------|
| Artifact | `org.upyog:upyog-dpdp-service:1.0.0-SNAPSHOT` |
| Package | `org.upyog.dpdp` |
| Context path | `/upyog-dpdp-service` |
| Port | 8096 |

## Packages

- `web/controllers`, `web/models`
- `service/{consent,policy,retention,rights,scan,compliance,incident}`
- `scanner/{database,api,log,access,encryption}`
- `engine/{rule,classification,scoring}`
- `adapter` — `DpdpDataProvider` SPI + Generic HTTP + User/Individual
- `repository`, `producer`, `scheduler`, `ai`, `config`, `util`

## Key sequences

**Consent grant:** Controller → RequestValidator → PolicyService.resolveProcessingBasis → ConsentRepository.insert → privacy event + metric.

**Rights erasure:** RightsService → workflow status → `DpdpDataProvider.deletePersonalData` (User/Individual or Generic HTTP) → result summary (masked).

**Scan:** ScanService persists job → scanner by type → RuleEngine.evaluate → findings (samples always `[REDACTED]`) → ScoringService snapshot.

**Retention:** ShedLock nightly job → skip `legalHold` → default `SOFT_DELETE`.
