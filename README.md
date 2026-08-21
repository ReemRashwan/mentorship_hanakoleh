# Hanakoleh

Hanakoleh is a food delivery platform built with Java 17 and Spring Boot. The project is designed for a modern delivery workflow where customers can browse restaurants and place orders, while backend services manage ordering, business rules, persistence, and integrations.

## Project overview

- Application type: Spring Boot REST API
- Java version: 17
- Build tool: Maven
- Database: PostgreSQL
- Migration tool: Flyway
- Security: Spring Security
- Validation: Bean Validation
- Architecture checks: ArchUnit
- Code style checks: Checkstyle
- Container support: Docker Compose

## Prerequisites

Before running the project locally, make sure the following are available:

- Java 17+
- Maven 3.9+
- Docker and Docker Compose
- Git
- Optional: IDE such as IntelliJ IDEA or VS Code

## Setup

Use the project setup script for the standard local setup flow:

```powershell
./setup.ps1
```

This script prepares the environment and starts the required services for the application. 
Then review the environment values and adjust them if needed.

```powershell
APP_PORT=8080
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hanakoleh_db
DB_USER=hanakoleh_app
DB_PASSWORD=change_me
```

## Run the application

Use the startup script for running the app:

```powershell
./startup.ps1
```

For project verification, you can still use Maven when needed:

```powershell
./mvnw clean verify
```

## Docker setup

The repository includes Docker Compose configuration for the application and database.

```bash
docker compose up -d
```

This starts:

- PostgreSQL database
- The application container
- Mounted logs folder for local debugging

## Configuration notes

The application configuration is defined in `src/main/resources/application.properties`.

Key settings:

- App port defaults to `8080`
- Database host, port, name, user, and password are environment-driven
- Flyway is enabled and migration scripts are loaded from `src/main/resources/db/migration`
- Hibernate DDL validation is enabled to keep the database schema aligned with the codebase

## Naming conventions

The project enforces naming conventions through Checkstyle (`config/checkstyle/checkstyle.xml`).

Rules include:

- Package names: lowercase, dot-separated, e.g. `com.mentorship.hanakoleh.service`
- Type names: PascalCase, e.g. `OrderService`
- Method names: camelCase, e.g. `createOrder`
- Fields and local variables: camelCase, e.g. `deliveryAddress`
- Constants: UPPER_SNAKE_CASE, e.g. `MAX_RETRY_COUNT`
- Static final logger names may be `log` or `logger`

## Architecture rules

The project uses ArchUnit tests to keep the application layered and maintainable.

The current rules enforce:

- `controller` layer
- `service` layer
- `repository` layer
- Controllers do not expose internals to lower layers
- Services can be accessed by controllers
- Repositories can be accessed by services only
- Repository classes must not depend on controller classes
- Classes in each package must follow naming suffix conventions:
  - `*Controller`
  - `*Service`
  - `*Repository`

These checks are implemented in:

- `src/test/java/com/mentorship/hanakoleh/architecture/ArchitectureTest.java`

## Flyway migration rules

Database migration scripts must follow strict naming and SQL naming conventions.

The project validates:

- Flyway file naming format such as `V1__create_users.sql` or `U1__add_status.sql`
- Lowercase snake_case names for migration descriptions
- Database constraints and indexes using approved prefixes:
  - `pk__` for primary keys
  - `fk__` for foreign keys
  - `idx__` and `ux__` for indexes
  - `uq__` for unique constraints
  - `chk__` for check constraints

These checks are implemented in:

- `src/test/java/com/mentorship/hanakoleh/architecture/FlywayNamingConventionTest.java`

## Testing

Run the test suite:

```bash
./mvnw test
```

Run the full Maven verification pipeline:

```bash
./mvnw verify
```

This includes code style validation and architecture validation checks.

## Important project notes

- Flyway migrations are expected under `src/main/resources/db/migration`
- The project currently follows a layered architecture and should continue to do so as functionality grows
- Keep package structure aligned with the architecture rules
- Do not introduce controller-to-repository direct access
- Use lowercase package names and consistent Java naming conventions across new classes
- Keep migration names consistent with the project’s Flyway validation rules

## Useful commands

```powershell
./setup.ps1
./startup.ps1
./mvnw clean compile
./mvnw test
./mvnw verify
``` 
