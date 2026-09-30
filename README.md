# EventForge REST API

EventForge is a Spring Boot 3.3.5 / Java 17 backend for planning events. PostgreSQL is the production datastore and every `/api/**` route except authentication requires a JWT bearer token.

## Run

Set `JWT_SECRET` to a secure 32+ byte secret, then run `docker compose up --build`. The API listens on `http://localhost:8000`; PostgreSQL uses `postgres:15.10`. For local Maven development, configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, then run `mvn spring-boot:run`.

Environment variables: `SERVER_ADDRESS` (default `0.0.0.0`), `SERVER_PORT` (8000), `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `HIBERNATE_DDL_AUTO` (update), `JWT_SECRET`, `JWT_EXPIRATION_MS` (86400000), `POSTGRES_DB`, `POSTGRES_USER`, and `POSTGRES_PASSWORD`.

Public operational routes are `GET /health`, `GET /docs`, and `GET /api-docs`. Swagger UI documents generated request and response schemas.

## Authentication

Register with `POST /api/auth/register`:
```json
{"name":"Alex Planner","email":"alex@example.com","password":"secure-password"}
```
Login with `POST /api/auth/login` using email/password. Both return:
```json
{"token":"eyJ...","tokenType":"Bearer","user":{"id":"uuid","name":"Alex Planner","email":"alex@example.com","createdAt":"2025-01-01T00:00:00Z"}}
```
Send `Authorization: Bearer <token>` on all protected calls. Passwords and hashes are never returned.

## API surface

All listed routes require bearer authentication unless noted otherwise.

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | Public account registration/login |
| GET, POST | `/api/events` | List/create the caller's events |
| GET, PUT, DELETE | `/api/events/{eventId}` | Read/change/delete an owned event |
| GET, POST | `/api/events/{eventId}/tasks` | List/create tasks |
| PUT, DELETE | `/api/events/{eventId}/tasks/{taskId}` | Modify/remove task |
| GET, POST | `/api/events/{eventId}/guests` | List/create invitations |
| PUT, DELETE | `/api/events/{eventId}/guests/{guestId}` | Change/remove invitation |
| PATCH | `/api/events/{eventId}/guests/{guestId}/status` | RSVP status |
| GET, POST | `/api/events/{eventId}/budget/categories` | List/create budget categories |
| PUT, DELETE | `/api/events/{eventId}/budget/categories/{categoryId}` | Change/remove category |
| GET, POST | `/api/events/{eventId}/budget/expenses` | List/create expenses |
| PUT, DELETE | `/api/events/{eventId}/budget/expenses/{expenseId}` | Change/remove expense |
| GET, POST | `/api/events/{eventId}/schedule` | List/create schedule items |
| PUT, DELETE | `/api/events/{eventId}/schedule/{itemId}` | Change/remove schedule item |
| GET, POST | `/api/events/{eventId}/vendors` | List/create vendors |
| PUT, DELETE | `/api/events/{eventId}/vendors/{vendorId}` | Change/remove vendor |
| GET, POST | `/api/events/{eventId}/vendors/{vendorId}/payments` | List/create payments |
| DELETE | `/api/events/{eventId}/vendors/{vendorId}/payments/{paymentId}` | Remove payment |
| GET | `/api/events/{eventId}/vendors/{vendorId}/payments/summary` | Vendor paid/outstanding totals |
| GET | `/api/events/{eventId}/activities` | Event audit trail |
| GET | `/api/notifications` | Caller notifications |
| PATCH | `/api/notifications/{notificationId}/read` | Mark notification read |

Example event request:
```json
{"name":"Launch","eventDate":"2025-12-31","venue":"Hall A","capacity":100,"budgetLimit":5000.00,"status":"PLANNED","description":"Product launch"}
```
Example task request: `{"title":"Book venue","priority":"HIGH","status":"TODO","dependencyIds":[]}`. Example schedule request uses title, optional description/location, and ISO timestamps for `startTime`/`endTime`. Expense requests contain `categoryId`, `description`, positive `amount`, and `expenseDate`.

Collection endpoints accept Spring pagination parameters `page`, `size`, and `sort=field,asc|desc`; task/expense/invitation notifications use pageable responses. Ownership is always resolved through the event owner, so another user's event resources are not accessible.

## Rules and errors

A task cannot become `COMPLETED` until its same-event dependencies are completed. Accepted guests cannot exceed event capacity; changes maintain the confirmed count transactionally. Expenses cannot exceed a non-null event budget limit. Schedule intervals cannot overlap. Validation/malformed input returns 400; missing/invalid credentials 401; inaccessible resources 404 (without revealing ownership); conflicts such as capacity/dependency/budget/overlap return 409.

Errors are JSON envelopes with `code`, client-safe `message`, `timestamp`, `correlationId`, and validation `details`. Supply `X-Request-ID` to correlate a request.

## Verification

Run `mvn test` and `mvn -DskipTests package`. Generated test/report artifacts are in `tests-artifacts/`.
