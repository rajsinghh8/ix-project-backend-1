# EventForge checkpoint

| Step | Status |
|---|---|
| 1 Design/scaffold | complete |
| 2-7 Implementation | in progress |
| 8-16 Verification/reports | pending |

## Decisions
- Java 17 / Spring Boot 3.3.5 Maven monolith, port 8000; package `com.eventforge`.
- PostgreSQL via compose service `postgres`; API uses `/api`, JWT bearer; health/docs/api-docs public.
- UUID entities, DTO records, constructor-injected transactional services, ownership lookups anchored at Event.owner.id.
- Feature groups: identity/security; events/tasks; invitations/budget; schedule/vendors/notifications/activity.
- Shared domain contract: `Event` owns every event resource; activities and notifications belong to the event owner. Enums persist as strings.
