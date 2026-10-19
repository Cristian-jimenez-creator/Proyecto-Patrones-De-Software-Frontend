# Smart Pool Security

The application is organized as two separately deployable services:

- `backend/`: Java 17, Spring Boot REST API, PostgreSQL in production, and local H2 for development.
- `frontend/`: standalone Spanish HTML, CSS, and JavaScript site.

The previous folders `smart-pool-backend/` and `Smart-pool-Frontend/` are preserved as source backups and excluded from Git. Use only the new `backend/` and `frontend/` folders going forward.

## Local setup

1. Start the API from `backend/` with JDK 17 and Maven: `mvn spring-boot:run`.
2. Serve `frontend/` with a local static server such as VS Code Live Server at `http://localhost:5500`.
3. The local API URL is set in `frontend/assets/config.js` to `http://localhost:8080`.
4. To enable login and registration locally, set `SMARTPOOL_AUTH_ENABLED=true`, `SMARTPOOL_AUTH_USERNAME`, and `SMARTPOOL_AUTH_PASSWORD` before starting the API. The configured admin password must have at least 16 characters.

## Separate Render deployment

Use this repository for both services, with the service-specific root directories:

### Backend Web Service

- Root Directory: `backend`
- Runtime: Docker
- Dockerfile Path: `./Dockerfile`
- Docker Build Context: `.`
- Health Check Path: `/health`
- Add a PostgreSQL database and set `SPRING_PROFILES_ACTIVE=prod`, `SMARTPOOL_AUTH_ENABLED=true`, `SMARTPOOL_AUTH_USERNAME`, `SMARTPOOL_AUTH_PASSWORD`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, and `SMARTPOOL_FRONTEND_ORIGIN` in Render.

### Frontend Static Site

- Root Directory: `frontend`
- Build Command: leave empty
- Publish Directory: `.`
- After Render gives the backend URL, set that URL in `frontend/assets/config.js` as `SMARTPOOL_API_URL` and push the change.
- Set `SMARTPOOL_FRONTEND_ORIGIN` on the backend to the exact public URL of the Static Site, then redeploy the backend.

Do not commit database credentials or admin passwords. The signup form creates user accounts in PostgreSQL with BCrypt-hashed passwords. The admin login remains configured through Render secrets.

## Demo limitations

Pool, camera, and alert records are demonstration data. Camera feeds and AI analysis are simulated; this project does not connect to physical cameras or run a real AI model.
