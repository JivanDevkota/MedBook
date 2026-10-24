# MedBook

MedBook is a full-stack healthcare management platform for booking doctor appointments, managing medical records, prescriptions, payments, and notifications. It is a single-repository monorepo containing a Spring Boot REST API and an Angular client.

## Project structure

```
MedBook/
├── medbook_backend/          # Spring Boot REST API (Java 17)
│   └── src/main/java/com/nsu/medbook_backend/
│       ├── appointment/      # Appointment booking (entity, repository)
│       ├── auth/             # Registration, login, JWT refresh (controller, dto, service)
│       ├── common/           # Shared kernel: auditing, exceptions, validation, responses
│       ├── doctor/           # Doctor profiles
│       ├── file/             # Patient file metadata
│       ├── medicalrecord/    # Medical records
│       ├── notification/     # User notifications
│       ├── patient/          # Patient profiles
│       ├── payment/          # Payments
│       ├── prescription/     # Prescriptions + prescription items
│       ├── schedule/         # Doctor weekly schedules
│       ├── security/         # Spring Security config, JWT filter and token provider
│       └── user/             # User accounts and roles
├── medbook_frontend/         # Angular 21 SPA (standalone components, signals)
│   └── src/app/
│       ├── core/             # HTTP services, interceptors, guards, models, constants
│       ├── features/         # Feature modules: auth, appointments, doctors, admin, ...
│       ├── layouts/          # Main layout, navbar, sidebar
│       └── shared/           # Reusable pipes, validators
├── .gitattributes
└── .gitignore
```

Each backend domain package follows the same layout: `entity/`, `repository/`, and — as the module grows — `dto/`, `service/`, `controller/`.

## Tech stack

| Layer    | Technology                                                                 |
|----------|----------------------------------------------------------------------------|
| Backend  | Java 17, Spring Boot 4.1, Spring Security (JWT), Spring Data JPA, Lombok   |
| Database | MySQL (prod), H2 in-memory (dev)                                           |
| Frontend | Angular 21, TypeScript 5.9, Tailwind CSS 4, RxJS                           |
| Auth     | JWT access + refresh tokens, role-based access (ADMIN, DOCTOR, PATIENT, STAFF) |

## Prerequisites

- JDK 17+
- Node.js 20+ and npm
- MySQL 8+ (used by the `dev` and `prod` profiles; tests run on in-memory H2)

## One-time MySQL setup

Create the database and the application account (the backend never connects as root):

```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql" -u root -p < docs/mysql-setup.sql
```

That creates the `medbook` database and a `medbook`/`medbook` app user scoped to it. There is no separate roles table to seed: user roles (`ADMIN`, `DOCTOR`, `PATIENT`, `STAFF`) are stored on each `users` row and every table is created automatically by Hibernate on first boot. The first boot also seeds a bootstrap admin (`admin@medbook.local` / `Admin@12345` in dev).

## Getting started

### Backend

```bash
cd medbook_backend
./mvnw spring-boot:run            # dev profile: MySQL on localhost:3306
```

The API starts on `http://localhost:8080/api/v1`. Override any database setting with environment variables (`DB_USERNAME`, `DB_PASSWORD`, `DB_HOST`, ...) — see the configuration table below.

### Frontend

```bash
cd medbook_frontend
npm install
npm start                         # http://localhost:4200, proxies to the API URL in environment.ts
```

`npm run build` produces a production build with `src/app/environments/environment.prod.ts` swapped in (`apiUrl: '/api/v1'`, for serving behind the same origin as the API or a reverse proxy).

## Configuration

| Environment variable         | Default                        | Purpose                          |
|------------------------------|--------------------------------|----------------------------------|
| `SPRING_PROFILES_ACTIVE`     | `dev`                          | `dev` (local MySQL) or `prod`    |
| `SERVER_PORT`                | `8080`                         | HTTP port                        |
| `JWT_SECRET`                 | dev fallback                   | Base64 HMAC secret (≥ 32 bytes)  |
| `JWT_ACCESS_TOKEN_EXPIRATION`| `900000` (15 min)              | Access token TTL in ms           |
| `JWT_REFRESH_TOKEN_EXPIRATION`| `604800000` (7 days)          | Refresh token TTL in ms          |
| `CORS_ALLOWED_ORIGINS`       | `http://localhost:4200`        | Comma-separated allowed origins  |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `medbook` | MySQL connection      |
| `DB_USERNAME` / `DB_PASSWORD` | `medbook` / `medbook`         | App database account             |
| `UPLOAD_DIR`                 | `uploads`                      | File storage directory           |
| `COOKIE_SECURE`              | `false`                        | Set `true` when the API is served over HTTPS |
| `COOKIE_SAME_SITE`           | `Lax`                          | `Lax` (same-site SPA) or `None` (cross-site, requires Secure) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | dev seeds `admin@medbook.local` | Bootstrap admin account  |

