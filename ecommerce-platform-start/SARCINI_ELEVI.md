# Sarcini pentru elevi — backend-ul Java

Tot proiectul este gata (frontend, Docker, securitate JWT, DTO-uri, exceptii, Kafka, teste),
**in afara de 5 foldere** pe care le scrieti voi, de mana:

    backend/src/main/java/com/ecommerce/
    ├── model/        entitatile (tabelele din baza de date)
    ├── repository/   accesul la baza de date
    ├── config/       configurari (securitate, CORS, Swagger, date de start)
    ├── service/      logica aplicatiei
    └── controller/   rutele API (/api/...)

Folderele sunt **goale**. Voi creati fiecare clasa: click dreapta pe folder → **New → Java Class**,
scrieti numele exact ca mai jos (pentru enum/interfata alegeti tipul din aceeasi fereastra).
Mai jos, la „Ce trebuie scris”, gasiti pentru fiecare clasa campurile, metodele, adnotarile si rutele.

(Fisierul `.gitkeep` din fiecare folder exista doar ca GitHub sa pastreze folderul gol — il puteti ignora.)

## Ordinea recomandata
Lucrati de jos in sus, pentru ca fiecare strat il foloseste pe cel dinainte:

1. **model/** — `Role`, `TaskStatus`, `Category`, `User`, `Product`, `Task`
2. **repository/** — `CategoryRepository`, `UserRepository`, `ProductRepository`, `TaskRepository`
3. **config/** — `CacheConfig`, `OpenApiConfig`, `CorsConfig`, `SecurityConfig`, `DataSeeder`
4. **service/** — `CategoryService`, `ProductService`, `AuthService`, `TaskService`
5. **controller/** — `HealthController`, `PracticeController`, `CategoryController`,
   `ProductController`, `ConfigController`, `AuthController`, `TaskController`

> Proiectul **nu se compileaza** pana nu sunt scrise toate clasele (celelalte pachete le folosesc).
> Erorile rosii din IntelliJ dispar pe masura ce avansati — e normal.

## Cum va verificati
- **Teste:** `cd backend && mvn test` (sau click dreapta pe folderul `test` → *Run 'All Tests'*).
  `TaskServiceTest`, `AuthControllerTest`, `TaskControllerTest` si `PublicEndpointsTest` verifica munca voastra.
- **Swagger:** porniti aplicatia si deschideti http://localhost:8080/swagger-ui.html
- **Frontend:** `frontend/index.html` — la Lectia 1 consola trebuie sa arate **5 / 5**.

Pornire: vezi `README.md` (sectiunea „Dezvoltare locala”).

## Reguli
- Nu modificati fisierele din afara celor 5 foldere.
- Numele claselor, metodelor si campurilor trebuie sa fie **exact** ca in sarcina —
  restul proiectului si testele se bazeaza pe ele.
- Fiecare sarcina are si o intrebare. Raspundeti in comentariu, in codul vostru.

---

# Ce trebuie scris

## 1. model/

### `Role.java`
```text
Scrie un ENUM (nu clasa) numit Role, cu doua valori:
  USER, ADMIN
```

### `TaskStatus.java`
```text
Scrie un ENUM numit TaskStatus, cu trei valori (in aceasta ordine):
  TODO, IN_PROGRESS, DONE
```

### `Category.java`
```text
Scrie clasa Category = entitate JPA pentru tabelul "categories".

Adnotari pe clasa: @Entity, @Table(name = "categories")

Campuri (private):
  - Long id       -> @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
  - String name   -> @Column(nullable = false, unique = true)

Metode:
  - getId()
  - getName(), setName(String name)

Importuri utile: jakarta.persistence.*
```

### `User.java`
```text
Scrie clasa User = entitate JPA pentru tabelul "users".
(Atentie: "user" e cuvant rezervat in PostgreSQL, de aceea tabelul se numeste "users".)

Adnotari pe clasa: @Entity, @Table(name = "users")

Campuri (private):
  - Long id               -> @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
  - String email          -> @Column(nullable = false, unique = true)
  - String passwordHash   -> @Column(name = "password_hash", nullable = false)
  - Role role             -> @Enumerated(EnumType.STRING) + @Column(nullable = false, length = 20)
  - Instant createdAt     -> @Column(name = "created_at", nullable = false, updatable = false)

O metoda cu @PrePersist (ex. void onCreate()) care seteaza createdAt = Instant.now()
inainte de prima salvare in baza de date.

Metode: getId(), getCreatedAt(), plus get/set pentru email, passwordHash, role.

Intrebare: de ce salvam passwordHash si NU parola in clar?
```

### `Product.java`
```text
Scrie clasa Product = entitate JPA pentru tabelul "products".

Adnotari pe clasa: @Entity, @Table(name = "products")

Campuri (private):
  - Long id                -> @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
  - String name            -> @Column(nullable = false)
  - String description     -> @Column(length = 2000)
  - BigDecimal price       -> @Column(nullable = false, precision = 10, scale = 2)
  - int stock              -> @Column(nullable = false)
  - Category category      -> relatie "multe produse la o categorie":
                              @ManyToOne(fetch = FetchType.LAZY)
                              @JoinColumn(name = "category_id")

Metode: getId(), plus get/set pentru name, description, price, stock, category.

Intrebare pentru tine: de ce folosim BigDecimal si nu double pentru pret?
```

### `Task.java`
```text
Scrie clasa Task = entitate JPA pentru tabelul "tasks".
Fiecare task apartine unui singur utilizator (owner).

Adnotari pe clasa: @Entity, @Table(name = "tasks")

Campuri (private):
  - Long id              -> @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
  - String title         -> @Column(nullable = false, length = 150)
  - String description   -> @Column(length = 2000)
  - TaskStatus status    -> @Enumerated(EnumType.STRING) + @Column(nullable = false, length = 20)
  - Instant createdAt    -> @Column(name = "created_at", nullable = false, updatable = false)
  - User owner           -> relatie "multe task-uri la un user":
                            @ManyToOne(fetch = FetchType.LAZY, optional = false)
                            @JoinColumn(name = "user_id", nullable = false)

O metoda cu @PrePersist care seteaza createdAt = Instant.now().

Metode: getId(), getCreatedAt(), plus get/set pentru title, description, status, owner.
(Numele "owner" conteaza: repository-ul foloseste findByOwnerId...)
```


## 2. repository/

### `CategoryRepository.java`
```text
Scrie INTERFATA CategoryRepository care extinde JpaRepository<Category, Long>.
Nu are nevoie de metode proprii: findAll(), findById(), save(), count() vin gata facute.
```

### `UserRepository.java`
```text
Scrie INTERFATA UserRepository care extinde JpaRepository<User, Long>.

Metode (Spring le implementeaza singur dupa nume, nu scrii SQL):
  - Optional<User> findByEmail(String email)
  - boolean existsByEmail(String email)
```

### `ProductRepository.java`
```text
Scrie INTERFATA ProductRepository care extinde JpaRepository<Product, Long>.

Metode:
  - List<Product> findAll()                        -> @Override + @EntityGraph(attributePaths = "category")
  - List<Product> findByCategoryId(Long categoryId) -> @EntityGraph(attributePaths = "category")
  - Optional<Product> findById(Long id)            -> @Override + @EntityGraph(attributePaths = "category")
  - List<Product> findByNameContainingIgnoreCase(String name)

@EntityGraph incarca si categoria in aceeasi interogare (evita problema "N+1" la LAZY).
Import: org.springframework.data.jpa.repository.EntityGraph
```

### `TaskRepository.java`
```text
Scrie INTERFATA TaskRepository care extinde JpaRepository<Task, Long>.

Metode:
  - List<Task> findByOwnerIdOrderByCreatedAtDesc(Long ownerId)
        -> toate task-urile unui user, cele mai noi primele
  - List<Task> findByOwnerIdAndStatusOrderByCreatedAtDesc(Long ownerId, TaskStatus status)
        -> la fel, dar doar cu un anumit status
  - Optional<Task> findByIdAndOwnerId(Long id, Long ownerId)
        -> cauta un task DOAR printre task-urile userului
           (asa un user nu poate citi/modifica/sterge task-ul altcuiva)
```


## 3. config/

### `CacheConfig.java`
```text
Scrie clasa CacheConfig care activeaza cache-ul in aplicatie.
Adnotari pe clasa: @Configuration si @EnableCaching. Clasa ramane goala.
```

### `OpenApiConfig.java`
```text
Scrie clasa OpenApiConfig pentru documentatia Swagger (/swagger-ui.html).

Adnotari pe clasa:
  @Configuration
  @OpenAPIDefinition(info = @Info(title = "E-Commerce & Tasks API", version = "1.0",
                                  description = "..." ))
  @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP,
                  scheme = "bearer", bearerFormat = "JWT")

Numele "bearerAuth" este folosit in controllere (@SecurityRequirement(name = "bearerAuth")).
Clasa ramane goala. Importuri: io.swagger.v3.oas.annotations.*
```

### `CorsConfig.java`
```text
Scrie clasa CorsConfig: permite frontend-ului (alt port) sa apeleze API-ul.

  - @Configuration, clasa implementeaza WebMvcConfigurer
  - camp: @Value("${app.cors.allowed-origins}") private String[] allowedOrigins;
  - suprascrie addCorsMappings(CorsRegistry registry):
        pentru "/api/**" permite:
          originile din allowedOrigins
          metodele GET, POST, PUT, PATCH, DELETE, OPTIONS
          orice header ("*")
          maxAge 3600

Intrebare: ce se intampla in browser daca uiti sa pui PATCH in lista?
```

### `SecurityConfig.java`
```text
Scrie clasa SecurityConfig (Spring Security + JWT).

Adnotari pe clasa: @Configuration, @EnableMethodSecurity

1) @Bean PasswordEncoder passwordEncoder()  -> intoarce new BCryptPasswordEncoder()

2) @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService):
   - dezactiveaza CSRF (API stateless cu JWT)
   - cors(Customizer.withDefaults())
   - sesiuni STATELESS (SessionCreationPolicy.STATELESS)
   - reguli de acces:
        PUBLIC:
          OPTIONS  /**
          POST     /api/auth/register, /api/auth/login
          GET      /api/products/**, /api/categories/**, /api/health, /api/config/info
          POST     /api/cache/clear
          orice    /api/practice/**
          orice    /swagger-ui.html, /swagger-ui/**, /v3/api-docs/**, /error
        TOT RESTUL: authenticated()
   - la cerere neautentificata raspunde 401 cu JSON:
        {"message":"Neautentificat: token lipsa sau invalid"}
        (exceptionHandling -> authenticationEntryPoint)
   - adauga filtrul: new JwtFilter(jwtService) INAINTE de UsernamePasswordAuthenticationFilter
   - return http.build();

Clasele JwtFilter si JwtService exista deja in pachetul security.
```

### `DataSeeder.java`
```text
Scrie clasa DataSeeder: creeaza date de start la pornirea aplicatiei (doar daca lipsesc).

  - @Component, implementeaza CommandLineRunner
  - primeste prin constructor: UserRepository, CategoryRepository, ProductRepository, PasswordEncoder
    si valorile:
        @Value("${app.admin.email}")                      String adminEmail
        @Value("${app.admin.password}")                   String adminPassword
        @Value("${app.demo-user.email:user@impact.md}")   String demoEmail
        @Value("${app.demo-user.password:user123}")       String demoPassword

  - run(String... args), cu @Transactional:
      1. creeaza userul ADMIN (adminEmail/adminPassword, Role.ADMIN) daca nu exista
      2. creeaza userul demo (demoEmail/demoPassword, Role.USER) daca nu exista
         (verifici cu existsByEmail; parola se salveaza cu passwordEncoder.encode(...))
      3. daca nu exista nicio categorie (count() == 0):
           categorii: "Îmbrăcăminte", "Accesorii"
           produse (nume, descriere, pret, stoc, categorie):
             Tricou Impact        | Tricou din bumbac cu logo Impact   | 199 | 40  | Îmbrăcăminte
             Hanorac cu glugă     | Hanorac călduros, unisex           | 499 | 18  | Îmbrăcăminte
             Cană de cafea        | Cană ceramică de 350 ml            | 129 | 75  | Accesorii
             Rucsac pentru laptop | Rucsac rezistent la apă            | 899 | 12  | Accesorii
             Set autocolante      | Set de 10 autocolante              | 49  | 200 | Accesorii
             Șapcă snapback       | Șapcă cu cozoroc drept             | 179 | 33  | Îmbrăcăminte

Sfat: fa metode private ajutatoare, ex. createUserIfMissing(...), category(...), product(...).
```


## 4. service/

### `CategoryService.java`
```text
Scrie clasa CategoryService (@Service).

  - primeste CategoryRepository prin constructor
  - public List<CategoryResponse> list()   cu @Transactional(readOnly = true)
        -> ia toate categoriile si le transforma in CategoryResponse(id, name)
           (foloseste stream().map(...).toList())
```

### `ProductService.java`
```text
Scrie clasa ProductService (@Service).

  - primeste ProductRepository prin constructor

  - public List<ProductResponse> list(Long categoryId)
        @Cacheable(cacheNames = "products", key = "#categoryId == null ? 'all' : #categoryId")
        @Transactional(readOnly = true)
        -> daca categoryId e null: toate produsele, altfel findByCategoryId(categoryId)
        -> transforma fiecare Product in ProductResponse

  - public ProductResponse get(Long id)    cu @Transactional(readOnly = true)
        -> daca produsul nu exista: throw new ApiException(HttpStatus.NOT_FOUND, "Produsul nu a fost gasit")

  - metoda privata toResponse(Product p):
        new ProductResponse(id, name, description, NUMELE categoriei (sau null), price, stock)
```

### `AuthService.java`
```text
Scrie clasa AuthService (@Service): inregistrare, login, userul curent.

Primeste prin constructor: UserRepository, PasswordEncoder, JwtService.

  - public AuthResponse register(RegisterRequest request)      @Transactional
        1. emailul se "normalizeaza": trim() + toLowerCase(Locale.ROOT)
        2. daca emailul exista deja -> throw new ApiException(HttpStatus.CONFLICT, "Email-ul este deja folosit")
        3. creeaza User: email, passwordHash = passwordEncoder.encode(parola), role = Role.USER
           (rolul NU vine niciodata de la client!)
        4. salveaza si intoarce new AuthResponse(jwtService.generateToken(user), email, role)

  - public AuthResponse login(LoginRequest request)            @Transactional(readOnly = true)
        - cauta userul dupa email (normalizat)
        - daca nu exista SAU parola nu se potriveste (passwordEncoder.matches):
              throw new ApiException(HttpStatus.UNAUTHORIZED, "Email sau parola incorecte")
          (acelasi mesaj in ambele cazuri - de ce?)
        - altfel intoarce AuthResponse, ca la register

  - public UserResponse me(Long userId)                        @Transactional(readOnly = true)
        - daca userul nu exista: ApiException(HttpStatus.UNAUTHORIZED, "Utilizatorul nu mai exista")
        - intoarce new UserResponse(id, email, role)
```

### `TaskService.java`
```text
Scrie clasa TaskService (@Service): logica pentru task-uri.
Fiecare metoda primeste userId (din token) si lucreaza DOAR cu task-urile acelui user.

Primeste prin constructor: TaskRepository, UserRepository, ApplicationEventPublisher.

  - public List<TaskResponse> list(Long userId, TaskStatus status)      @Transactional(readOnly = true)
        status null -> toate task-urile userului; altfel doar cele cu acel status (cele mai noi primele)

  - public TaskResponse get(Long userId, Long taskId)                   @Transactional(readOnly = true)

  - public TaskResponse create(Long userId, CreateTaskRequest request)  @Transactional
        1. gaseste userul (daca lipseste: ApiException(UNAUTHORIZED, "Utilizatorul nu mai exista"))
        2. Task nou: title = request.title().trim(), description,
           status = request.status() sau TaskStatus.TODO daca e null, owner = userul
        3. salveaza
        4. publica evenimentul:
           events.publishEvent(new TaskCreatedEvent(saved.getId(), owner.getId(), owner.getEmail(),
                                                    saved.getTitle(), saved.getCreatedAt()));
        5. intoarce TaskResponse

  - public TaskResponse update(Long userId, Long taskId, UpdateTaskRequest request)  @Transactional
        seteaza title (trim), description, status. Nu e nevoie de save() - de ce?

  - public void delete(Long userId, Long taskId)                        @Transactional

  - metoda privata findOwned(userId, taskId):
        taskRepository.findByIdAndOwnerId(taskId, userId)
        daca nu exista -> ApiException(HttpStatus.NOT_FOUND, "Task-ul nu a fost gasit")

  - metoda privata toResponse(Task t): new TaskResponse(id, title, description, status, createdAt)

Testul TaskServiceTest verifica aceasta clasa: ruleaza-l dupa ce termini.
```


## 5. controller/

### `HealthController.java`
```text
Scrie clasa HealthController (@RestController, @RequestMapping("/api/health")).

  GET /api/health  -> intoarce Map.of("status", "UP")

Optional (Swagger): @Tag(name = "Utility") pe clasa, @Operation(summary = "...") pe metoda.
```

### `PracticeController.java`
```text
Lectia 1 - consola de practica din frontend (tinta: 5 / 5).

Scrie clasa PracticeController:
  @RestController
  @RequestMapping(value = "/api/practice", produces = MediaType.TEXT_PLAIN_VALUE)

Cate o metoda pentru fiecare verb HTTP. Cele cu body primesc
@RequestBody(required = false) String body (daca body e null, foloseste "").

  GET     -> "api initialised"
  POST    -> "youve posted: " + body
  PUT     -> "your update is : " + body
  PATCH   -> "you have updated the : " + body
  DELETE  -> "youve deleted : " + body

Textele trebuie sa fie EXACT asa (cu spatiile lor), altfel frontend-ul nu le accepta.
```

### `CategoryController.java`
```text
Scrie clasa CategoryController (@RestController, @RequestMapping("/api/categories")).

  - primeste CategoryService prin constructor
  - GET /api/categories -> intoarce categoryService.list()
```

### `ProductController.java`
```text
Scrie clasa ProductController (@RestController, @RequestMapping("/api/products")).

  - primeste ProductService prin constructor
  - GET /api/products              -> productService.list(categoryId)
        parametru optional din URL: @RequestParam(name = "category", required = false) Long categoryId
        (ex. /api/products?category=2)
  - GET /api/products/{id}         -> productService.get(id)   (@PathVariable Long id)
```

### `ConfigController.java`
```text
Scrie clasa ConfigController (@RestController, @RequestMapping("/api")).

Primeste prin constructor: Environment, CacheManager si
@Value("${spring.application.name}") String appName.

  - GET  /api/config/info
        -> Map.of("profile", <primul profil activ sau "dev" daca nu e niciunul>, "appName", appName)
           (profilele: environment.getActiveProfiles())
  - POST /api/cache/clear
        -> goleste toate cache-urile din cacheManager (getCacheNames(), getCache(name).clear())
        -> intoarce ResponseEntity.noContent().build()   (status 204)
```

### `AuthController.java`
```text
Scrie clasa AuthController (@RestController, @RequestMapping("/api/auth")).
Controllerul NU are logica: primeste cererea, valideaza si cheama AuthService.

  - primeste AuthService prin constructor

  - POST /api/auth/register
        parametru: @Valid @RequestBody RegisterRequest request
        raspuns: status 201 CREATED cu authService.register(request)
                 (ResponseEntity.status(HttpStatus.CREATED).body(...))

  - POST /api/auth/login
        parametru: @Valid @RequestBody LoginRequest request
        raspuns: authService.login(request)   (status 200)

  - GET  /api/auth/me        (cere token)
        parametru: @AuthenticationPrincipal AuthUser user
        raspuns: authService.me(user.id())

Optional (Swagger): @Tag, @Operation, @SecurityRequirement(name = "bearerAuth") pe /me.
```

### `TaskController.java`
```text
Scrie clasa TaskController (@RestController, @RequestMapping("/api/tasks")).
Toate rutele cer token. Userul curent vine din token cu @AuthenticationPrincipal AuthUser user,
NU din URL sau body - asa nimeni nu poate cere task-urile altcuiva.

Pune pe clasa si @SecurityRequirement(name = "bearerAuth") (pentru lacatul din Swagger).
Primeste TaskService prin constructor.

  GET    /api/tasks            -> taskService.list(user.id(), status)
                                  @RequestParam(required = false) TaskStatus status
  GET    /api/tasks/{id}       -> taskService.get(user.id(), id)
  POST   /api/tasks            -> @Valid @RequestBody CreateTaskRequest
                                  raspuns 201 CREATED cu taskService.create(...)
  PUT    /api/tasks/{id}       -> @Valid @RequestBody UpdateTaskRequest
                                  raspuns taskService.update(user.id(), id, request)
  DELETE /api/tasks/{id}       -> taskService.delete(user.id(), id)
                                  raspuns 204: ResponseEntity.noContent().build()
```

