# EventForge REST API

Spring Boot 3 / Java 17 API for event planning. Production uses PostgreSQL; tests use H2. Run with `docker compose up --build`, or configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` and run `mvn spring-boot:run`. The service is at `http://localhost:8000`.

`GET /health`, `GET /docs`, and `GET /api-docs` are public. All `/api/**` routes below other than auth require `Authorization: Bearer <token>`. Swagger/OpenAPI, including field examples, is available at `/api-docs`.

## Authentication

`POST /api/auth/register` request `{"name":"Alex Planner","email":"alex@example.com","password":"secure-password"}` → **201** `{"token":"eyJ...","tokenType":"Bearer","user":{"id":"uuid","name":"Alex Planner","email":"alex@example.com","createdAt":"2025-01-01T00:00:00Z"}}`.

`POST /api/auth/login` request `{"email":"alex@example.com","password":"secure-password"}` → **200** the same token response. Passwords are never returned.

## Route reference and JSON examples

Replace `E`, `T`, `G`, `C`, `X`, `S`, `V`, and `P` with returned UUIDs. Collection responses are Spring pages (`{"content":[...],"totalElements":1,...}`). A `204` response has no JSON body.

### Events

* `GET /api/events?search=launch&status=PLANNED&page=0&size=20` → **200** `{"content":[{"id":"E","name":"Launch","eventDate":"2025-12-31","venue":"Hall A","capacity":100,"confirmedGuestCount":0,"budgetLimit":5000.00,"status":"PLANNED","description":"Product launch","budgetSummary":{"budgetLimit":5000.00,"planned":0.00,"actual":0.00,"remaining":5000.00}}]}`.
* `POST /api/events` request `{"name":"Launch","eventDate":"2025-12-31","venue":"Hall A","capacity":100,"budgetLimit":5000.00,"status":"PLANNED","description":"Product launch"}` → **201** event JSON shown above.
* `GET /api/events/E` → **200** event JSON shown above.
* `PUT /api/events/E` uses the same event request → **200** updated event JSON.
* `DELETE /api/events/E` request *(none)* → **204** *(no body)*.

### Tasks

* `GET /api/events/E/tasks?status=TODO&page=0&size=20` → **200** `{"content":[{"id":"T","eventId":"E","title":"Book venue","priority":"HIGH","deadline":"2025-12-01T12:00:00Z","status":"TODO","dependencyIds":[]}]}`.
* `POST /api/events/E/tasks` request `{"title":"Book venue","description":"Confirm contract","priority":"HIGH","deadline":"2025-12-01T12:00:00Z","status":"TODO","dependencyIds":[]}` → **201** task JSON.
* `PUT /api/events/E/tasks/T` uses that task request → **200** task JSON.
* `PATCH /api/events/E/tasks/T/complete` request *(none)* → **200** `{"id":"T","status":"COMPLETED",...}`.
* `DELETE /api/events/E/tasks/T` request *(none)* → **204** *(no body)*.

### Invitations

* `GET /api/events/E/guests?page=0&size=20` → **200** `{"content":[{"id":"G","name":"Taylor Guest","email":"taylor@example.com","phone":"+15551234567","status":"PENDING"}]}`.
* `POST /api/events/E/guests` request `{"name":"Taylor Guest","email":"taylor@example.com","phone":"+15551234567"}` → **201** invitation JSON.
* `PUT /api/events/E/guests/G` uses that request → **200** invitation JSON.
* `PATCH /api/events/E/guests/G/status` request `{"status":"ACCEPTED"}` → **200** `{"id":"G","status":"ACCEPTED",...}`.
* `DELETE /api/events/E/guests/G` request *(none)* → **204** *(no body)*.

### Budget

