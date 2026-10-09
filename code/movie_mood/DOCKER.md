# Docker setup for MovieMood

This project runs as a Spring Boot application and expects external database credentials to be supplied through environment variables.

## Required environment variables

Create a local `.env` file in this directory (or export the variables in your shell) before running Docker Compose.

```env
SERVER_PORT=8080
APP_PORT=8080
DB_URL=jdbc:postgresql://YOUR_SUPABASE_HOST:5432/postgres
DB_USERNAME=YOUR_DATABASE_USERNAME
DB_PASSWORD=YOUR_DATABASE_PASSWORD
TMDB_API_TOKEN=YOUR_TMDB_API_READ_ACCESS_TOKEN
SUPABASE_URL=https://YOUR_PROJECT_REF.supabase.co
SUPABASE_SECRET_KEY=YOUR_SUPABASE_SECRET_KEY
SUPABASE_STORAGE_BUCKET=playlist-covers
JWT_SECRET=YOUR_JWT_SECRET
```

A template is already available in `.env.example`.

## Build the JAR

```bash
./mvnw -DskipTests package
```

## Build the Docker image

```bash
docker build -t movie-mood .
```

## Run with Docker Compose

```bash
docker compose up --build
```

## Run the container directly

```bash
docker run --rm -p 8080:8080 \
  --env-file .env \
  movie-mood
```

## Notes

- No local database container is started for this project.
- The application connects to Supabase using the environment variables above.
- Credentials are never baked into the image; they are passed at runtime via `.env` or shell environment variables.
- The app listens on port 8080 by default and sets Spring Boot to the same port via `SERVER_PORT`.
