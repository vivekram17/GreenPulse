# GreenPulse Frontend

React + Vite frontend for the GreenPulse waste-complaint Spring Boot API.

## Setup

```bash
npm install
npm run dev
```

The app runs at `http://localhost:5173` and expects the backend at
`http://localhost:8080`.

## Backend requirements

Your Spring Boot controller must expose:

```
POST   /api/complaints
GET    /api/complaints
GET    /api/complaints/{id}
PATCH  /api/complaints/{id}/status
```

## Assumed JSON shapes

Request (`POST /api/complaints`):
```json
{
  "title": "Overflowing waste bin",
  "description": "The bin near Block A has been overflowing for two days.",
  "location": "Block A"
}
```

Response:
```json
{
  "id": 1,
  "title": "Overflowing waste bin",
  "description": "The bin near Block A has been overflowing for two days.",
  "location": "Block A",
  "status": "SUBMITTED"
}
```

Status update (`PATCH /api/complaints/{id}/status`):
```json
{
  "status": "IN_PROGRESS"
}
```

**If your DTOs use different field names** (e.g. `subject` instead of
`title`), update `src/components/ComplaintForm.jsx`,
`src/components/ComplaintCard.jsx`, and `src/services/complaintApi.js`
accordingly.

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
