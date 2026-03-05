# ComicHub — Product & Engineering Roadmap

> **Última atualização:** 2026-03-04
> **Metodologia:** TDD (Red → Green → Refactor) · Hexagonal Architecture · Vertical Slices
> **Stack:** Java 21 · Spring Boot 3.4 · PostgreSQL (Aurora) · AWS (S3, SQS, DynamoDB, EventBridge, Cognito)

---

## Legenda de Status

| Símbolo | Significado |
|---------|-------------|
| ✅ | Implementado e testado |
| 🚧 | Em construção / parcialmente implementado |
| 📋 | Planejado — próximo sprint |
| 🔮 | Planejado — sprints futuros |

---

## Sprint 0 — Fundação de Infraestrutura
**Data:** 2026-03-04

Estabelece o esqueleto do projeto e o ambiente de desenvolvimento local antes de qualquer linha de código de negócio.

| Feature | Status | Detalhes |
|---------|--------|----------|
| Estrutura hexagonal de pacotes | ✅ | `domain / application / infrastructure` por domínio de negócio |
| Spring Boot 3.4 + Java 21 | ✅ | `pom.xml` com BOM gerenciado, Java Records habilitado |
| Flyway (migrações SQL) | ✅ | V1–V4 aplicadas; convenção: `V{n}__descricao.sql` |
| Testcontainers (base de integração) | ✅ | `AbstractIntegrationTest` sobe PostgreSQL + LocalStack via Docker |
| LocalStack (emulação AWS) | ✅ | S3, DynamoDB, SQS, EventBridge, Secrets Manager |
| Docker Compose (infraestrutura) | ✅ | `docker-compose.infra.yml`: PostgreSQL 16 + LocalStack 3.8 |
| Script de inicialização LocalStack | ✅ | `infra/localstack/init/01-setup.sh`: buckets, filas, tabelas, secrets |
| `.gitignore` revisado para o projeto | ✅ | Maven, Spring Boot, IntelliJ, secrets, LocalStack data |
| Devcontainer | ✅ | `.devcontainer/devcontainer.json` |
| CI local via Maven Wrapper | ✅ | `mvn test` — 27 testes unitários passando |

**Migrations SQL aplicadas:**

```
V1 — items         (id, isbn, title, publisher, series, volume, variant, cover_image, created_at)
V2 — users         (id, email, created_at, auth_provider)
V3 — collections   (user_id ⟶ FK users, item_id ⟶ FK items, shelf_location, added_at)
V4 — collections   ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'OWNED'
```

---

## Sprint 1 — Módulo Catalog: Busca por ISBN
**Data:** 2026-03-04

Implementa o pipeline completo de ingestão de dados: do código de barras escaneado pelo usuário até a capa armazenada no S3.

| Feature | Status | Detalhes |
|---------|--------|----------|
| `SearchComicByIsbnUseCase` (port in) | ✅ | Interface pura no domínio, sem dependência de framework |
| `SearchComicByIsbnService` (use case) | ✅ | Orquestra: busca metadados → baixa capa → armazena no S3 |
| `FetchBookMetadataPort` (port out) | ✅ | Interface: `Optional<ComicBook> fetchByIsbn(String isbn)` |
| `CoverDownloadPort` (port out) | ✅ | Interface: `Optional<byte[]> download(String url)` |
| `ImageStoragePort` (port out) | ✅ | Interface: `String store(String isbn, byte[] bytes)` |
| `ComicBook` (domain model) | ✅ | Record Java: isbn, title, author, publisher, synopsis, coverImageUrl |
| `ComicBookNotFoundException` | ✅ | Exceção de domínio (sem Spring) |
| `GoogleBooksRestAdapter` | ✅ | Chama `/volumes?q=isbn:{isbn}`, mapeia resposta para `ComicBook` |
| `HttpCoverDownloadAdapter` | ✅ | `java.net.http.HttpClient` — retorna `Optional.empty()` em falha |
| `S3ImageStorageAdapter` | ✅ | AWS SDK v2: salva como `covers/{isbn}.webp`, Content-Type `image/webp` |
| `S3Config` | ✅ | Bean `S3Client` com suporte a endpoint LocalStack (`pathStyleAccess`) |
| Graceful degradation (S3) | ✅ | Se download falhar, retorna URL original da API externa |
| Testes unitários (5 testes) | ✅ | `SearchComicByIsbnServiceTest`, `GoogleBooksRestAdapterTest` (MockWebServer) |
| Teste de integração S3 | ✅ | `S3ImageStorageAdapterTest` (LocalStack — requer Docker) |

**Endpoints REST:** *(não exposto ainda — ver Sprint 3)*

---

## Sprint 2 — Módulo User: Autenticação com Cognito
**Data:** 2026-03-04

