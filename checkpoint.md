# EventForge checkpoint

| Step | Status |
|---|---|
| 1 Design/scaffold | complete |
| 2-7 Implementation | complete |
| 8-16 Verification/reports | complete |

## Decisions
- Java 17 / Spring Boot 3.3.5 Maven monolith, port 8000; package `com.eventforge`.
- PostgreSQL via compose service `postgres`; API uses `/api`, JWT bearer; health/docs/api-docs public.
- UUID entities, DTO records, constructor-injected transactional services, ownership lookups anchored at Event.owner.id.
- Feature groups: identity/security; events/tasks; invitations/budget; schedule/vendors/notifications/activity.
- Shared domain contract: `Event` owns every event resource; activities and notifications belong to the event owner. Enums persist as strings.

## Unit status: schedule/vendors/notifications/activity
- Implemented ScheduleItem CRUD with validated time ranges, event-owner scoping, pageable lists, and overlap conflict checks.
- Implemented event-scoped vendor CRUD plus validated payment creation/deletion, pageable payment lists, and payment summaries.
- Implemented owner notification listing/read endpoint and public NotificationService recording methods.
- Implemented immutable EventActivity persistence, owner-scoped activity listing, and public EventActivityService recording method.
- Events/tasks unit complete: owner-scoped event and task JPA models, repositories, validated DTOs, transactional services, and REST controllers are implemented. Task completion rejects incomplete dependencies; budget summary currently returns a safe zero planned/actual baseline.

## Unit status
- Identity/security complete: added `User` persistence and repository, validated auth DTOs, BCrypt-backed register/login service/controller, signed UUID-subject JWT service/filter, and stateless security configuration. Public paths are `/api/auth/**`, `/health`, `/docs/**`, and `/api-docs/**`; all other application paths require bearer authentication.
- Verification: `mvn test` and `mvn -DskipTests package` passed using Maven 3.9.9. Boot verification against an H2 compatibility database observed /health 200, /api-docs 200, /docs 200 after redirect, and protected /api/events 401. Reports exist under `tests-artifacts/`.
- Docker daemon was unavailable in this environment, so required compose images could not be pulled here; Dockerfile and docker-compose.yml are present and compose pins `postgres:15.10`.
- Invitations/budget complete: owner-scoped guest invitations, categories, and expenses with validated DTOs, paginated guest/expense endpoints, pessimistic event locking for capacity/budget invariants, and transactional confirmed-guest count maintenance. Invitation statuses are `PENDING`, `ACCEPTED`, and `DECLINED`.
