# 🚀 qm-service

A modern **microservices-based architecture** built with Spring technologies, focusing on scalability, resilience, and security.

---

## 📦 Overview

`qm-service` is a microservices ecosystem designed for high-performance distributed systems.  
It uses **Spring WebFlux** for reactive programming and integrates essential production-grade features like security, rate limiting, and fault tolerance.

---

## 🧩 Architecture

### 🔹 API Gateway (`api-gateway`)

The central entry point for all client requests.

Built with:
- **Spring WebFlux** (Reactive stack)

---

## ✨ Key Features

### ⚡ Reactive API Gateway
- Non-blocking request handling using **Spring WebFlux**
- Optimized for high throughput and low latency

### 🔒 Security
- **OAuth2 authentication** with Keycloak
- **JWT-based authorization**
- Stateless session management

### 🚦 Rate Limiting
- Global rate limiter implemented using **Redis**
- Protects services from abuse and overload

### 🔁 Resilience & Fault Tolerance
- **Circuit Breaker** for handling failed services
- Automatic **retry mechanism** for idempotent requests
- Built-in **fallback strategies**

### 🔍 Observability & Tracing
- Propagation of:
    - **User data**
    - **Correlation ID**
- Enables full request tracking across microservices

### 🧠 Smart Request Handling
- Retry logic applied only to **idempotent requests**
- Prevents unintended side effects

---

## 🏗️ Tech Stack

- **Java 25**
- **Spring Boot 4.0.5**
- **Spring WebFlux**
- **Spring Cloud Gateway 5.0.1**
- **Keycloak**
- **Redis**
- **JWT**
- **Resilience4j** (Circuit Breaker)

---

## 🔐 Authentication Flow
1. Client authenticates via Keycloak
2. Receives JWT token
3. Sends requests with token to API Gateway
4. Gateway validates and forwards request to services

---

## 📄 License

MIT License