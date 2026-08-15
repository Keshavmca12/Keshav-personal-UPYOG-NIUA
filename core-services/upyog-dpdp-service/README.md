# upyog-dpdp-service

Native UPYOG DPDP core capability: consent, purpose, processing basis, rights orchestration, retention, scanners, scoring, and incidents.

This is a **platform service**, not a municipal business module. Modules consume it through MDMS configuration, `dpdp-spring-boot-starter`, and `DpdpDataProvider` adapters.

Context path: `/upyog-dpdp-service`  
Port: `8096`

```bash
mvn -f upyog-dpdp-service/pom.xml test
mvn -f libraries/dpdp-spring-boot-starter/pom.xml test
```

Design docs: `docs/dpdp/`.
