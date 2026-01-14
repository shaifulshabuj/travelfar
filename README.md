# Travel Reservation API

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![CI/CD Pipeline](https://github.com/shaifulshabuj/travelfar/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/shaifulshabuj/travelfar/actions)
[![Docker Build](https://github.com/shaifulshabuj/travelfar/workflows/Docker%20Build%20and%20Publish/badge.svg)](https://github.com/shaifulshabuj/travelfar/actions)

A production-quality Spring Boot REST API for hotel search and reservations, built following **Travel engineering standards**. This system is designed to handle high-traffic scenarios with Redis caching, optimistic locking, and comprehensive validation.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Cache Strategy](#cache-strategy)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [CI/CD Pipeline](#cicd-pipeline)
- [Testing](#testing)
- [Project Structure](#project-structure)

## 🎯 Overview

This API simulates a Travel-like hotel search and reservation backend. It's not a toy project—it's designed as if it will serve **hundreds of requests per second** in production.

### Key Design Principles

- **Scalability**: Optimized for high-traffic read operations
- **Clean Architecture**: Strict layered architecture (Controller → Service → Repository)
- **Reliability**: Optimistic locking prevents double-booking
- **Performance**: Redis caching for frequently accessed data
- **Code Quality**: Production-ready code with comprehensive testing

## 🏗 Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│          Controller Layer               │
│  (REST API, Request Validation)         │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│          Service Layer                  │
│  (Business Logic, Transactions)         │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│        Repository Layer                 │
│  (Data Access, JPA)                     │
└─────────────────┬───────────────────────┘
                  │
                  ▼
          ┌───────────────┐
          │    MySQL DB    │
          └───────────────┘

            ┌──────────────┐
            │ Redis Cache  │  (for search results)
            └──────────────┘
```

### Design Rules

- ❌ No business logic in controllers
- ❌ No direct DB access from controllers
- ❌ No static utility logic
- ✅ Transaction boundaries in service layer
- ✅ DTOs for API I/O (entities never exposed)
- ✅ Centralized exception handling

## 🛠 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 3.2.1 | Application framework |
| Spring Data JPA | 3.2.1 | Database access |
| MySQL | 8.0 | Primary database |
| Redis | 7.x | Caching layer |
| Hibernate Validator | - | Request validation |
| Lombok | - | Reduce boilerplate |
| JUnit 5 | - | Unit testing |
| Swagger/OpenAPI | 3.x | API documentation |
| Docker | - | Containerization |
| Maven | - | Build tool |

## ✨ Features

### 1. Hotel Search API (READ-HEAVY)

- ✅ Paginated search results
- ✅ Sorted by price (ascending)
- ✅ Redis caching (5-minute TTL)
- ✅ City-based search
- ✅ Date range filtering
- ✅ Guest count validation

### 2. Reservation API (WRITE)

- ✅ Create reservations with validation
- ✅ Hotel availability checking
- ✅ Optimistic locking (prevents double-booking)
- ✅ Transactional integrity
- ✅ Room inventory management

### 3. Cross-Cutting Concerns

- ✅ Comprehensive error handling
- ✅ Structured logging (SLF4J + Logback)
- ✅ Request/response validation
- ✅ API documentation (Swagger)

## 🔌 API Endpoints

### Search Hotels

```http
GET /api/v1/hotels/search
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| city | String | Yes | City name |
| checkIn | LocalDate | Yes | Check-in date (yyyy-MM-dd) |
| checkOut | LocalDate | Yes | Check-out date (yyyy-MM-dd) |
| guests | Integer | Yes | Number of guests (min: 1) |
| page | Integer | No | Page number (default: 0) |
| size | Integer | No | Page size (default: 20) |

**Example Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/hotels/search?city=Tokyo&checkIn=2026-01-20&checkOut=2026-01-23&guests=2&page=0&size=20"
```

**Example Response:**

```json
{
  "content": [
    {
      "id": 4,
      "name": "Budget Stay Tokyo",
      "city": "Tokyo",
      "pricePerNight": 5000.00,
      "rating": 3.5,
      "description": "Clean and comfortable budget accommodation",
      "availableRooms": 80
    },
    {
      "id": 2,
      "name": "Business Inn Tokyo",
      "city": "Tokyo",
      "pricePerNight": 8000.00,
      "rating": 4.0,
      "description": "Affordable business hotel near Tokyo Station",
      "availableRooms": 40
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 5,
  "totalPages": 1
}
```

### Create Reservation

```http
POST /api/v1/reservations
```

**Request Body:**

```json
{
  "hotelId": 1,
  "guestName": "John Doe",
  "guestEmail": "john.doe@example.com",
  "checkIn": "2026-01-20",
  "checkOut": "2026-01-23",
  "guests": 2
}
```

**Example Request:**

```bash
curl -X POST "http://localhost:8080/api/v1/reservations" \
  -H "Content-Type: application/json" \
  -d '{
    "hotelId": 1,
    "guestName": "John Doe",
    "guestEmail": "john.doe@example.com",
    "checkIn": "2026-01-20",
    "checkOut": "2026-01-23",
    "guests": 2
  }'
```

**Example Response:**

```json
{
  "id": 1,
  "hotelId": 1,
  "guestName": "John Doe",
  "guestEmail": "john.doe@example.com",
  "checkIn": "2026-01-20",
  "checkOut": "2026-01-23",
  "guests": 2,
  "createdAt": "2026-01-14T10:30:00"
}
```

## 🚀 Cache Strategy

### Why Redis?

In a high-traffic travel platform like Travel:

1. **Read-Heavy Traffic**: Hotel searches are 10-100x more frequent than reservations
2. **Repeated Queries**: Same city + date combinations searched repeatedly
3. **Database Load**: Without caching, every search hits MySQL
4. **Response Time**: Redis reduces latency from ~100ms to ~5ms

### Cache Implementation

- **Cache Key**: `city_checkIn_checkOut_guests_page_size`
- **TTL**: 5 minutes (configurable in `application.yml`)
- **Eviction**: Automatic based on TTL
- **Serialization**: JSON for debugging ease

### Cache Behavior

```
Search Request → Check Redis → Cache Hit? → Return from Redis
                                    ↓ (if miss)
                             Query MySQL → Store in Redis → Return
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0
- Redis 7.x

### Local Development Setup

1. **Clone the repository**

```bash
git clone https://github.com/shaifulshabuj/travelfar.git
cd travelfar
```

2. **Start MySQL and Redis**

```bash
# Start MySQL
mysql -u root -p
CREATE DATABASE travel_db;

# Start Redis
redis-server
```

3. **Update configuration** (if needed)

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_db
    username: root
    password: your_password
```

4. **Build the project**

```bash
mvn clean install
```

5. **Run the application**

```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

6. **Access Swagger Documentation**

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

## 🐳 Running with Docker

The easiest way to run the entire stack:

```bash
# Start all services (MySQL, Redis, App)
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

This will:
- Start MySQL on port 3306
- Start Redis on port 6379
- Build and run the Spring Boot app on port 8080
- Initialize database with sample hotels

## 🚀 CI/CD Pipeline

This project uses **GitHub Actions** for continuous integration and deployment.

### Automated Workflows

1. **CI/CD Pipeline** - Runs on every push and PR
   - Builds and tests the application
   - Generates code coverage reports
   - Builds Docker images
   - Deploys to staging (on main branch)
   - Deploys to production (manual approval required)

2. **Pull Request Checks** - Validates PRs
   - Runs quick tests
   - Validates code formatting
   - Checks PR title format

3. **Docker Publish** - Multi-platform builds
   - Builds for linux/amd64 and linux/arm64
   - Publishes to GitHub Container Registry
   - Scans for security vulnerabilities

4. **Dependency Updates** - Weekly automated updates
   - Checks for Maven dependency updates
   - Creates PRs with updates

### Pipeline Status

View the current pipeline status in the [Actions tab](https://github.com/shaifulshabuj/travelfar/actions).

### Code Coverage

Coverage reports are generated on every build:
- Minimum requirement: 60%
- Target: 80%

View coverage locally:
```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

**For detailed CI/CD documentation, see [docs/CI-CD.md](docs/CI-CD.md)**

## 🧪 Testing

### Run all tests

```bash
mvn test
```

### Run specific test class

```bash
mvn test -Dtest=HotelSearchServiceTest
```

### Test Coverage

- Service layer unit tests
- Validation logic tests
- Business rule verification
- Mock repository usage

## 📁 Project Structure

```
src/main/java/com/example/travel/
├── TravelApplication.java          # Main application entry point
├── controller/                     # REST Controllers (no business logic)
│   ├── HotelSearchController.java
│   └── ReservationController.java
├── service/                        # Business logic & transactions
│   ├── HotelSearchService.java
│   └── ReservationService.java
├── repository/                     # Data access layer
│   ├── HotelRepository.java
│   └── ReservationRepository.java
├── entity/                         # JPA entities
│   ├── Hotel.java
│   └── Reservation.java
├── dto/                           # Data Transfer Objects
│   ├── HotelSearchRequest.java
│   ├── HotelResponse.java
│   ├── ReservationRequest.java
│   └── ReservationResponse.java
├── exception/                     # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── ErrorResponse.java
└── config/                        # Configuration classes
    ├── RedisConfig.java
    └── SwaggerConfig.java
```

## 📊 Database Schema

### Hotels Table

```sql
CREATE TABLE hotels (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    rating DOUBLE NOT NULL,
    description VARCHAR(1000),
    total_rooms INT,
    available_rooms INT,
    INDEX idx_city (city),
    INDEX idx_price (price_per_night)
);
```

### Reservations Table

```sql
CREATE TABLE reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hotel_id BIGINT NOT NULL,
    guest_name VARCHAR(200) NOT NULL,
    guest_email VARCHAR(200) NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guests INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    version BIGINT,  -- for optimistic locking
    INDEX idx_hotel_id (hotel_id),
    INDEX idx_check_in_out (check_in, check_out)
);
```

## 🔒 Error Handling

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-01-14T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Check-out date must be after check-in date",
  "path": "/api/v1/hotels/search"
}
```

**HTTP Status Codes:**

- `200` - Success
- `201` - Created
- `400` - Bad Request (validation errors)
- `404` - Not Found (hotel doesn't exist)
- `500` - Internal Server Error

## 📝 Logging

Logs are written to console and follow a structured format:

```
2026-01-14 10:30:00 - Searching hotels in city: Tokyo, checkIn: 2026-01-20, checkOut: 2026-01-23
2026-01-14 10:30:00 - Found 5 hotels in city: Tokyo
```

**Log Levels:**
- `INFO` - Request/response logging
- `DEBUG` - Business decision logging
- `ERROR` - Exception stack traces

## 🎯 Non-Functional Requirements

- ✅ Clean, readable code
- ✅ Meaningful method/variable names
- ✅ No over-engineering
- ✅ Comments explain "why", not "what"
- ✅ Production mindset throughout

## 📚 API Documentation

Full API documentation is available via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:
```
http://localhost:8080/api-docs
```

## 🤝 Contributing

This project follows Travel engineering standards. When contributing:

1. Follow the layered architecture strictly
2. Write unit tests for service layer
3. Use meaningful commit messages
4. Ensure all tests pass before PR

## 📄 License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 👥 Author

Built following **Travel (TDD)** engineering standards.

---

**Note**: This is a demonstration project showcasing production-quality Spring Boot development practices. It's designed to be a reference implementation for building scalable, maintainable REST APIs.