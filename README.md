# WoToS Statistics Service

![Build](https://github.com/kevinthelago/wotos-statistics-service/actions/workflows/maven.yml/badge.svg)

Microservice in the [WoToS](https://github.com/users/kevinthelago/projects/2) system. Calculates and persists WN8 ratings for players and individual vehicles by combining raw stats from the WoT API with expected statistics from the XVM API. Called exclusively by the edge service.

## How WN8 is calculated

1. Fetch raw player/vehicle stats from the WoT API via Feign client.
2. Fetch expected statistics (avg damage, frags, etc. per vehicle) from XVM (`static.modxvm.com`).
3. Persist expected values in the `expected_statistics` table (refreshed on demand).
4. Calculate WN8 per vehicle, then aggregate to a player-level rating.
5. Store results as time-series snapshots — each call may append a new row, enabling historical tracking.

## Prerequisites

- Java 8 (Temurin recommended)
- Maven or the included `./mvnw` wrapper
- MySQL 8 running at `localhost:3306`, user `root`, password `root`
- Database `wotos_statistics_database` (created automatically by Hibernate on first run)
- WoT application ID set as environment variable: `app_id`
- `wotos-eureka-server` running (service registry)
- `wotos-config-server` running at `localhost:4040`

## Running Locally

### Command Line

```bash
./mvnw spring-boot:run
```

### IntelliJ

1. Open the project root in IntelliJ IDEA.
2. Set the environment variable `app_id=<your-app-id>` in the Run Configuration.
3. Run `WotosStatisticsServiceApplication`.

## Building

```bash
./mvnw clean package        # build JAR, skip tests
./mvnw clean install        # build JAR + run all tests
```

## Testing

Tests require MySQL running locally. The test database is `wotos_statistics_test_database`.

```bash
./mvnw test                                        # run all tests
./mvnw test -Dtest=PlayerStatisticsServiceTest     # run a single test class
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/stats/players` | Get player statistics snapshots |
| `POST` | `/api/stats/players` | Create new player statistics snapshots |
| `GET` | `/api/stats/vehicles` | Get vehicle statistics snapshots |
| `POST` | `/api/stats/vehicles` | Create new vehicle statistics snapshots |

### Query parameters

| Parameter | Type | Endpoints | Description |
|-----------|------|-----------|-------------|
| `accountIds` | `Integer[]` | all | WoT account IDs |
| `gameModes` | `String[]` | GET players/vehicles | Game modes to include (e.g. `random`, `ranked_battles`) |
| `vehicleIds` | `Integer[]` | vehicle endpoints | Filter by vehicle |

## Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```
