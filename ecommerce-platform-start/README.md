# E-Commerce & Tasks — backend Spring Boot (Anul 2)

> **Elevi:** incepeti cu [`SARCINI_ELEVI.md`](SARCINI_ELEVI.md) — acolo e ce aveti de scris.

Sistem cu doua servicii Java, PostgreSQL, Kafka, JWT, Swagger, Docker Compose si frontend-ul cursului.

```
 Frontend (nginx :3000)
        │  HTTP + JWT
        ▼
 backend (Spring Boot :8080) ──JDBC──▶ PostgreSQL :5432
        │
        │  eveniment "task-created" (JSON)
        ▼
     Kafka (topic task-created)
        │
        ▼
 notification-service (Spring Boot) ──▶ afiseaza notificarea in log
```

## Pornire rapida (totul in Docker)
Necesita doar Docker Desktop.

    docker compose up --build

| Ce                | Adresa                                   |
|-------------------|------------------------------------------|
| Frontend          | http://localhost:3000                    |
| API               | http://localhost:8080                    |
| Swagger UI        | http://localhost:8080/swagger-ui.html    |
| PostgreSQL        | localhost:5432 (ecommerce / ecommerce)   |

Notificarile se vad cu:  `docker compose logs -f notification-service`

Conturi create automat: `user@impact.md / user123` (USER), `admin@shop.local / admin123` (ADMIN).

## Dezvoltare locala (backend din IDE / mvn)
    docker compose up -d postgres kafka
    cd backend && mvn spring-boot:run
    cd notification-service && mvn spring-boot:run        # in alt terminal
Valorile implicite (localhost:5432, localhost:9094) se potrivesc cu portile publicate de compose.

## Frontend
`frontend/index.html` este build-ul frontend-ului cursului (https://github.com/Victoras23/impact_2_year_fe).
In bara de sus alege lectia: la Lectia 1 apare consola cu cele 5 verbe HTTP (`/api/practice`, tinta 5/5),
de la Lectia 2 apar catalogul (din PostgreSQL) si login/inregistrare.
Se poate deschide si direct cu dublu-click; backend-ul accepta originea `null` a fisierelor locale (CORS).

## Endpoint-uri
| Metoda | URL | Acces | Descriere |
|---|---|---|---|
| POST | /api/auth/register | public | cont nou (USER) -> `{token,email,role}` |
| POST | /api/auth/login | public | login -> `{token,email,role}` |
| GET | /api/auth/me | JWT | userul curent |
| GET | /api/tasks[?status=] | JWT | task-urile MELE |
| GET | /api/tasks/{id} | JWT | un task al meu |
| POST | /api/tasks | JWT | creeaza task + eveniment Kafka |
| PUT | /api/tasks/{id} | JWT | modifica un task al meu |
| DELETE | /api/tasks/{id} | JWT | sterge un task al meu |
| GET | /api/products[?category=] | public | catalog |
| GET | /api/products/{id} | public | un produs |
| GET | /api/categories | public | categorii |
| GET/POST/PUT/PATCH/DELETE | /api/practice | public | consola de practica (Lectia 1) |
| GET | /api/config/info, /api/health | public | profil activ, health |
| POST | /api/cache/clear | public | goleste cache-ul de produse |

Prefixul `/api` este cerut de frontend; cerinta din tema foloseste exemple fara prefix (`/tasks`).

## Structura backend-ului
    com.ecommerce
    ├── controller/   primesc cererea HTTP si deleaga (fara logica de business)
    ├── service/      logica de business, maparea Entity <-> DTO, tranzactii
    ├── repository/   acces la PostgreSQL (Spring Data JPA)
    ├── model/        entitati JPA: User, Task, Category, Product
    ├── dto/          obiectele din API (request/response) + validare
    ├── security/     JwtService, JwtFilter, AuthUser
    ├── config/       SecurityConfig, CorsConfig, OpenApiConfig, DataSeeder
    ├── exception/    ApiException + GlobalExceptionHandler
    └── messaging/    TaskCreatedEvent + TaskEventPublisher (Kafka)

## Teste (`cd backend && mvn test`, ruleaza pe H2, fara Docker)
- `TaskServiceTest` — test unitar cu Mockito: creare, modificare, sterge task strain -> 404
- `AuthControllerTest`, `PublicEndpointsTest` — teste de endpoint (register/login/validare, practice, catalog)
- `TaskControllerTest` — acces protejat (401 fara token) si izolarea datelor intre utilizatori

## Cerinte din tema -> unde sunt
| Cerinta | Unde |
|---|---|
| register / login / JWT | `AuthController`, `AuthService`, `JwtService`, `JwtFilter` |
| CRUD task-uri (titlu, descriere, status, data creare) | `Task`, `TaskController`, `TaskService` |
| fiecare user doar task-urile lui | `TaskRepository.findByIdAndOwnerId`, `TaskService.findOwned` |
| validare + erori | DTO-uri cu `@Valid`, `GlobalExceptionHandler` |
| parole criptate | BCrypt in `AuthService` / `SecurityConfig` |
| PostgreSQL + Docker Compose | `docker-compose.yml`, `application.yaml` |
| al doilea serviciu + Kafka | `notification-service/`, `messaging/TaskEventPublisher` |
| Swagger | `OpenApiConfig`, adnotarile din controllere, `/swagger-ui.html` |
| teste (minim 3) | `backend/src/test` |

Ghid pentru prezentarea la atestare: `docs/PREZENTARE.md`.
