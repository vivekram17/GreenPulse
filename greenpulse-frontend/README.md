# GreenPulse Frontend

A React + Vite frontend for submitting and tracking waste complaints.

## Setup

```bash
npm install
npm run dev
```

The app runs at `http://localhost:5173`.

## Project structure

```
src/
├── components/
│   ├── ComplaintForm.jsx   # Form to submit a new complaint
│   └── ComplaintCard.jsx   # Displays a single complaint and its status
└── services/
    └── complaintApi.js     # API calls used by the components
```

## Data the UI works with

Each complaint in the UI uses these fields:

| Field         | Used for                                  |
|---------------|-------------------------------------------|
| `title`       | Short heading of the complaint            |
| `description` | Details of the issue                      |
| `location`    | Where the issue is (e.g. "Block A")       |
| `status`      | Current state (e.g. `SUBMITTED`, `IN_PROGRESS`) |

## Customizing field names

If your data uses different field names (e.g. `subject` instead of `title`), update these files:

- `src/components/ComplaintForm.jsx`
- `src/components/ComplaintCard.jsx`
- `src/services/complaintApi.js`
