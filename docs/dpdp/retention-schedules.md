# Retention Schedules

Retention policies are sourced from MDMS `RetentionPolicy` masters. Expired processing activities trigger erasure jobs that invoke each registered `DpdpDataProvider`.

Default action is configurable via `retention.default.action` (`SOFT_DELETE`, `HARD_DELETE`, `ARCHIVE`).
