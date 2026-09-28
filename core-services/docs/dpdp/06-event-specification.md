# DPDP Event Specification

## Persister topics (optional, `dpdp.kafka.persister-enabled`)

`save-dpdp-consent`, `update-dpdp-consent`, `save-dpdp-privacy-event`, `save-dpdp-principal-request`, `update-dpdp-principal-request`, `save-dpdp-scan`, `update-dpdp-scan`, `save-dpdp-finding`, `save-dpdp-incident`, `update-dpdp-incident`

## Domain events (always)

| Topic | When |
|-------|------|
| `dpdp.consent.v1` | Grant / withdraw |
| `dpdp.rights.v1` | Rights request lifecycle |
| `dpdp.retention.v1` | Expired activity processed |
| `dpdp.scan.v1` | Scan started / completed |
| `dpdp.compliance-finding.v1` | Finding created |
| `dpdp.incident.v1` | Incident create / update |
| `dpdp.audit.v1` | Privileged privacy action |

Payloads contain identifiers and categories, never raw PII.
