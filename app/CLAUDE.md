# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.2 + JDK 21 REST API shopping mall backend example with SQLite database.

## Common Commands

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run tests with verbose output
mvn test -Dsurefire.useFile=false

# Run performance tests with Gatling
mvn gatling:test

# View Gatling HTML report
start target/gatling/shoppingmallsimulation-*/index.html
```

## Architecture

### Layer Structure
- **Controller** - REST endpoints, request/response handling
- **Service** - Business logic, transaction management
- **Repository** - Data access via JPA
- **Entity** - JPA entities mapped to database tables
- **DTO** - Data transfer objects for API

### Key Configuration
- Database: SQLite (`shopping.db`), managed by HikariCP (max 10 connections)
- Cache: Caffeine (max 1000 entries, 5min TTL)
- API Docs: SpringDoc OpenAPI at `/swagger-ui.html`

### SQL Scripts Execution
`schema.sql` and `data.sql` execute on every startup (`mode: always`), which overwrites any manual changes. This is intentional for development/testing.

### Core Modules
- **User**: Registration and query
- **Product**: CRUD, inventory management, caching
- **Order**: Create order (auto deducts inventory), status management
- **Mail**: Async simulated email sending

## Important Notes

- JPA `ddl-auto: update` is used - Hibernate will auto-create/update tables based on entity annotations
- SQLite dialect: `org.hibernate.community.dialect.SQLiteDialect`
- Email is simulated (no real SMTP), check logs for "sending" confirmation

## Code Standards

### Dependency Injection
- Use **constructor injection** instead of field injection
- Required: `private final SomeService someService;` and constructor
- Optional: Use `@Nullable` + constructor for optional dependencies