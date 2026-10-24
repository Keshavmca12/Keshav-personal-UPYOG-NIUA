# DPDP Integration Guide

## Module SDK

```xml
<dependency>
  <groupId>org.upyog</groupId>
  <artifactId>dpdp-spring-boot-starter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```yaml
dpdp:
  enabled: true
  service-name: property-services
  tenant-id: pg
  core-url: http://upyog-dpdp-service:8080/upyog-dpdp-service
  fail-mode: FAIL_OPEN   # statutory PT/TL; use FAIL_CLOSED for consent-required ops
  enforcement:
    consent: true
    purpose: true
```

Use `@Purpose("PROPERTY_TAX_SERVICE")` on the controller or `@RequiresConsent(purpose="OPTIONAL_ALERTS")` only where a filter cannot see intent.

## Rights adapters

Implement `DpdpDataProvider` or register a Generic HTTP mapping in MDMS. First delivery includes User/Individual. Do not rewrite PGR/PT/TL in this phase.

## State / ULB config

Copy `src/main/resources/mdms/DPDP/` into `upyog-mdms-data/data/pg/DPDP/`. Existing `DataSecurity` masters stay authoritative for encryption.
