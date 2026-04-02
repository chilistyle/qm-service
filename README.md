# 🚀 qm-service

Enterprise-grade **microservices platform** built with Spring ecosystem, focused on scalability, resilience, and observability.

---

## 📦 Overview

`qm-service` is a distributed system designed for high-load environments.  
It combines **reactive and blocking architectures**, integrates centralized security, and provides production-ready infrastructure.

---

## 🧩 Architecture

### 🔹 API Gateway (`api-gateway`)
Central entry point for all client requests.

**Tech:**
- Spring WebFlux
- Spring Cloud Gateway

**Responsibilities:**
- Routing requests
- Authentication & authorization
- Rate limiting
- Correlation ID propagation

---

### 🔹 Book Service (`book-service`)
Domain service responsible for managing books.

**Tech:**
- Spring MVC
- Virtual Threads (Project Loom)
- Flyway

**Capabilities:**
- REST API: `/api/v1/books`
- Role-based access validation (via Gateway headers)
- Correlation ID logging
- Database migrations with Flyway

---

## ✨ Key Features

### ⚡ Performance
- Reactive Gateway (WebFlux)
- Virtual Threads for efficient concurrency

### 🔒 Security
- OAuth2 with Keycloak
- JWT-based authentication
- Stateless services

### 🚦 Rate Limiting
- Redis-backed rate limiter

### 🔁 Resilience
- Circuit Breaker (Resilience4j)
- Retry mechanisms
- Fallback strategies

### 🔍 Observability
- Correlation ID propagation
- Structured logging
- Request tracing across services

---

## 🏗️ Tech Stack

- Java 25
- Spring Boot 4
- Spring WebFlux / Spring MVC
- Spring Cloud Gateway
- Keycloak
- Redis
- PostgreSQL
- Flyway
- Docker

---

## 🐳 Local Development Environment

Fully automated local setup using Docker.

### Services:
- PostgreSQL
- Redis
- Keycloak
- API Gateway
- Book Service

### Run:

```bash
make local
```

### Requirements:
- Docker
- Make
- Maven

---

## 🔐 Authentication Flow

1. User authenticates via Keycloak
2. Receives JWT token
3. Sends request to API Gateway
4. Gateway validates token
5. Request forwarded with headers (including roles & correlationId)

---

## 📄 License

MIT License
