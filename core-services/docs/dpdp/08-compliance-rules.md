# DPDP Compliance Rules and Scoring

The deterministic rule engine is authoritative. AI recommendations are advisory only.

## 1. Rule schema

Supported predicates: `equals`, `notEquals`, `isNull`, `isTrue`, `isFalse`, `exists`. Combined with `all` / `any`.

## 2. Seed rules

| Code | Condition | Severity | Category |
|------|-----------|----------|----------|
| DPDP-DATA-001 | personalData and classification null | HIGH | CLASSIFICATION |
| DPDP-DATA-002 | personalData and category UNCLASSIFIED | MEDIUM | CLASSIFICATION |
| DPDP-RET-001 | personalData and retentionPolicy null | HIGH | RETENTION |
| DPDP-RET-002 | expired and legalHold false and not deleted | HIGH | RETENTION |
| DPDP-API-001 | personalDataEndpoint and authentication false | CRITICAL | API_SECURITY |
| DPDP-API-002 | personalData in query parameter | HIGH | API_SECURITY |
| DPDP-API-003 | personalDataEndpoint and authorization false | CRITICAL | ACCESS_CONTROL |
| DPDP-ENC-001 | highRiskPersonal and encrypted false | CRITICAL | ENCRYPTION |
| DPDP-ENC-002 | personalData and masking missing on response | HIGH | ENCRYPTION |
| DPDP-CNS-001 | processingBasis CONSENT and consent missing | HIGH | CONSENT |
| DPDP-PUR-001 | processing without purpose | HIGH | PURPOSE |
| DPDP-MIN-001 | collected field not in required/justified set | MEDIUM | MINIMIZATION |
| DPDP-AUD-001 | personalData access and auditEvent missing | HIGH | AUDIT |
| DPDP-RBAC-001 | privileged personalData access without role | CRITICAL | ACCESS_CONTROL |
| DPDP-LOG-001 | raw PII pattern in log sample | CRITICAL | AUDIT |

## 3. Severity weights

CRITICAL 25, HIGH 15, MEDIUM 8, LOW 3.

## 4. Scoring formula

```
penalty(c) = Σ_s  F(c,s) * weight(s)
categoryScore(c) = max(0, 100 - penalty(c))
overallScore = Σ_c  categoryScore(c) * categoryWeight(c) / Σ_c categoryWeight(c)
```

Category weights: CLASSIFICATION 0.10, CONSENT 0.10, PURPOSE 0.08, MINIMIZATION 0.08, RETENTION 0.10, ENCRYPTION 0.12, API_SECURITY 0.12, ACCESS_CONTROL 0.12, AUDIT 0.10, INCIDENT 0.08.

## 5. AI usage

Default `TemplateRecommendationProvider` is deterministic. Optional LLM adapters receive masked finding summaries only. AI never changes severity, status, or score.
