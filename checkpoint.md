# EventForge checkpoint

| Step | Status |
|---|---|
| 1 Design/scaffold | complete |
| 2-7 Implementation | remediation in progress |
| 8-16 Verification/reports | pending final remediation verification |

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
- Pending: feature groups, documentation/OpenAPI examples, representative workflow tests, complete live API report regeneration.