Configura o Spring Security como Resource Server OAuth2, valida JWTs do Amazon Cognito e provisiona usuários automaticamente no primeiro login.

| Feature | Status | Detalhes |
|---------|--------|----------|
| `User` (domain model) | ✅ | Record: `UUID id, String email, Instant createdAt, String authProvider` |
| `ProvisionUserUseCase` (port in) | ✅ | `provision(cognitoSub, email, authProvider)` → `User` |
| `UserRepository` (port out) | ✅ | `save(User)`, `findById(UUID)` |
| `ProvisionUserService` | ✅ | Find-or-create: busca por UUID do Cognito, cria se não existir |
| `UserEntity` (JPA) | ✅ | `@Entity @Table("users")` |
| `JpaUserRepositoryAdapter` | ✅ | Bridge entre Spring Data e o port de domínio |
| `SecurityConfig` | ✅ | STATELESS, CSRF off, JWT OAuth2 Resource Server, 401 sem token |
| `CognitoJwtConverter` | ✅ | Converte `Jwt` → `JwtAuthenticationToken`; chama `ProvisionUserService` |
| Lazy JWKS fetch | ✅ | `jwk-set-uri` (não conecta ao Cognito no startup — seguro em testes) |
| RFC 7807 Problem Details | ✅ | `spring.mvc.problemdetails.enabled=true` |
| Testes unitários (3 testes) | ✅ | `ProvisionUserServiceTest` |

---

## Sprint 3 — Módulo Collection: Estante Virtual
**Data:** 2026-03-04

Permite ao usuário adicionar itens à coleção e consultá-la com filtros. Primeira API REST protegida por JWT.

| Feature | Status | Detalhes |
|---------|--------|----------|
| `Collection` (domain model) | ✅ | Record: userId, itemId, shelfLocation, status, addedAt |
| `CollectionStatus` (enum) | ✅ | `OWNED`, `READING`, `LENT`, `READ` |
| `CollectionFilter` (record) | ✅ | `status` + `series`; factory `CollectionFilter.empty()` |
| `AddToCollectionUseCase` | ✅ | `add(userId, itemId, shelfLocation, status)` |
| `ListCollectionUseCase` | ✅ | `list(userId, CollectionFilter)` |
| `CollectionRepository` | ✅ | `save(Collection)`, `findByUserIdAndFilter(UUID, CollectionFilter)` |
| `AddToCollectionService` | ✅ | Grava `addedAt = Instant.now()`, persiste status |
| `ListCollectionService` | ✅ | Delega filtro ao repositório |
| `CollectionEntity` (JPA) | ✅ | Chave composta `@EmbeddedId(CollectionId)`, status `@Enumerated(STRING)` |
| `CollectionProjection` | ✅ | Interface para resultado da query nativa JOIN |
| `SpringDataCollectionRepository` | ✅ | Query nativa: `LEFT JOIN items` com filtros opcionais por status e series |
| `JpaCollectionRepositoryAdapter` | ✅ | Mapeia `CollectionProjection` → `Collection` (domínio) |
| `POST /api/v1/collections` | ✅ | Autenticado (JWT), status padrão `OWNED`, `201 Created` |
| `GET /api/v1/collections` | ✅ | Filtros `?status=&series=`, `200 OK`, lista paginável |
| Segurança: 401 sem token | ✅ | Validado por `CollectionControllerTest` |
| Testes unitários (6 testes) | ✅ | `AddToCollectionServiceTest`, `ListCollectionServiceTest` |
| Testes de controller (9 testes) | ✅ | `CollectionControllerTest` (`@WebMvcTest` + `jwt()` post-processor) |

---

## Sprint 4 — Catalog API + Persistência no Banco
**Status:** 📋 Próximo

Expõe o pipeline de ISBN como endpoint REST e persiste o `ComicBook` na tabela `items` do Aurora.

| Feature | Prioridade | Detalhes |
|---------|-----------|----------|
| `ItemRepository` (port out) | Alta | `save(ComicBook)`, `findByIsbn(String)`, `findById(UUID)` |
| `ItemEntity` (JPA) | Alta | `@Entity @Table("items")` mapeando todos os campos de V1 |
| `JpaItemRepositoryAdapter` | Alta | Adapter do port out para Spring Data |
| `PersistComicBookUseCase` | Alta | Salva `ComicBook` após pipeline de ingestão |
| Atualizar `SearchComicByIsbnService` | Alta | Chamar `ItemRepository.save()` após S3 (idempotente por ISBN) |
| `POST /api/v1/catalog/items/scan` | Alta | Body: `{ "isbn": "9788542615456" }` → `201 Created` com `ComicBook` |
| `GET /api/v1/catalog/items/{id}` | Média | Busca por UUID interno |
| `GET /api/v1/catalog/items?isbn={isbn}` | Média | Busca por ISBN (útil para verificar duplicatas antes de escanear) |
| `GET /api/v1/catalog/items?series={series}` | Média | Listagem por série para o Completion Tracker |
| Teste de integração Catalog API | Alta | `CatalogControllerTest` (`@WebMvcTest`) |
| V5 migration: índice `items(isbn)` | Alta | Garante unicidade e performance de busca |

