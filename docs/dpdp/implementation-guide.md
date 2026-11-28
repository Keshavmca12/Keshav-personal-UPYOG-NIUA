# UPYOG DPDP Implementation Guide

This guide describes how `upyog-dpdp-service` and `dpdp-spring-boot-starter` implement India DPDP capabilities: consent, notices, principal rights, retention erasure, breach timelines, and MDMS-backed policy.

## Core service

- REST APIs under `/upyog-dpdp-service`
- Policy from `egov-mdms` module `DPDP` with classpath fallback
- Flyway migrations in `db/migration/main`

## SDK starter

Enable with `dpdp.enabled=true`. The interceptor reads `x-tenant-id`, `x-user-uuid`, and `x-user-type`, then calls `/consent/v1/_verifyInterService`.

## Integration checklist

1. Add the starter dependency and configure `dpdp.core-url`.
2. Annotate controllers with `@RequiresConsent` / `@Purpose`.
3. Register module `DpdpDataProvider` beans or rely on USER/PT adapters.
4. Seed MDMS masters and activate the Helm chart for the core service.
