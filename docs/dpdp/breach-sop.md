# Breach Standard Operating Procedure

1. Log incident via `POST /incident/v1/_create`.
2. System sets `board_report_due_at` to discovery + 72 hours.
3. CRITICAL severity sets `cert_in_required`.
4. `BreachDeadlineScheduler` escalates overdue board notifications using MDMS fiduciary contacts.
