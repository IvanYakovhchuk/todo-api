# Backend (Java)

Spring Boot 3 REST API for the todo app. In-memory storage (resets on restart).

## Run locally

```bash
cd backend
mvn spring-boot:run
```

API listens on `http://localhost:8080`.

## Build jar

```bash
mvn -q -DskipTests package
java -jar target/todo-api-1.0.0.jar
```

Cloud hosts that set `PORT` are supported via `server.port=${PORT:8080}`.