---

## Sprint 5 — Módulo Wishlist
**Status:** 📋 Próximo

Permite ao usuário salvar itens que deseja comprar com um preço-alvo. Alimenta o Price Scanner Engine.

| Feature | Prioridade | Detalhes |
|---------|-----------|----------|
| V5 migration: `wishlists` table | Alta | `user_id`, `item_id`, `target_price`, `currency`, `created_at` |
| `Wishlist` (domain model) | Alta | Record com todos os campos acima |
| `WishlistStatus` (enum) | Média | `ACTIVE`, `FULFILLED`, `CANCELLED` |
| `AddToWishlistUseCase` | Alta | `add(userId, itemId, targetPrice, currency)` |
| `RemoveFromWishlistUseCase` | Alta | `remove(userId, itemId)` |
| `ListWishlistUseCase` | Alta | `list(userId)` — itens ativos do usuário |
| `WishlistRepository` (port out) | Alta | CRUD + `findActiveByItemId(UUID)` (para deduplicação do scanner) |
| `POST /api/v1/wishlist` | Alta | Adicionar item; valida se item existe no catálogo |
| `DELETE /api/v1/wishlist/{itemId}` | Alta | Remover item |
| `GET /api/v1/wishlist` | Alta | Listar wishlist do usuário com preço-alvo |
| Testes unitários | Alta | Use cases + controller |

---

## Sprint 6 — Price Scanner Engine
**Status:** 📋 Próximo

Worker assíncrono que monitora preços em lojas parceiras. Regra de ouro: uma verificação por item, independente de quantos usuários o monitoram.

| Feature | Prioridade | Detalhes |
|---------|-----------|----------|
| V6 migration: `price_history` table | Alta | `item_id`, `store`, `price`, `currency`, `in_stock`, `captured_at` |
| `PriceRecord` (domain model) | Alta | Record com os campos da `price_history` |
| `PriceScannerPort` (port out) | Alta | Interface: `PriceRecord scan(String isbn, String storeId)` |
| `DynamoDbLockAdapter` | Alta | Cria lock `store_id + isbn` com TTL antes de iniciar scan (sem duplicatas) |
| Worker SQS Consumer | Alta | `@SqsListener` — processa mensagens `{ "isbn": "...", "store_id": "..." }` |
| Deduplication por `isbn + store_id` | Alta | Verifica lock DynamoDB antes de enfileirar novo scan |
| `PriceHistoryRepository` | Alta | Salva `PriceRecord` após cada scan bem-sucedido |
| `EnqueuePriceScanUseCase` | Alta | Enfileira scan na SQS ao adicionar à Wishlist |
| EventBridge scheduler | Média | Re-escaneia diariamente itens ativos na wishlist |
| Adapter Amazon scraper (stub) | Média | Implementação inicial consultando Amazon PA-API |
| Testes unitários worker | Alta | Mock SQS + Mock DynamoDB |
| Teste de integração (LocalStack) | Média | SQS + DynamoDB real via Testcontainers |

---

## Sprint 7 — Alertas de Preço & Notificações Push
**Status:** 🔮 Futuro

Fecha o loop do Price Scanner: quando o preço cai abaixo do target, o usuário recebe uma notificação push em tempo real.

| Feature | Prioridade | Detalhes |
|---------|-----------|----------|
| V7 migration: `device_tokens` table | Alta | `user_id`, `token`, `platform` (iOS/Android), `created_at` |
| `RegisterDeviceTokenUseCase` | Alta | `POST /api/v1/users/me/device-token` |
| `NotificationPort` (port out) | Alta | Interface: `send(userId, title, body)` |
| `FcmNotificationAdapter` | Alta | Envia via Firebase Cloud Messaging (FCM) |
| `PriceAlertService` | Alta | Compara preço atual com `target_price`; dispara notificação se `≤` |
| EventBridge trigger | Alta | Evento `price.updated` → `PriceAlertService` |
| `POST /api/v1/feedback/purchase-intent` | Média | "Friction feedback": por que não comprou? (muito caro, frete alto…) |
| `notification_history` table | Média | Registro de todas as notificações enviadas |
| Testes unitários | Alta | `PriceAlertServiceTest`, `NotificationAdapterTest` |

---

