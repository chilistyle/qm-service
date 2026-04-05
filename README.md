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

### 🔹 Eureka Server (`eureka-server`)
Service discovery server for dynamic microservice registration and lookup.

**Tech:**

- Spring Cloud Netflix Eureka

**Responsibilities:**

- Service registration & discovery
- Health monitoring of services
- Dynamic routing support for Gateway

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

### 🔹 Library Service (`library-service`)
High-performance reactive service for library management operations.
**Tech:**

- Quarkus 3.33 (Reactive Stack)
- Kotlin & Coroutines support
- Hibernate Reactive with Mutiny
- Panache Entity/Repository pattern
- PostgreSQL Reactive Driver
- SmallRye Stork (Service Discovery & Registration)

**Capabilities:**

- Non-blocking I/O: Built on top of Vert.x for maximum throughput, optimized for limited resources (1 CPU / 1 GB RAM).
- Reactive Persistence: Fully asynchronous database communication using PanacheRepository for clean and expressive data access.
- Self-Healing Registration: Custom Eureka Watchdog (Kotlin) to ensure instant service re-registration after Eureka Server restarts.
- Stork Integration: Client-side load balancing and robust service discovery.
- REST API: `/api/v1/books` optimized for high-concurrency read/write operations without thread blocking.

---

## ✨ Key Features

### ⚡ Performance
- Reactive Gateway (WebFlux)
- Virtual Threads for efficient concurrency
- Fully Reactive Stack (Vert.x Core + Hibernate Reactive)
- Resource Efficiency: Optimized for low-resource environments (1 CPU / 1 GB RAM per node).
- Non-blocking I/O: Fully asynchronous database communication in library-service. 

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
- Self-Healing Registry: Custom Watchdog logic for instant Eureka recovery.
- Fail-safe Redis Integration: Non-blocking rate limiting with fallback strategies.

### 🔍 Observability
- Correlation ID propagation
- Structured logging
- Request tracing across services

### 🧭 Service Discovery
- Centralized registry via Eureka Server
- Dynamic routing in API Gateway
- Supports horizontal scaling of services

---

## 🏗️ Tech Stack

- Java 25 & Kotlin 2.3.10
- Quarkus 3.33.1 (Reactive Stack)
- Spring Boot 4 (Spring MVC & WebFlux)
- Spring Cloud Gateway
- Keycloak
- Redis
- PostgreSQL
- Flyway
- Docker
- Hibernate Reactive / Mutiny
- SmallRye Stork (Client-side Load Balancing)

---

## 🐳 Local Development Environment

Fully automated local setup using Docker.

### Services:
- PostgreSQL
- Redis
- Keycloak
- Eureka Server
- API Gateway
- Book Service
- Library Service

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
