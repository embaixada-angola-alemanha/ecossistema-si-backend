# ecossistema-si-backend

**SI -- Sitio Institucional (Backend)**

API backend do sitio institucional da Embaixada da Republica de Angola na Alemanha. Sistema CMS multilingue (PT/EN/DE/CS) para gestao de paginas institucionais, eventos, menus de navegacao e informacoes de contacto, com fluxo editorial completo.

Parte do [Ecossistema Digital -- Embaixada de Angola na Alemanha](https://github.com/embaixada-angola-alemanha/ecossistema-project).

---

## Stack Tecnologica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.3 |
| Base de Dados | PostgreSQL |
| Migracoes | Flyway |
| Autenticacao | Keycloak (OAuth2 Resource Server / JWT) |
| Armazenamento | MinIO (via commons-storage) |
| Mensageria | RabbitMQ (AMQP) |
| Mapeamento | MapStruct 1.5.5 |
| Documentacao API | SpringDoc OpenAPI 2.8.6 (Swagger UI) |
| Rate Limiting | Bucket4j |
| Testes | JUnit 5, Testcontainers, H2 |
| Cobertura | JaCoCo |
| CI/CD | GitHub Actions |
| Containerizacao | Docker (Eclipse Temurin 21 JRE Alpine) |

---

## Estrutura do Projecto

```
src/main/java/ao/gov/embaixada/si/
  config/
    OpenApiConfig.java            # Configuracao Swagger/OpenAPI
  controller/
    PageController.java           # CRUD de paginas (admin, autenticado)
    PublicPageController.java     # Paginas publicas (sem autenticacao)
    InstitutionalController.java  # Conteudo institucional publico
    EventController.java          # CRUD de eventos
    MenuController.java           # Gestao de menus de navegacao
    ContactController.java        # Gestao de contactos
    EditorialController.java      # Workflow editorial, versionamento, templates
    MediaController.java          # Upload/download de ficheiros media (MinIO)
  dto/
    PageCreateRequest.java, PageResponse.java, PageUpdateRequest.java
    EventCreateRequest.java, EventResponse.java
    MenuCreateRequest.java, MenuResponse.java, MenuItemCreateRequest.java, MenuItemResponse.java
    ContactInfoCreateRequest.java, ContactInfoResponse.java
    TranslationRequest.java, TranslationResponse.java
    MediaFileResponse.java
    ContentTemplateCreateRequest.java, ContentTemplateResponse.java
    PageVersionResponse.java, SchedulePublicationRequest.java
  entity/
    Page.java, PageTranslation.java, PageVersion.java
    Event.java, Menu.java, MenuItem.java
    ContactInfo.java, MediaFile.java
    ContentTemplate.java, ScheduledPublication.java
  enums/
    EstadoConteudo.java           # DRAFT, REVIEW, PUBLISHED, ARCHIVED
    Idioma.java                   # PT, EN, DE, CS
    LocalizacaoMenu.java          # HEADER, FOOTER
    TipoPagina.java               # Tipos de pagina institucional
    TipoMedia.java                # Tipos de media
  exception/
    GlobalExceptionHandler.java
    ResourceNotFoundException.java
    DuplicateResourceException.java
    InvalidStateTransitionException.java
  integration/
    SiEventPublisher.java         # Publicacao de eventos via RabbitMQ
    SiSgcConsumer.java            # Consumo de mensagens do SGC
  repository/
    PageRepository.java, PageTranslationRepository.java, PageVersionRepository.java
    EventRepository.java, MenuRepository.java, MenuItemRepository.java
    ContactInfoRepository.java, MediaFileRepository.java
    ContentTemplateRepository.java, ScheduledPublicationRepository.java
  service/
    PageService.java, PageVersionService.java
    EventService.java, MenuService.java
    ContactInfoService.java, MediaService.java
    EditorialWorkflowService.java, ContentTemplateService.java
    ScheduledPublicationService.java

src/main/resources/
  application.yml                 # Configuracao principal (porta 8082)
  application-staging.yml         # Configuracao staging
  application-production.yml      # Configuracao producao
  db/migration/
    V1__create_si_tables.sql
    V2__create_content_management_tables.sql
    V3__create_institutional_tables.sql
```

---

## Endpoints da API

Base URL: `http://localhost:8082`

### Paginas (Admin) -- `/api/v1/pages`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/pages` | Criar pagina com traducoes multilingues | ADMIN, EDITOR |
| GET | `/api/v1/pages` | Listar paginas (filtro por tipo/estado) | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/pages/{id}` | Obter pagina por ID | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/pages/slug/{slug}` | Obter pagina por slug | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/pages/search?q=&idioma=` | Pesquisa full-text | ADMIN, EDITOR, VIEWER |
| PUT | `/api/v1/pages/{id}` | Actualizar pagina | ADMIN, EDITOR |
| PATCH | `/api/v1/pages/{id}/estado` | Alterar estado da pagina | ADMIN, EDITOR |
| DELETE | `/api/v1/pages/{id}` | Eliminar pagina | ADMIN |

### Eventos (Admin) -- `/api/v1/events`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/events` | Criar evento | ADMIN, EDITOR |
| GET | `/api/v1/events` | Listar eventos (filtro por estado) | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/events/{id}` | Obter evento por ID | ADMIN, EDITOR, VIEWER |
| PUT | `/api/v1/events/{id}` | Actualizar evento | ADMIN, EDITOR |
| PATCH | `/api/v1/events/{id}/estado` | Alterar estado do evento | ADMIN, EDITOR |
| DELETE | `/api/v1/events/{id}` | Eliminar evento | ADMIN |

### Menus -- `/api/v1/menus`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/menus` | Criar menu | ADMIN |
| GET | `/api/v1/menus` | Listar todos os menus | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/menus/{id}` | Obter menu por ID | ADMIN, EDITOR, VIEWER |
| POST | `/api/v1/menus/{id}/items` | Adicionar item ao menu | ADMIN, EDITOR |
| DELETE | `/api/v1/menus/{menuId}/items/{itemId}` | Remover item do menu | ADMIN, EDITOR |
| PATCH | `/api/v1/menus/{id}/toggle` | Activar/desactivar menu | ADMIN |
| DELETE | `/api/v1/menus/{id}` | Eliminar menu | ADMIN |

### Contactos -- `/api/v1/contacts`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/contacts` | Criar contacto | ADMIN, EDITOR |
| GET | `/api/v1/contacts` | Listar contactos | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/contacts/{id}` | Obter contacto por ID | ADMIN, EDITOR, VIEWER |
| PUT | `/api/v1/contacts/{id}` | Actualizar contacto | ADMIN, EDITOR |
| PATCH | `/api/v1/contacts/{id}/toggle-active` | Activar/desactivar contacto | ADMIN, EDITOR |
| DELETE | `/api/v1/contacts/{id}` | Eliminar contacto | ADMIN |

### Editorial -- `/api/v1/editorial`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/editorial/pages/{id}/submit-review` | Submeter para revisao | ADMIN, EDITOR |
| POST | `/api/v1/editorial/pages/{id}/publish` | Publicar pagina | ADMIN, EDITOR |
| POST | `/api/v1/editorial/pages/{id}/unpublish` | Despublicar pagina | ADMIN, EDITOR |
| POST | `/api/v1/editorial/pages/{id}/archive` | Arquivar pagina | ADMIN |
| POST | `/api/v1/editorial/pages/{id}/versions` | Criar snapshot de versao | ADMIN, EDITOR |
| GET | `/api/v1/editorial/pages/{id}/versions` | Listar versoes | ADMIN, EDITOR, VIEWER |
| POST | `/api/v1/editorial/pages/{id}/versions/{n}/restore` | Restaurar versao | ADMIN, EDITOR |
| POST | `/api/v1/editorial/pages/{id}/schedule` | Agendar publicacao | ADMIN, EDITOR |
| POST | `/api/v1/editorial/templates` | Criar template | ADMIN |
| GET | `/api/v1/editorial/templates` | Listar templates | ADMIN, EDITOR |
| GET | `/api/v1/editorial/templates/{id}` | Obter template | ADMIN, EDITOR |
| PUT | `/api/v1/editorial/templates/{id}` | Actualizar template | ADMIN |
| DELETE | `/api/v1/editorial/templates/{id}` | Eliminar template | ADMIN |

### Media -- `/api/v1/media`

| Metodo | Endpoint | Descricao | Roles |
|---|---|---|---|
| POST | `/api/v1/media` | Upload de ficheiro (multipart) | ADMIN, EDITOR |
| GET | `/api/v1/media` | Listar ficheiros media | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/media/{id}` | Metadados do ficheiro | ADMIN, EDITOR, VIEWER |
| GET | `/api/v1/media/{id}/download` | Download do ficheiro | ADMIN, EDITOR, VIEWER |
| DELETE | `/api/v1/media/{id}` | Eliminar ficheiro | ADMIN |

### API Publica (sem autenticacao) -- `/api/v1/public`

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | `/api/v1/public/pages` | Listar paginas publicadas |
| GET | `/api/v1/public/pages/{slug}` | Obter pagina publicada por slug |
| GET | `/api/v1/public/menus/{localizacao}` | Obter menu activo (HEADER/FOOTER) |
| GET | `/api/v1/public/institutional/embaixador` | Pagina do Embaixador |
| GET | `/api/v1/public/institutional/sobre-angola` | Pagina Sobre Angola |
| GET | `/api/v1/public/institutional/sobre-angola/{subsection}` | Subseccoes: presidente, poderes, geografia, historia, demografia, economia, cultura, simbolos |
| GET | `/api/v1/public/institutional/relacoes-bilaterais` | Relacoes bilaterais |
| GET | `/api/v1/public/institutional/events` | Eventos publicados (paginado) |
| GET | `/api/v1/public/institutional/events/upcoming` | Proximos eventos |
| GET | `/api/v1/public/institutional/events/calendar?start=&end=` | Eventos por intervalo de datas |
| GET | `/api/v1/public/institutional/events/{id}` | Detalhe do evento |
| GET | `/api/v1/public/institutional/contacts` | Contactos activos |

### Outros

| Endpoint | Descricao |
|---|---|
| GET `/actuator/health` | Health check |
| GET `/v3/api-docs` | Especificacao OpenAPI (JSON) |
| GET `/swagger-ui.html` | Swagger UI interactivo |

---

## Fluxo Editorial

```
DRAFT --> REVIEW --> PUBLISHED --> ARCHIVED
                        |
                        v
                      DRAFT (unpublish)
```

---

## Roles Keycloak

| Role | Permissoes |
|---|---|
| `si-admin` (ADMIN) | Acesso total: CRUD, workflow, templates, eliminacao |
| `si-editor` (EDITOR) | Criar/editar conteudos, workflow, upload media |

---

## Pre-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+
- Keycloak (realm `ecossistema`)
- MinIO (armazenamento de media)
- RabbitMQ (mensageria)

---

## Como Executar

### Desenvolvimento Local

```bash
# Criar base de dados
createdb si_db

# Executar aplicacao (porta 8082)
mvn spring-boot:run

# Executar testes
mvn clean verify
```

### Build

```bash
# Compilar e gerar JAR
mvn clean package -DskipTests

# Build Docker
docker build -t ecossistema-si-backend .

# Executar container
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/si_db \
  -e SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://keycloak:8080/realms/ecossistema \
  ecossistema-si-backend
```

---

## Configuracao de Ambiente

| Variavel | Descricao | Valor Default |
|---|---|---|
| `SERVER_PORT` | Porta do servidor | `8082` |
| `SPRING_DATASOURCE_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5432/si_db` |
| `SPRING_DATASOURCE_USERNAME` | Utilizador BD | `ecossistema` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | (dev only) |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` | Keycloak issuer | `http://localhost:8080/realms/ecossistema` |
| `SPRING_RABBITMQ_HOST` | Host RabbitMQ | `localhost` |
| `ECOSSISTEMA_STORAGE_ENDPOINT` | Endpoint MinIO | `http://localhost:9000` |
| `ECOSSISTEMA_STORAGE_DEFAULT_BUCKET` | Bucket MinIO | `si-media` |

Perfis disponiveis: `default`, `staging`, `production`

---

## CI/CD

Pipeline GitHub Actions (`.github/workflows/ci.yml`):
1. **Build & Test** -- `mvn clean verify` com JDK 21
2. **Build Docker Image** -- Build da imagem Docker nos branches `main`/`develop`
3. **Conventional Commits** -- Validacao de mensagens de commit em PRs

---

## Projecto Principal

Este repositorio faz parte do **Ecossistema Digital -- Embaixada de Angola na Alemanha**.

Repositorio principal: [ecossistema-project](https://github.com/embaixada-angola-alemanha/ecossistema-project)

Dominio: `embaixada-angola.site`
