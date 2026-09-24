# GreenPulse AI — Backend

Spring Boot backend for a civic complaint workflow:
citizen complaint → REST API → MySQL → LLM classification → auto-routed
ticket or trend summary, plus a small RAG-style policy Q&A endpoint.

## Stack

- Java 17, Spring Boot 3.2 (Web, Data JPA, Validation)
- MySQL
- LLM for classification and summarization — **fully optional**. Supported
  providers: **Ollama** (local, default; IBM Granite) or OpenAI. If no LLM is
  reachable, every AI step falls back to a rule-based equivalent (keyword
  classification, a plain-count trend summary, best-matching policy snippet),
  so the whole pipeline runs end-to-end with zero external calls.

## Setup

1. **Database** — create a MySQL instance (local or hosted). The app creates
   the `greenpulse` schema and tables on first run
   (`spring.jpa.hibernate.ddl-auto=update`). Update the username/password in
   `src/main/resources/application.properties` or override them with the
   `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` env vars.

2. **(Optional) Local LLM with Ollama + IBM Granite** — no API key and no
   data leaves your machine:

   ```bash
   # install Ollama from https://ollama.com, then pull the Granite model
   ollama pull granite4:3b        # use the exact tag shown in `ollama list`
   ollama serve                   # skip if Ollama is already running
   ```

   Then in `application.properties`:

   ```properties
   llm.provider=ollama
   llm.ollama.base-url=http://localhost:11434
   llm.ollama.model=granite4:3b
   ```

   Or override with environment variables:

   ```bash
   export LLM_PROVIDER=ollama
   export LLM_OLLAMA_MODEL=granite4:3b
   ```

   The Ollama client calls `POST /api/chat` with `stream=false` and
   `format=json` so classification responses come back as parseable JSON.

   **Using OpenAI instead:**

   ```bash
   export LLM_API_KEY=sk-...
   ```

   and set `llm.provider=openai` (default model `gpt-4o-mini`).

3. **Run**:

   ```bash
   mvn spring-boot:run
   ```

   The API starts on `http://localhost:8080`.

> Run `mvn clean verify` locally as a first step and fix anything your
> Maven/JDK version flags.

## API reference

| Method | Path | Purpose |
|---|---|---|
| POST | `/api/complaints` | Submit a complaint; runs classify → route automatically |
| GET | `/api/complaints` | List all complaints |
| GET | `/api/complaints/{id}` | Get one complaint |
| PATCH | `/api/complaints/{id}/status` | Officer updates status (`NEW`, `ROUTED`, `IN_PROGRESS`, `RESOLVED`) |
| GET | `/api/insights/trends` | Counts by category/location + a narrative summary |
| POST | `/api/policy/ask` | Ask a question against the local policy knowledge base |

### Example: submit a complaint

```bash
curl -X POST http://localhost:8080/api/complaints \
  -H "Content-Type: application/json" \
  -d '{"description": "Water has been leaking from a broken pipe near the bus stop on Lake Road for two days.", "locationHint": "Lake Road"}'
```

The response includes the AI-assigned `category`, `urgency`, `department`, and
a one-line `aiReasoning`, which gives an officer visibility into *why* the
complaint was routed where it was.

### Example: ask a policy question

```bash
curl -X POST http://localhost:8080/api/policy/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "How long until someone responds to a water leak?"}'
```

### Example: trend summary

```bash
curl http://localhost:8080/api/insights/trends
```

## Project layout

```
model/        JPA entity + enums (Complaint, ComplaintCategory, Urgency, ComplaintStatus)
repository/   Spring Data JPA repository
dto/          Request/response payloads
llm/          LlmClient interface + Ollama/OpenAI implementations + config
service/      ClassificationService, DepartmentRoutingService, ComplaintService,
              TrendSummaryService, PolicyQaService
controller/   REST endpoints
exception/    Centralized error handling
```

## Notes and limitations

- **Policy knowledge base is illustrative sample content**, not real official
  policy. Swap `src/main/resources/policy-knowledge-base.json` for actual
  local rules before treating any answer as authoritative.
- **Retrieval is keyword-overlap, not embeddings** — fine for ~10 snippets,
  won't scale much past that. Swap `PolicyQaService.rankByKeywordOverlap` for
  vector search as the policy set grows.
- **Small local models can return malformed JSON.** If parsing fails, the
  service falls back to rule-based classification for that request.
- **No auth** — every endpoint is open. Add Spring Security before exposing
  this beyond localhost.
- **No pagination** on `GET /api/complaints` — add `Pageable` once volume
  grows.

## Next steps

- Embeddings via Ollama (`/api/embed`) plus a vector store for policy retrieval
- Authentication and role-based access (citizen vs officer vs planner)
- Pagination and filtering on the complaints list
- Integration tests against a Testcontainers MySQL instance
