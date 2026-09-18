# GreenPulse AI — frontend

A React + Vite frontend for the GreenPulse AI Spring Boot backend
(`greenpulse-ai`), matching the endpoints documented in that project's
README:

| Method | Path | Used by |
|---|---|---|
| POST | `/api/complaints` | Report form |
| GET | `/api/complaints` | Community issues list |
| GET | `/api/complaints/{id}` | (available in `complaintApi.js`, not wired to a route yet) |
| PATCH | `/api/complaints/{id}/status` | Status dropdown on each card |
| GET | `/api/insights/trends` | Insights panel |
| POST | `/api/policy/ask` | Policy Q&A panel |

## Run it

```bash
npm install
npm run dev
```

Opens on `http://localhost:5173`. Point it at a different backend host by
editing `API_ROOT` in `src/services/apiConfig.js`.

## Backend CORS

The backend needs to allow the Vite dev origin. Add this to the Spring Boot
project if it isn't there already:

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*");
            }
        };
    }
}
```

## A note on field names

No `ComplaintResponse` / `ComplaintSubmitRequest` / `StatusUpdateRequest` /
`TrendSummary` / `PolicyAnswer` Java source was available when this was
built — only the backend README and its curl examples. The frontend was
built against what that README documents directly:

- **Submit** (`POST /api/complaints`): `{ description, locationHint }` —
  taken verbatim from the README's example.
- **Status** (`PATCH /api/complaints/{id}/status`): `{ status }`, one of
  `NEW`, `ROUTED`, `IN_PROGRESS`, `RESOLVED` — the enum values the README
  lists.
- **Complaint fields shown in the UI** (`category`, `urgency`, `department`,
  `aiReasoning`): the README names these explicitly as what the AI
  classification step assigns.
- **Trends** (`GET /api/insights/trends`) and **policy answers**
  (`POST /api/policy/ask`): the README only describes these in prose
  ("counts by category/location + a narrative summary"), so the exact
  response key names are a guess. `src/services/insightsApi.js` and
  `src/services/policyApi.js` each check a few likely key names
  (`byCategory`/`countsByCategory`/`categoryCounts`, etc.) and render
  gracefully if none match.

If any of these don't line up with the real DTOs, the two normalize
functions in `complaintApi.js`, `insightsApi.js`, and `policyApi.js` are the
only places that need editing — the components consume the normalized
shape, not the raw response.
