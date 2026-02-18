# Enterprise Scheduling Engine — Step-by-Step Plan

## Overview

Single deployable modular monolith with four modules: **scheduling**, **constraints**, **optimization**, **notifications**. Communication via domain events; boundaries enforced by Spring Modulith and ArchUnit.

---

## Phase 0: Project Bootstrap

**Objective:** Create a runnable Spring Boot 3.5 + Java 21 project with Gradle, all dependencies, Docker Compose for Postgres, Flyway, Actuator, OpenTelemetry/Micrometer, and the agreed package layout. No business logic yet.

**Files to create/modify:**
- `build.gradle.kts` (dependencies)
- `settings.gradle.kts`
- `gradle.properties`
- `src/main/java/schedulingengine/Application.java` (`@SpringBootApplication`, `@Modulithic`)
- `src/main/resources/application.yml`
- `docker-compose.yml` (Postgres)
- `src/main/resources/application-local.yml` (optional, for local profile)
- Empty module packages: `schedulingengine.scheduling`, `.constraints`, `.optimization`, `.notifications` (each with `package-info.java` and minimal placeholder so Modulith detects modules)

**Commands:**
- `./gradlew bootRun` (with Postgres up)
- `curl -s http://localhost:8080/actuator/health | jq`

**Verification:**
- App starts; actuator health is UP; no cross-module logic.

---

## Phase 1: Scheduling Module (Create Schedule + Event)

**Objective:** Implement the scheduling module with hexagonal layout. Expose POST/GET `/api/schedules`, persist schedules in Postgres via Flyway, and publish `ScheduleRequested` when a schedule is created. Other modules are stubbed so the app compiles and Modulith/ArchUnit pass.

**Files to create/modify:**
- **Scheduling module (full):**
  - Domain: `Schedule` entity, value types.
  - Application: port interface (e.g. create schedule), application service.
  - Adapters: REST controller (DTOs), JPA repository, Flyway migration.
  - API (module root): `ScheduleRequested` event (Spring application event).
- **Stubs:** constraints, optimization, notifications — minimal `package-info.java` and empty or event listener stubs only if needed for compilation.
- **Infra:** ArchUnit test for “no direct access across modules” (only via public API/events).
- **Unit test:** Schedule creation and event publication.

**Commands:**
- `./gradlew test`
- `./gradlew bootRun`
- `curl -X POST http://localhost:8080/api/schedules -H "Content-Type: application/json" -d '{"start":"2025-02-20T09:00:00","end":"2025-02-20T17:00:00","title":"Meeting"}'`
- `curl -s http://localhost:8080/api/schedules/{id}`

**Verification:**
- Tests pass (including ArchUnit); POST creates schedule and returns ID; GET returns schedule; `ScheduleRequested` is published (observable in logs or test).

---

## Phase 2: Constraints Module

**Objective:** Listen for `ScheduleRequested`, validate constraints (e.g. start < end, duration ≤ max), and publish `ConstraintsValidated` or `ConstraintsFailed`. No REST API for constraints; communication only via events.

**Files to create/modify:**
- constraints: domain (validation result), application (listener, validator), adapters (optional persistence of validation results).
- Events: `ConstraintsValidated`, `ConstraintsFailed` in constraints module API.
- `package-info.java`: `allowedDependencies` for constraints (e.g. only scheduling’s event types if exposed via shared kernel or events in scheduling package).
- ArchUnit: update/confirm constraints may only depend on scheduling’s public API (events).

**Commands:**
- `./gradlew test`
- Create schedule via POST; verify in logs or DB that constraints ran and event published.

**Verification:**
- Unit tests for validator (`ScheduleConstraintsValidatorTest`); listener test (`ScheduleRequestedListenerTest`) that valid schedule → `ConstraintsValidated`, invalid → `ConstraintsFailed`.
- Manual: POST valid schedule, see `ConstraintsValidated` in logs; POST invalid (e.g. end before start), see `ConstraintsFailed`.

---

## Phase 3: Optimization Module

**Objective:** Listen for `ConstraintsValidated`, run a placeholder optimization, persist optimized schedule, publish `ScheduleOptimized`.

**Files to create/modify:**
- optimization: domain (optimized schedule entity), application (listener, optimizer service), adapters (JPA, optional REST for read).
- Event: `ScheduleOptimized` in optimization module API.
- Flyway: tables for optimized schedules.

**Commands:**
- `./gradlew test`
- End-to-end: POST schedule → constraints → optimization → `ScheduleOptimized` published.

