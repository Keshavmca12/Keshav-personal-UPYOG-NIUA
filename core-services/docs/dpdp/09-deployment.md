# DPDP Deployment

- App image: `build/maven/Dockerfile` → `upyog-dpdp-service`
- DB image: `upyog-dpdp-service/src/main/resources/db` → `upyog-dpdp-service-db`
- Local fallback: `upyog-dpdp-service/deploy/deployment.yaml`
- Helm: `UPYOG-DevOps/config-as-code/helm/charts/core-services/upyog-dpdp-service` with `ingress.zuul: true`, `context: upyog-dpdp-service`
- Gateway: `/upyog-dpdp-service/**`
- Persister YAML in service, `egov-persister`, and `upyog-configs/configs/egov-persister/`
- Metrics: `dpdp.scan.duration`, `dpdp.scan.findings`, `dpdp.consent.granted`, `dpdp.consent.withdrawn`, `dpdp.deletion.completed` (no PII labels)
