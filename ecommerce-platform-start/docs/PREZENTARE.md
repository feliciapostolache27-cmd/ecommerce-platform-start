# Ghid de prezentare (10–15 minute)

## 1. Arhitectura
Doua servicii Spring Boot + PostgreSQL + Kafka, toate in Docker Compose. Frontend-ul vorbeste doar cu `backend`.
`backend` scrie in PostgreSQL si, la crearea unui task, trimite un eveniment pe Kafka. `notification-service` il consuma si il afiseaza in log.

## 2. Controller / Service / Repository
- **Controller**: primeste HTTP, valideaza DTO-ul (`@Valid`), deleaga. Nu are logica de business.
- **Service**: regulile aplicatiei (status implicit, verificarea proprietatii, tranzactii, mapare Entity -> DTO).
- **Repository**: interfata `JpaRepository`; Spring genereaza implementarea. Metode derivate din nume (`findByIdAndOwnerId`).

## 3. Cum functioneaza JWT
Login -> serverul verifica parola cu BCrypt -> semneaza un token (id, email, rol, expirare 1h) cu o cheie secreta.
Clientul trimite `Authorization: Bearer <token>`. `JwtFilter` valideaza semnatura si expirarea si pune userul in `SecurityContext`.
Serverul nu tine sesiuni (stateless).

## 4. Cum sunt protejate endpoint-urile
`SecurityConfig`: `/api/auth/register`, `/login`, catalogul si Swagger sunt publice; orice altceva cere token valid, altfel **401**.
Parolele: doar hash BCrypt. Rolul ADMIN nu poate fi cerut la inregistrare.
Izolarea datelor: userul curent vine din token, iar repository-ul cauta task-ul dupa `id` SI `owner`. Un task strain da **404** (nu dezvaluim ca exista).

## 5. Baza de date
Tabele: `users`, `tasks` (FK `user_id` -> users, relatie 1:N), `categories`, `products` (FK `category_id`).
Hibernate le creeaza (`ddl-auto: update`); intr-un proiect real: migrari Flyway.

## 6. Comunicarea intre servicii
Asincrona, prin Kafka (nu apel REST direct). Backend-ul nu stie cine consuma evenimentul.
Contractul este forma JSON-ului; serviciile nu partajeaza cod (fiecare are propria clasa `TaskCreatedEvent`).

## 7. Rolul Kafka
Decupleaza serviciile: crearea task-ului nu asteapta notificarea si nu cade daca `notification-service` e oprit
(mesajele raman in topic si sunt procesate la repornire). Evenimentul se trimite **dupa commit-ul** din PostgreSQL
(`@TransactionalEventListener(AFTER_COMMIT)`), deci nu se anunta task-uri care nu s-au salvat.

## 8. Containerizare
Fiecare serviciu are un `Dockerfile` in doua etape (Maven build -> imagine mica cu JRE).
`docker-compose.yml` porneste postgres, kafka (KRaft), backend, notification-service si frontend; backend-ul asteapta ca Postgres sa fie healthy.
Configuratia vine din variabile de mediu (`DB_URL`, `JWT_SECRET`, `KAFKA_BOOTSTRAP_SERVERS`), nu din cod.

## 9. Ce verifica testele
- `TaskServiceTest` (Mockito): creare cu status implicit + eveniment publicat; modificare; stergere task strain -> 404.
- `TaskControllerTest`: fara token -> 401; un user vede doar task-urile lui; nu poate citi/modifica/sterge task-ul altuia.
- `AuthControllerTest`: register/login, email duplicat 409, parola gresita 401, email invalid 400.
- `PublicEndpointsTest`: consola `/api/practice` si catalogul.

## 10. Scalare si imbunatatiri
- Mai multe instante de backend in spate unui load balancer (stateless datorita JWT).
- Notification-service scalat prin *consumer group* Kafka; topic cu mai multe partitii.
- Cache Redis pentru catalog; migrari Flyway; refresh token; paginare pe `/api/tasks`.
- *Outbox pattern* pentru garantia livrarii evenimentelor; observabilitate (Actuator, Prometheus); CI cu GitHub Actions.
