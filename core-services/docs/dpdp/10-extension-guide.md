# DPDP Extension Guide

- Add a compliance rule in MDMS `DPDP.ComplianceRule` — do not fork `RuleEngine`.
- Add a purpose / basis / retention policy in MDMS; code stays tenant-identical.
- Implement `DpdpDataProvider` for a new module instead of changing DPDP core.
- Implement `DpdpAiRecommendationProvider` for an optional HTTP LLM. Send masked summaries only.
- GDPR / ISO packs are additional rule JSON, not new Java services.
