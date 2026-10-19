# MedBook

MedBook is a full-stack healthcare management application with a Spring Boot backend and an Angular frontend.

## Project structure

- `medbook_backend/` — Spring Boot REST API and JPA entities
- `medbook_frontend/` — Angular application for the patient and admin experience

## Tech stack

- Backend: Java 17, Spring Boot 3.3.x, Spring Security, Spring Data JPA, MySQL
- Frontend: Angular 21, TypeScript, Tailwind CSS

## Local development

### Backend

```bash
cd medbook_backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd medbook_frontend
npm install
npm start
```

## Notes

- Build artifacts and editor metadata are intentionally excluded from source control.
- The project is structured as a clean monorepo with separate backend and frontend concerns.