**Verification:**
- Unit tests: `ScheduleOptimizerTest`, `ConstraintsValidatedListenerTest`.
- Manual: POST a valid schedule and observe logs for `ConstraintsValidated` and `ScheduleOptimized`. Confirm `optimized_schedules` table has a row for the schedule.

---

## Phase 4: Notifications Module

**Objective:** Listen for `ScheduleOptimized`, create notification record, expose GET `/api/notifications`.

**Files to create/modify:**
- notifications: domain (notification entity), application (listener, query port), adapters (REST, JPA).
- Flyway: notifications table.
- REST: `GET /api/notifications` (list; DTOs at boundary).

**Commands:**
- `./gradlew test`
- `curl http://localhost:8080/api/notifications`

**Verification:**
- After full flow, GET returns notifications; DTOs used; no entity leakage.

---

## Phase 5: Integration Test + Observability

**Objective:** One end-to-end Testcontainers integration test (POST schedule → GET schedule, eventually GET notifications). Add correlation ID propagation (e.g. from OpenTelemetry trace) and meaningful logging.

**Files to create/modify:**
- `@SpringBootTest` + Testcontainers Postgres; test full flow.
- Logging: use trace context (e.g. `TraceId`) in log format or MDC.
- Ensure OpenTelemetry and Micrometer are configured (minimal).

**Commands:**
- `./gradlew test`
- Optional: `curl` with header to simulate correlation.

**Verification:**
- Integration test green; logs show correlation/trace info where applicable.

---

## Recommended Package / Module Layout

```
schedulingengine
├── Application.java
├── scheduling
│   ├── package-info.java          (@ApplicationModule, allowedDependencies)
│   ├── ScheduleRequested.java    (event — public API)
│   ├── domain
│   │   └── Schedule.java
│   ├── application
│   │   ├── ScheduleCommandService.java (port)
│   │   └── ScheduleApplicationService.java
│   └── adapters
│       ├── web
│       │   ├── ScheduleController.java
│       │   └── ScheduleDto.java
│       └── persistence
│           ├── ScheduleJpaRepository.java
│           └── ScheduleEntity.java (or reuse domain with JPA in adapters)
├── constraints
│   ├── package-info.java
│   ├── ConstraintsValidated.java
│   ├── ConstraintsFailed.java
│   └── internal (or domain/application/adapters)
├── optimization
│   ├── package-info.java
│   ├── ScheduleOptimized.java
│   └── internal
└── notifications
    ├── package-info.java
    └── internal
```

- **Same DB, same app:** One Postgres; Flyway migrations in `src/main/resources/db/migration`; modules can use separate tables/schemas (e.g. `scheduling.schedules`, `notifications.notifications`) or prefix table names.
- **Hexagonal inside each module:** `domain`, `application`, `adapters` as subpackages; Modulith treats them as internal (only base package is API).

---

## Dependencies (Gradle)

See `build.gradle.kts` for full list. Summary:

- **Spring Boot** 3.5.x (BOM)
- **Java** 21
- **Spring Modulith** (spring-modulith-bom or explicit version)
- **Spring Data JPA** + **Postgres** driver
- **Flyway**
- **Testcontainers** (JUnit 5, Postgres)
- **ArchUnit** (test)
- **OpenTelemetry** (e.g. opentelemetry-api, micrometer-tracing-bridge) + **Micrometer** (metrics)
- **Actuator** (health, metrics, optionally info)

---

## Phase 1 — Commands to Run and Verification

1. **Install Gradle wrapper (if needed):** `gradle wrapper`
2. **Start Postgres:** `docker compose up -d`
3. **Run tests:** `./gradlew test` — expect `ModulithBoundariesTest` and `ScheduleApplicationServiceTest` to pass.
4. **Run the application:** `./gradlew bootRun`
5. **Create schedule:** `curl -X POST http://localhost:8080/api/schedules -H "Content-Type: application/json" -d '{"start":"2025-02-20T09:00:00Z","end":"2025-02-20T17:00:00Z","title":"Team sync"}'`
6. **Get schedule:** `curl -s http://localhost:8080/api/schedules/{id}` (use `id` from step 5).
7. **Optional:** Check logs for `ScheduleRequested` event after POST.

---

## Next Steps After Phase 1

- Implement Phase 2 (constraints listener + events).
- Add ArchUnit rules that restrict dependencies to explicit `allowedDependencies` and enforce “no class from module X in module Y except via API package”.
