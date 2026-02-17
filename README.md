# Enterprise Scheduling Engine (Modular Monolith)

Spring Boot 3.5 + Java 21 modular monolith with **scheduling**, **constraints**, **optimization**, and **notifications** modules. Boundaries enforced by Spring Modulith and ArchUnit; cross-module communication via domain events.

## Prerequisites

- **Java 21+**
- **Docker & Docker Compose** (for Postgres)
- **Gradle** (optional; see below for wrapper)

## Gradle Wrapper

`./gradlew` works in either of these ways:

1. **Using the bundled Gradle (no jar needed)**  
   If the project has `.gradle/gradle-8.11.1/` (e.g. after a one-time extract), `./gradlew` uses that.

2. **Using the wrapper jar**  
   If you have Gradle installed, run `gradle wrapper` once to generate `gradle/wrapper/gradle-wrapper.jar`; then `./gradlew` uses it.

If you see "Missing Gradle", either run `gradle wrapper` (with Gradle installed) or extract a Gradle 8.11.1 distribution into `.gradle/gradle-8.11.1/`.

## Quick Start

1. **Start Postgres**

   ```bash
   docker compose up -d
   ```

2. **Run the application**

   ```bash
   ./gradlew bootRun
   ```

3. **Health check**

   ```bash
   curl -s http://localhost:8080/actuator/health | jq
   ```

4. **Create a schedule**

   ```bash
   curl -X POST http://localhost:8080/api/schedules \
     -H "Content-Type: application/json" \
     -d '{"start":"2025-02-20T09:00:00Z","end":"2025-02-20T17:00:00Z","title":"Team sync"}'
   ```

5. **Get a schedule** (use the `id` from the response above)

   ```bash
   curl -s http://localhost:8080/api/schedules/{id}
   ```

## Tests

```bash
./gradlew test
```

- **ModulithBoundariesTest**: Verifies module arrangement and that no module depends on another’s internals.
- **ScheduleApplicationServiceTest**: Unit test for schedule creation and `ScheduleRequested` event publication.

## Plan

See [PLAN.md](PLAN.md) for the step-by-step plan (Phase 0–5), package layout, and verification steps for each phase.

## Tech Stack

- Spring Boot 3.5.x, Java 21
- Spring Modulith, Spring Data JPA, Flyway
- PostgreSQL (Docker Compose)
- Testcontainers, ArchUnit
- OpenTelemetry / Micrometer, Actuator
