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
- MySQL 8+ (only for the `prod` profile)

## Getting started

### Backend

```bash
cd medbook_backend
./mvnw spring-boot:run            # dev profile: H2 in-memory, h2-console at /api/v1/h2-console
```

The API starts on `http://localhost:8080/api/v1`.

To run against MySQL, use the prod profile with environment variables:

```bash
SPRING_PROFILES_ACTIVE=prod \
DB_URL=jdbc:mysql://localhost:3306/medbook \
DB_USERNAME=medbook \
DB_PASSWORD=secret \
JWT_SECRET=<base64-encoded-secret> \
./mvnw spring-boot:run
```

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
| `SPRING_PROFILES_ACTIVE`     | `dev`                          | `dev` (H2) or `prod` (MySQL)     |
| `SERVER_PORT`                | `8080`                         | HTTP port                        |
| `JWT_SECRET`                 | dev fallback                   | Base64 HMAC secret (≥ 32 bytes)  |
| `JWT_ACCESS_TOKEN_EXPIRATION`| `900000` (15 min)              | Access token TTL in ms           |
| `JWT_REFRESH_TOKEN_EXPIRATION`| `604800000` (7 days)          | Refresh token TTL in ms          |
| `CORS_ALLOWED_ORIGINS`       | `http://localhost:4200`        | Comma-separated allowed origins  |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | local MySQL        | Prod datasource                  |
| `UPLOAD_DIR`                 | `uploads`                      | File storage directory           |

## API overview

Base path: `/api/v1`

| Endpoint          | Method | Description                        |
|-------------------|--------|------------------------------------|
| `/auth/register`  | POST   | Create a patient account           |
| `/auth/login`     | POST   | Authenticate, returns access + refresh tokens |
| `/auth/refresh`   | POST   | Exchange refresh token for new tokens |
| `/auth/me`        | GET    | Current authenticated user         |
| `/auth/logout`    | POST   | Client-side token discard          |

Domain endpoints for doctors, patients, appointments, schedules, medical records, prescriptions, payments, notifications, and files are scaffolded as entities/repositories and DTOs; controllers and services land with each feature.

## Conventions

- **One git repository** at the `MedBook/` root covers both frontend and backend; there are no nested repositories.
- Entities extend `BaseEntity` for audit columns (`created_at`, `updated_at`, `created_by`, `updated_by`) populated by JPA auditing.
- All API errors use a common `ErrorResponse` shape produced by `GlobalExceptionHandler`.
- Validation annotations live on entities and request DTOs; reusable rules (`@ValidPhone`, `@FutureAppointment`) are in `common/validation`.