* `GET /api/events/E/budget-categories` → **200** `[{"id":"C","name":"Catering","description":"Food and drinks","plannedAmount":1200.00}]`.
* `POST /api/events/E/budget-categories` request `{"name":"Catering","description":"Food and drinks","plannedAmount":1200.00}` → **201** category JSON.
* `PUT /api/events/E/budget-categories/C` uses that request → **200** category JSON; `DELETE /api/events/E/budget-categories/C` → **204** *(no body)*.
* `GET /api/events/E/expenses?categoryId=C&search=deposit&page=0&size=20` → **200** `{"content":[{"id":"X","categoryId":"C","categoryName":"Catering","description":"Deposit","amount":450.00,"expenseDate":"2025-12-01"}]}`.
* `POST /api/events/E/expenses` request `{"categoryId":"C","description":"Deposit","amount":450.00,"expenseDate":"2025-12-01"}` → **201** expense JSON.
* `PUT /api/events/E/expenses/X` uses that request → **200** expense JSON; `DELETE /api/events/E/expenses/X` → **204** *(no body)*.

### Schedule

* `GET /api/events/E/schedule-items?page=0&size=20` → **200** `{"content":[{"id":"S","eventId":"E","title":"Reception","description":"Opening","startTime":"2025-12-31T18:00:00","endTime":"2025-12-31T19:00:00","location":"Main hall"}]}`.
* `POST /api/events/E/schedule-items` request `{"title":"Reception","description":"Opening","startTime":"2025-12-31T18:00:00","endTime":"2025-12-31T19:00:00","location":"Main hall"}` → **201** schedule JSON.
* `PUT /api/events/E/schedule-items/S` uses that request → **200** schedule JSON; `DELETE /api/events/E/schedule-items/S` → **204** *(no body)*.

### Vendors and payments

* `GET /api/events/E/vendors?page=0&size=20` → **200** `{"content":[{"id":"V","eventId":"E","name":"Acme Catering","category":"Catering","contactName":"Morgan Lee","email":"morgan@acme.test","phone":"+15551234567","contractedAmount":2000.00}]}`.
* `POST /api/events/E/vendors` request `{"name":"Acme Catering","category":"Catering","contactName":"Morgan Lee","email":"morgan@acme.test","phone":"+15551234567","contractedAmount":2000.00}` → **201** vendor JSON.
* `PUT /api/events/E/vendors/V` uses that request → **200** vendor JSON; `DELETE /api/events/E/vendors/V` → **204** *(no body)*.
* `GET /api/events/E/vendors/V/payments?page=0&size=20` → **200** `{"content":[{"id":"P","vendorId":"V","amount":500.00,"paidOn":"2025-12-01","note":"Deposit"}]}`.
* `POST /api/events/E/vendors/V/payments` request `{"amount":500.00,"paidOn":"2025-12-01","note":"Deposit"}` → **201** payment JSON; `DELETE /api/events/E/vendors/V/payments/P` → **204** *(no body)*.
* `GET /api/events/E/vendors/V/payments/summary` → **200** `{"vendorId":"V","contractedAmount":2000.00,"paidAmount":500.00,"outstandingAmount":1500.00,"paymentCount":1}`.

### Activities and notifications

* `GET /api/events/E/activities?page=0&size=20` → **200** `{"content":[{"id":"uuid","eventId":"E","actorId":"uuid","action":"EVENT_CREATED","details":"Launch","createdAt":"2025-01-01T00:00:00"}]}`.
* `GET /api/notifications?page=0&size=20` → **200** `{"content":[{"id":"uuid","eventId":"E","type":"TASK_ASSIGNED","message":"Task assigned","read":false,"createdAt":"2025-01-01T00:00:00"}]}`.
* `PATCH /api/notifications/N/read` request *(none)* → **200** `{"id":"N","eventId":"E","type":"TASK_ASSIGNED","message":"Task assigned","read":true,"createdAt":"2025-01-01T00:00:00"}`.

## Rules and errors

Dependencies must be completed before a task can complete; accepted guests cannot exceed capacity; expenses cannot exceed the event budget; schedule intervals cannot overlap. Validation is **400**, missing credentials **401**, an inaccessible event/resource **404**, and these rule conflicts **409**. Errors use `{"code":"CONFLICT","message":"...","timestamp":"...","correlationId":"...","details":{}}`.

Run `mvn test` and `mvn -DskipTests package`.
