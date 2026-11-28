# Module Plugin Onboarding

1. Add `dpdp-spring-boot-starter` to the module POM.
2. Set `dpdp.core-url` to the platform DPDP service.
3. Declare `@Purpose` on statutory and consent-based APIs.
4. Optionally implement `DpdpDataProvider` for module-specific access/correction/erasure.
5. Register `DataProviderConfig` and inventory rows in MDMS `DPDP`.
