# GreenPulse AI — backend prototype

Working Spring Boot implementation of the system workflow from the project deck:
citizen complaint → Spring Boot API → MySQL → LLM classification → auto-routed
ticket or trend summary, plus a small RAG-style policy Q&A endpoint.

1M1B AI for Sustainability Virtual Internship (IBM SkillsBuild × AICTE) — SDG 11
(Sustainable Cities and Communities), with SDG 12 and SDG 6 as secondary goals.

## Stack

- Java 17, Spring Boot 3.2 (Web, Data JPA, Validation)
- MySQL
- OpenAI or Gemini for classification and summarization — **fully optional**. With
  no API key configured, every AI step falls back to a rule-based equivalent
  (keyword classification, a plain-count trend summary, best-matching policy
  snippet) so the whole pipeline runs end-to-end with zero external calls.

## Setup

1. **Database** — create a MySQL instance (local or hosted). The app will
   create the `greenpulse` schema and tables itself on first run
   (`spring.jpa.hibernate.ddl-auto=update`). Update the username/password in
   `src/main/resources/application.properties` or override them with
   `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` env vars.

2. **(Optional) LLM key** — to use a real model instead of the fallback logic:

   ```bash
   export LLM_API_KEY=sk-...          # your OpenAI or Gemini key
   ```

   and set `llm.provider=openai` or `llm.provider=gemini` in
   `application.properties` (defaults to `openai`, model `gpt-4o-mini`; Gemini
   defaults to `gemini-1.5-flash`).

3. **Run**:

   ```bash
   mvn spring-boot:run
   ```

   The API starts on `http://localhost:8080`.

> This project wasn't compiled inside the sandbox that generated it — Maven
> Central isn't reachable from there. Everything follows standard Spring Boot
> 3 / Jakarta EE conventions, but run `mvn clean verify` locally as a first
> step and fix anything your Maven/JDK version flags.

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

Response includes the AI-assigned `category`, `urgency`, `department`, and a
one-line `aiReasoning` — that reasoning is what gives an officer visibility
into *why* the AI routed it where it did (see Responsible AI → Transparency
in the deck).

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
llm/          LlmClient interface + OpenAI/Gemini implementations + config
service/      ClassificationService, DepartmentRoutingService, ComplaintService,
              TrendSummaryService, PolicyQaService
controller/   REST endpoints
exception/    Centralized error handling
```

## Notes and honest limitations

- **Policy knowledge base is illustrative sample content**, written for this
  demo — not real official municipal policy. Swap
  `src/main/resources/policy-knowledge-base.json` for actual local rules
  before treating any answer as authoritative.
- **Retrieval is keyword-overlap, not embeddings** — fine for ~10 snippets,
  won't scale much past that. A real deployment would swap
  `PolicyQaService.rankByKeywordOverlap` for a vector search.
- **No auth** — every endpoint is open. Add Spring Security before exposing
  this beyond localhost.
- **No pagination** on `GET /api/complaints` — add `Pageable` once complaint
  volume is more than a demo's worth.

## Natural next steps

- Swap keyword retrieval for embeddings (OpenAI/Gemini embeddings + a vector
  store) as the policy set grows
- Add authentication and role-based access (citizen vs officer vs planner)
- Add pagination/filtering on the complaints list
- Add integration tests against a Testcontainers MySQL instance
- Wire a small frontend (or Postman collection) for the demo walkthrough
