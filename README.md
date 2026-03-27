# CrimsonCompass OAuth Microservice

A production-ready **OAuth 2.0 authentication microservice** built with Spring Boot 3 and Spring Security. It handles Google OAuth login, issues signed JWTs, and synchronizes user data with the [CrimsonCompass TMS](https://github.com/RPreethamR/CrimsonCompass) main backend.


## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2 |
| Security | Spring Security, Spring OAuth2 Client |
| Build | Maven, Java 17 |
| CI/CD | GitHub Actions → GHCR → Azure Web App |
| Container | Docker (multi-stage, JRE-Alpine) |


## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- A **Google OAuth 2.0** client ID/secret ([Google Cloud Console](https://console.cloud.google.com/apis/credentials))

## Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/RPreethamR/CrimsonCompass_OA.git
   cd CrimsonCompass_OA
   ```

2. **Create your `.env` file**
   ```bash
   cp .env.example .env
   ```

3. **Run locally**
   ```bash
   export $(cat .env | xargs)

   ./mvnw spring-boot:run
   ```
   The service starts on **http://localhost:8081**.

4. **Run tests**
   ```bash
   ./mvnw clean test
   ```

## Docker

```bash
# Build
docker build -t crimsoncompass-oauth .

# Run
docker run -p 8081:8081 \
  -e GOOGLE_CLIENT_ID=... \
  -e GOOGLE_CLIENT_SECRET=... \
  -e JWT_SECRET=... \
  crimsoncompass-oauth
```

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/cicd_workflow.yml`) runs on every push to `main`:

1. **Test** — runs `mvn clean test` with dummy credentials
2. **Publish** — builds Docker image and pushes to GitHub Container Registry
3. **Deploy** — deploys to Azure Web App via publish profile

Required GitHub Secrets: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `JWT_SECRET`, `TMS_SERVICE_URL`, `CORS_ALLOWED_ORIGINS`, `OAUTH_REDIRECT_URL`, `AZURE_PUBLISH_PROFILE`.

