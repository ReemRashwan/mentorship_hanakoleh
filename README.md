# 🍔 Hanakol-Eh - Food Delivery Platform API

![Java 17](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

Hanakol-Eh is a robust, scalable backend RESTful API for a food delivery platform built with **Java 17** and **Spring Boot**. The system manages the core domain of online food ordering, including restaurant listings, menu items, customer order workflows, and business rule enforcement.

---

## 🌟 Key Features & Domain Scope

- **Restaurant & Menu Management:** Browse available restaurants and their menus.
- **Order Workflow:** Place, track, and manage food delivery orders.
- **Domain Validation & Security:** Enforced business rules via Bean Validation and Spring Security.
- **Database Migrations:** Schema evolution managed safely using Flyway.
- **Architecture & Quality Guardrails:** Automated architectural compliance via ArchUnit and strict code style checks with Checkstyle.

---

## 🛠️ Tech Stack & Tools

| Component | Technology |
| :--- | :--- |
| **Language** | Java 17 |
| **Framework** | Spring Boot |
| **Database** | PostgreSQL |
| **Migration** | Flyway |
| **Security** | Spring Security |
| **Architecture Testing** | ArchUnit |
| **Code Style** | Checkstyle |
| **Containerization** | Docker & Docker Compose |
| **Build Tool** | Maven |

---

## 🏗️ Architecture & Layering Rules

The project follows a strict Layered Architecture. Dependency flow is strictly enforced via ArchUnit tests (`ArchitectureTest.java`):

```
[ Controller Layer ] ---> [ Service Layer ] ---> [ Repository Layer ]
```

* **Controllers** expose REST endpoints and handle request/response mapping. They never access Repositories directly.
* **Services** contain business logic and transactional boundaries.
* **Repositories** handle persistence operations.
* **Naming Conventions:** Enforced suffixes (`*Controller`, `*Service`, `*Repository`).

---

## 🗄️ Database & Flyway Naming Conventions

All database changes are managed via Flyway migration scripts under `src/main/resources/db/migration`. Strict naming conventions are enforced via unit tests (`FlywayNamingConventionTest.java`):

* **Files:** `V<version>__<description>.sql`, `U<version>__<description>.sql`, or `R__<description>.sql` (e.g., `V1__create_users.sql`, `U1.1__update_users.sql`, `R__reset_data.sql`)
* **Primary Keys:** `pk__<name>` (e.g., `pk__users`)
* **Foreign Keys:** `fk__<name>` (e.g., `fk__orders_users`)
* **Indexes:** `idx__<name>` for non-unique, `ux__<name>` for unique (e.g., `idx__users_email`, `ux__users_username`)
* **Constraints:** `uq__<name>` for unique constraints, `chk__<name>` for check constraints

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### 1. Environment Setup
Run the setup script to initialize environment variables and required infrastructure:

```powershell
./setup.ps1
```

Review and update the generated `.env` / environment configurations if necessary:
```env
APP_PORT=8080
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hanakoleh_db
DB_USER=hanakoleh_app
DB_PASSWORD=change_me
```

### 2. Run with Docker Compose
To spin up both PostgreSQL and the application container:

```bash
docker compose up -d
```

### 3. Run Locally (PowerShell)
```powershell
./startup.ps1
```

---

## 🧪 Testing & Quality Verification

Run full verification including tests, ArchUnit architectural rules, and Checkstyle compliance:

```bash
./mvnw clean verify
```

To run unit and integration tests only:
```bash
./mvnw test
```