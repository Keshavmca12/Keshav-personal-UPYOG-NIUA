INSERT INTO ug_dpdp_compliance_rule
(id, tenant_id, rule_code, name, category, severity, enabled, condition_json, recommendation, reference, createdby, createdtime)
VALUES
('rule-data-001', 'pg', 'DPDP-DATA-001', 'Personal data must have classification', 'CLASSIFICATION', 'HIGH', TRUE,
 '{"all":[{"equals":{"path":"$.personalData","value":true}},{"isNull":{"path":"$.classification"}}]}',
 'Classify the data field using DPDP.FieldClassification.', 'DPDP Act 2023', 'SYSTEM', 1720000000000),
('rule-ret-001', 'pg', 'DPDP-RET-001', 'Personal data must have a retention policy', 'RETENTION', 'HIGH', TRUE,
 '{"all":[{"equals":{"path":"$.personalData","value":true}},{"isNull":{"path":"$.retentionPolicy"}}]}',
 'Attach a configured retention policy.', 'DPDP Act 2023', 'SYSTEM', 1720000000000),
('rule-api-001', 'pg', 'DPDP-API-001', 'Personal-data endpoints must authenticate', 'API_SECURITY', 'CRITICAL', TRUE,
 '{"all":[{"equals":{"path":"$.personalDataEndpoint","value":true}},{"equals":{"path":"$.authentication","value":false}}]}',
 'Require authentication on personal-data APIs.', 'DPDP Act 2023', 'SYSTEM', 1720000000000),
('rule-enc-001', 'pg', 'DPDP-ENC-001', 'High-risk personal data must be protected', 'ENCRYPTION', 'CRITICAL', TRUE,
 '{"all":[{"equals":{"path":"$.highRiskPersonal","value":true}},{"equals":{"path":"$.encrypted","value":false}}]}',
 'Apply the platform-approved encryption mechanism from egov-enc-service.', 'DPDP Act 2023', 'SYSTEM', 1720000000000),
('rule-cns-001', 'pg', 'DPDP-CNS-001', 'Consent required when basis is CONSENT', 'CONSENT', 'HIGH', TRUE,
 '{"all":[{"equals":{"path":"$.processingBasis","value":"CONSENT"}},{"equals":{"path":"$.consentPresent","value":false}}]}',
 'Capture purpose-specific consent or change the configured processing basis.', 'DPDP Act 2023', 'SYSTEM', 1720000000000),
('rule-pur-001', 'pg', 'DPDP-PUR-001', 'Processing must declare a purpose', 'PURPOSE', 'HIGH', TRUE,
 '{"all":[{"equals":{"path":"$.personalData","value":true}},{"isNull":{"path":"$.purpose"}}]}',
 'Bind the processing activity to a configured purpose.', 'DPDP Act 2023', 'SYSTEM', 1720000000000),
('rule-min-001', 'pg', 'DPDP-MIN-001', 'Unjustified extra field', 'MINIMIZATION', 'MEDIUM', TRUE,
 '{"all":[{"equals":{"path":"$.personalData","value":true}},{"equals":{"path":"$.required","value":false}},{"equals":{"path":"$.justified","value":false}}]}',
 'Remove the field or record a justification in ApplicationPolicy.', 'DPDP Act 2023', 'SYSTEM', 1720000000000),
('rule-log-001', 'pg', 'DPDP-LOG-001', 'Raw PII must not appear in logs', 'AUDIT', 'CRITICAL', TRUE,
 '{"all":[{"equals":{"path":"$.piiInLog","value":true}}]}',
 'Mask the field before logging. Never log OTP, tokens, full Aadhaar, or full account numbers.', 'DPDP Act 2023', 'SYSTEM', 1720000000000)
ON CONFLICT (tenant_id, rule_code) DO NOTHING;
