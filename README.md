# GreenPulse Frontend

React + Vite frontend for GreenPulse — an AI-powered civic waste-complaint
platform. Citizens describe an issue (optionally with a location hint and a
photo URL) and the Spring Boot + AI backend auto-categorizes it, assigns an
urgency and department, and routes it for resolution.

## Features

- Submit a complaint with a description, optional location hint, and
  optional photo URL
- AI-generated category, urgency, department, and routing reasoning shown
  on each complaint card
- Status tracking through `NEW → ROUTED → IN_PROGRESS → RESOLVED`
- Community complaints grid with live status updates
- Trends / insights view summarizing complaint volume by category
- Policy Q&A assistant for asking questions about waste-management policy

## Setup

```bash
npm install
npm run dev
```

The app runs at `http://localhost:5173` and expects the backend at
`http://localhost:8080` (configured in `src/services/apiConfig.js`).

## Backend requirements

Your Spring Boot API should expose at least:

```
POST   /api/complaints
GET    /api/complaints
GET    /api/complaints/{id}
PATCH  /api/complaints/{id}/status
```

Plus the endpoints backing the trends/insights and policy Q&A views —
confirm these against your controller and update `src/services/` accordingly
if the paths differ.

## Assumed JSON shapes

Request (`POST /api/complaints`):
```json
{
  "description": "The bin near Block A has been overflowing for two days.",
  "locationHint": "Block A",
  "photoUrl": "https://example.com/photo.jpg"
}
```

`description` is required; `locationHint` and `photoUrl` are optional.

Response:
```json
{
  "id": 1,
  "description": "The bin near Block A has been overflowing for two days.",
  "locationHint": "Block A",
  "photoUrl": "https://example.com/photo.jpg",
  "category": "Overflowing Bin",
  "urgency": "HIGH",
  "department": "Sanitation",
  "aiReasoning": "Recurring overflow near a high-traffic area poses a health risk.",
  "status": "NEW",
  "createdAt": "2026-09-18T10:15:00Z"
}
```

Status update (`PATCH /api/complaints/{id}/status`):
```json
{
  "status": "IN_PROGRESS"
}
```

Valid `status` values: `NEW`, `ROUTED`, `IN_PROGRESS`, `RESOLVED`.

**If your DTOs use different field names**, update
`src/services/complaintApi.js` (see `normalizeComplaint`, which already
tolerates a few likely field-name variants), `src/components/ComplaintForm.jsx`,
and `src/components/ComplaintCard.jsx` accordingly.

## CORS

Add this to your Spring Boot project so it accepts requests from the
Vite dev server:

```java
package com.greenpulse.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*");
            }
        };
    }
}
```

Restart Spring Boot after adding this.