### Role-based navigation

Each role lands on its own page after login and sees only its relevant navigation:

| Role    | Landing page    | Sidebar items                                        |
|---------|-----------------|------------------------------------------------------|
| ADMIN   | `/admin`        | everything incl. User Management                     |
| DOCTOR  | `/appointments` | dashboard, appointments, patients, schedule, records, prescriptions, files |
| STAFF   | `/appointments` | dashboard, appointments, doctors, patients, schedule, payments |
| PATIENT | `/dashboard`    | dashboard, appointments, doctors, records, prescriptions, payments, files |

## Authentication & security

The SPA authenticates with **HttpOnly cookies** — token material is never exposed to JavaScript:

- `MEDBOOK_ACCESS_TOKEN` — short-lived JWT (15 min), `HttpOnly`, scoped to the API context path `/api/v1`
- `MEDBOOK_REFRESH_TOKEN` — long-lived JWT (7 days), `HttpOnly`, scoped to `/api/v1/auth` so it only travels on `/auth/refresh` and `/auth/logout`; it is **rotated on every refresh**
- Because cookies authenticate requests, CSRF is enforced with Spring Security 7's `csrf.spa()` double-submit setup: the `XSRF-TOKEN` cookie is echoed in the `X-XSRF-TOKEN` header by the SPA, and a fresh token is re-issued as authentication succeeds
- The frontend keeps no tokens in localStorage; the session is resolved at boot via `GET /auth/me` and expired access cookies are refreshed transparently by a single-flight interceptor

**Role model:** `ADMIN`, `DOCTOR`, `PATIENT`, `STAFF`.

- Public self-registration (`POST /auth/register`) always creates a `PATIENT` account
- Privileged roles are provisioned only through the admin module: `POST /admin/users` (ADMIN-only) creates users with any role, requiring a medical license number for doctors
- `/admin/**` requires `ADMIN`; the Angular admin area is guarded by the same role check and hidden from non-admin navigation
- Admins can enable/disable accounts (`PATCH /admin/users/{id}/status`); disabled accounts cannot log in

A bootstrap admin is seeded in the `dev` profile (`admin@medbook.local` / `Admin@12345`, configurable via `app.bootstrap.admin.*`). Keep `app.bootstrap.admin.email` unset in production.

## API overview

Base path: `/api/v1`

| Endpoint               | Method | Auth     | Description                                     |
|------------------------|--------|----------|-------------------------------------------------|
| `/auth/csrf`           | GET    | public   | Prime the CSRF cookie for the SPA               |
| `/auth/register`       | POST   | public   | Self-register a patient account                 |
| `/auth/login`          | POST   | public   | Authenticate; sets HttpOnly auth cookies        |
| `/auth/refresh`        | POST   | refresh cookie | Rotate access + refresh cookies           |
| `/auth/me`             | GET    | user     | Current authenticated user                      |
| `/auth/logout`         | POST   | public   | Clear auth cookies                              |
| `/admin/users`         | GET    | ADMIN    | Paged user list with search                     |
| `/admin/users`         | POST   | ADMIN    | Create a user with any role                     |
| `/admin/users/{id}/status` | PATCH | ADMIN | Enable or disable an account                    |

Domain endpoints for doctors, patients, appointments, schedules, medical records, prescriptions, payments, notifications, and files are scaffolded as entities/repositories and DTOs; controllers and services land with each feature.

## Conventions

- **One git repository** at the `MedBook/` root covers both frontend and backend; there are no nested repositories.
- Entities extend `BaseEntity` for audit columns (`created_at`, `updated_at`, `created_by`, `updated_by`) populated by JPA auditing.
- All API errors use a common `ErrorResponse` shape produced by `GlobalExceptionHandler`.
- Validation annotations live on entities and request DTOs; reusable rules (`@ValidPhone`, `@FutureAppointment`) are in `common/validation`.
- Auth is cookie-based (HttpOnly) with CSRF double-submit — new endpoints must not accept tokens in request/response bodies.
