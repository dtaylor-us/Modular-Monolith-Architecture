# Phase 1 Optimizer Refactor – Analysis & Plan

## 1) Current state summary

**REST**
- `POST /api/schedules` – body `{ start, end, title }` (fixed times). Returns created schedule (id, start, end, title, createdAt).
- `GET /api/schedules/{id}` – returns that schedule by id.

**Domain / persistence**
- Scheduling: `Schedule` (id, startTime, endTime, title, createdAt). Table `schedules`.
- Optimization: `OptimizedSchedule` (id, scheduleId, algorithmVersion, summary, optimizedAt). Table `optimized_schedules` with `schedule_id` FK concept.

**Events**
- `ScheduleRequested(scheduleId, startTime, endTime, title, requestedAt)` – scheduling emits after persisting schedule.
- `ConstraintsValidated(scheduleId, validatedAt)` – constraints emit on success.
- `ConstraintsFailed(scheduleId, reasons, failedAt)` – constraints emit on failure.
- `ScheduleOptimized(scheduleId, optimizedScheduleId, optimizedAt)` – optimization emits after placeholder run.

**Who chooses start/end**
- Today: the **scheduling** module (user sends fixed start/end). Optimization does not choose times; it only persists a placeholder record.

---

## 2) Refactor plan (minimal, compilable steps)

**Checkpoint 1 – Scheduling: request model and API**
- New domain: `ScheduleRequest` (id, earliestStart, latestEnd, durationMinutes, preferredStart, priority, createdAt, status).
- New table: `schedule_requests` (V3 migration). Keep `schedules` for now (unused) or drop in same migration.
- New event: `ScheduleRequested(requestId, earliestStart, latestEnd, durationMinutes, preferredStart, priority, requestedAt)`.
- API: POST body `CreateScheduleRequest` (earliestStart, latestEnd, durationMinutes, preferredStart?, priority?). Response: requestId + status REQUESTED.
- GET /api/schedules/{id} for now returns request only (no optimized slot yet).

**Checkpoint 2 – Constraints: validate request**
- Validate: earliestStart < latestEnd, durationMinutes > 0, earliestStart + duration ≤ latestEnd.
- Listener: receive `ScheduleRequested` (full payload), validate, emit `ConstraintsValidated(requestId, ...request data...)` or `ConstraintsFailed(requestId, reasons)`.
- `ConstraintsValidated` carries request data so optimization can choose slot without calling scheduling.

**Checkpoint 3 – Optimization: slot choice + OptimizationFailed**
- Listener: on `ConstraintsValidated`, compute slot (preferred if fits, else earliestStart); if no slot, emit `OptimizationFailed(requestId, reason)`.
- Persist `OptimizedSchedule` with requestId, optimizedStart, optimizedEnd, strategyUsed.
- Event: `ScheduleOptimized(requestId, optimizedStart, optimizedEnd)`.
- New event: `OptimizationFailed(requestId, reason)`.

**Checkpoint 4 – Notifications**
- Listener: on `ScheduleOptimized`, store notification (“Schedule optimized for request <id>: <start> - <end>”).
- Table: `notifications`. REST: `GET /api/notifications`.

**Checkpoint 5 – Schedule view and GET composite**
- Table: `schedule_views` (request_id, status, optimized_start, optimized_end). Scheduling owns it; listener in scheduling updates it on ScheduleRequested, ConstraintsFailed, ScheduleOptimized, OptimizationFailed.
- GET /api/schedules/{id}: return request + status + optimizedStart/End from view.

**Checkpoint 6 – Tests**
- ArchUnit: unchanged (module boundaries).
- Integration test: POST request → wait for optimized or failed → assert preferredStart when valid, earliestStart when preferred invalid.

---

## 3) Event payloads (final)

| Event               | Payload |
|---------------------|---------|
| ScheduleRequested   | requestId, earliestStart, latestEnd, durationMinutes, preferredStart, priority, requestedAt |
| ConstraintsValidated| requestId, earliestStart, latestEnd, durationMinutes, preferredStart, validatedAt |
| ConstraintsFailed   | requestId, reasons, failedAt |
| ScheduleOptimized   | requestId, optimizedStart, optimizedEnd, optimizedAt |
| OptimizationFailed  | requestId, reason, failedAt |

---

## 4) Schema (Flyway)

- **V3** – `schedule_requests` (id, earliest_start, latest_end, duration_minutes, preferred_start, priority, created_at, status). Optionally drop or ignore `schedules`.
- **V4** – `optimized_schedules`: add `optimized_start`, `optimized_end`, rename `schedule_id` → `request_id` (or keep and add request_id). Prefer: add request_id, optimized_start, optimized_end; keep schedule_id as legacy or drop.
- **V5** – `notifications` (id, request_id, message, created_at).
- **V6** – `schedule_views` (request_id PK, status, optimized_start, optimized_end, updated_at).

Implementing checkpoints in order.