## Sprint 8 — Social & Engagement Hub
**Status:** 🔮 Futuro

Monetização via afiliados e engajamento com influenciadores.

| Feature | Prioridade | Detalhes |
|---------|-----------|----------|
| Links de afiliado (Amazon Associates) | Alta | Gera URL de afiliado ao exibir botão "Comprar" |
| `AffiliateLink` (domain model) | Alta | Geração e rastreamento de cliques |
| `GET /api/v1/catalog/items/{id}/buy-link` | Alta | Retorna URL de afiliado gerada dinamicamente |
| Integração YouTube/TikTok | Média | `embedded_reviews` — URLs de review por item |
| `ReviewLink` (domain model) | Média | `item_id`, `platform`, `url`, `influencer_id` |
| `GET /api/v1/catalog/items/{id}/reviews` | Média | Lista reviews em vídeo do item |
| Revenue Share (afiliados) | Baixa | Lógica de split entre plataforma e influenciador |
| Completion Tracker | Média | Cruza estante do usuário com catálogo da série; alerta volumes faltantes |

---

## Sprint 9 — Publisher Analytics (B2B)
**Status:** 🔮 Futuro

Portal de inteligência de mercado para editoras. Transforma dados de comportamento anônimos em relatórios preditivos.

| Feature | Prioridade | Detalhes |
|---------|-----------|----------|
| Event ingestion pipeline | Alta | Eventos JSON → S3 Raw (`events/dt=YYYY-MM-DD/event_type=...`) |
| `DataLakeEventPort` (port out) | Alta | Interface: `publish(Event event)` |
| `S3DataLakeAdapter` | Alta | Serializa evento e salva em S3 com particionamento por data/tipo |
| ETL Raw → Silver (Glue Job) | Alta | JSON → Parquet; limpeza e validação de schema |
| ETL Silver → Gold (Athena View) | Média | Agregações para dashboard B2B |
| API B2B: Demand Radar | Alta | `GET /b2b/api/v1/reports/out-of-print-demand` |
| API B2B: Friction Diagnostics | Média | `GET /b2b/api/v1/reports/friction-by-item` |
| Autenticação B2B (API Key) | Alta | Header `X-Api-Key`, tenant isolado por editora |
| Multitenancy (publisher_id) | Alta | Todos os dados B2B particionados por `publisher_id` |

---

## Sprint 10 — CI/CD & Produção
**Status:** 🔮 Futuro

Pipeline de entrega contínua, infraestrutura AWS e observabilidade em produção.

| Feature | Prioridade | Detalhes |
|---------|-----------|----------|
| GitHub Actions: CI | Alta | Build + Lint + Testes unitários em cada PR |
| GitHub Actions: CD | Alta | Deploy no ECS/Lambda ao merge na `main` |
| SonarLint / SAST | Alta | Análise estática de qualidade e segurança |
| Aurora PostgreSQL Serverless v2 | Alta | Substitui PostgreSQL local em produção |
| Amazon Cognito (produção) | Alta | User Pools com Google/Apple/Email |
| AWS Secrets Manager | Alta | Gerenciamento de segredos (`PAAPI_KEY`, etc.) |
| CloudFront CDN | Alta | Cache de capas WebP (S3 origin) |
| Structured Logging (JSON) | Alta | `correlation_id` em todos os logs; sem PII |
| Health endpoint | Alta | `GET /actuator/health` com status de dependências |
| Observabilidade (CloudWatch) | Média | Métricas, dashboards, alarmes críticos |

---

## Resumo Executivo

```
✅ FEITO (Sprint 0–3)       ████████████░░░░░░░░░░░░░░  ~35%
📋 PRÓXIMO (Sprint 4–6)    ░░░░░░░░░░░░████████░░░░░░  ~30%
🔮 FUTURO (Sprint 7–10)    ░░░░░░░░░░░░░░░░░░░░██████  ~35%
```

### O que está funcionando hoje

- Pipeline ISBN: scan → Google Books → S3 (WebP)
- Autenticação JWT (Cognito-ready), com auto-provisionamento de usuários
- Estante virtual: adicionar e listar itens com status e localização física
- 27 testes unitários passando; 2 testes de integração (requerem Docker)
- Ambiente local completo: PostgreSQL + LocalStack (S3, SQS, DynamoDB, EventBridge)

### Próximos marcos críticos

1. **Sprint 4** — Expor o scanner de ISBN como API REST e persistir no banco → completa o fluxo B2C essencial
2. **Sprint 5** — Wishlist → habilita o Price Scanner
3. **Sprint 6** — Price Scanner → core da monetização e diferencial competitivo
4. **Sprint 9** — B2B Analytics → primeira fonte de receita recorrente (SaaS)

---

*Este documento é atualizado a cada sprint concluído.*
