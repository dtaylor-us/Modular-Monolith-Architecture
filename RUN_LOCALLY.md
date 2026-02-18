# Run Locally

## Prerequisites

- Java 21
- Docker and Docker Compose (for Postgres)
- Gradle (or use the wrapper `./gradlew`)

## 1. Start Postgres

```bash
docker compose up -d
```

Wait until Postgres is healthy (or a few seconds). The app is configured to use:

- Host: `localhost:5432`
- Database: `schedulingdb`
- User: `scheduling`
- Password: `scheduling`

## 2. Run the application

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

## 3. Create a schedule request (POST)

```bash
curl -s -X POST http://localhost:8080/api/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Team sync",
    "earliestStart": "2026-02-18T09:00:00",
    "latestEnd": "2026-02-18T17:00:00",
    "durationMinutes": 90,
    "preferredStart": "2026-02-18T10:00:00",
    "priority": "HIGH"
  }'
```

Example response (status is `REQUESTED` initially):

```json
{
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Team sync",
  "status": "REQUESTED",
  "earliestStart": "2026-02-18T09:00:00Z",
  "latestEnd": "2026-02-18T17:00:00Z",
  "durationMinutes": 90,
  "preferredStart": "2026-02-18T10:00:00Z",
  "priority": "HIGH",
  "createdAt": "2026-02-18T12:00:00Z",
  "optimizedStart": null,
  "optimizedEnd": null
}
```

## 4. Get schedule by ID (GET)

Use the `requestId` from the POST response:

```bash
curl -s http://localhost:8080/api/schedules/<requestId>
```

After the optimizer runs (asynchronously), the same GET will return `status: "OPTIMIZED"` and non-null `optimizedStart` and `optimizedEnd`. If you used the preferred start above, the slot will be 10:00–11:30 UTC.

## 5. List notifications (GET)

After at least one schedule has been optimized:

```bash
curl -s http://localhost:8080/api/notifications
```

## 6. Run tests

```bash
./gradlew test
```

Integration tests use Testcontainers and will start a Postgres container automatically.

## 7. Stop Postgres

```bash
docker compose down
```
