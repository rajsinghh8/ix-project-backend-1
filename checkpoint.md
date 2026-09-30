# EventForge checkpoint

| Step | Status |
|---|---|
| 1 Design/scaffold | complete |
| 2-7 Implementation | remediation complete |
| 8-16 Verification/reports | complete |

## Decisions
- Java 17 / Spring Boot 3.3.5 Maven monolith, port 8000; package `com.eventforge`.
- PostgreSQL via compose service `postgres`; API uses `/api`, JWT bearer; health/docs/api-docs public.
- UUID entities, DTO records, constructor-injected transactional services, ownership lookups anchored at Event.owner.id.
- Feature groups: events/tasks; invitations/budget; schedule/vendors/notifications/activity; docs/tests/reporting.
- Shared mutation contract: every successful event, task, invitation, budget, schedule, vendor, payment, and notification mutation records `EventActivity`; event-owner notifications are emitted where actionable. Activities and notifications belong to the event owner.
- Fixed API contracts: budget paths are `/api/events/{eventId}/budget-categories` and `/api/events/{eventId}/expenses`; schedules are `/api/events/{eventId}/schedule-items`. JWT principal remains UUID and routes use `CurrentUser`.
- Budget category contract: `name`, optional `description`, and nonnegative `plannedAmount`; event budget summary aggregates category plans and expense totals. Invitation RSVP allows `PENDING`, `ACCEPTED`, `DECLINED`, `MAYBE`.
- Collection filters: events support search/status/date range; tasks support status/search/deadline range; expenses support category/search/date range.

## Previous implementation status
- Baseline implementation, build, H2 boot probe, and artifacts were completed. Docker daemon was unavailable in this environment.

## Remediation status
- Feature remediation complete: endpoints, budget planning/aggregation, RSVP MAYBE, filters, UUID principal handling, and mutation notification/activity integrations implemented.
- Documentation complete: README endpoint examples and Springdoc operation/schema examples cover the API; Swagger redirect path is public.
- Verification complete: Maven test passed (4 tests); Maven package passed. H2-backed live API workflow ran 29 observed checks with 0 failures, including `/docs` and `/api-docs` 200, JWT/ownership coverage, filters, and required rule rejections.
- Reports regenerated under `tests-artifacts/`: `api_test_report.xlsx` and `project_report.docx`.

## Verifier remediation plan
- Vendor group: replace legacy vendor fields with required `serviceType`, nonnegative required `cost`, required `contactDetails`, and string-persisted `VendorBookingStatus`; make payments use required `Instant paidAt`; add `paidAmount` and `outstandingAmount` to every vendor list response. Outstanding is `max(cost - paidAmount, 0)`.
- Lifecycle group: use `Instant` for schedule timestamps and activity creation; add scheduled deadline processing for approaching and overdue tasks; perform explicit transactional dependent-resource cleanup before event deletion. Deadline notifications are deduplicated per task/day/type.
- Final verification will rerun Maven tests/package and a live H2 workflow, regenerate accumulated real HTTP JSON/xlsx/docx artifacts, retaining `/docs` and `/api-docs` checks.
- Contracts between groups: lifecycle cleanup may delete vendor/payment rows by event id; vendor group owns the fields and payment aggregation exposed by vendor APIs.

| Verifier remediation group | Status |
|---|---|
| Vendor contract/payment summaries | in progress |
| Timestamp/notifications/event cleanup | in progress |
| Verification and reports | pending |
