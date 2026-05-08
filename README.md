# URL Shortener - Production-Style API

## Project Overview
This is a highly scalable, production-ready URL Shortener backend built with Java 17 and Spring Boot 3. It features JWT authentication, custom aliases, URL expiration, rate limiting, and real-time click analytics.

## Features
- **URL Shortening**: Generate random short codes or provide a custom alias.
- **Redirect Support**: Public endpoint to redirect short codes to original URLs.
- **JWT Authentication**: Secure endpoints for managing personal URLs.
- **Custom Aliases**: Choose your own short URL (e.g., `api/url/redirect/my-link`).
- **URL Expiration**: Set an optional TTL for your short links.
- **Rate Limiting**: Integrated Bucket4j for protecting against DDoS and abuse.
- **Pagination & Sorting**: Efficiently browse through your links.
- **Search**: Search your personal links by original URL or short code.
- **Analytics**: Track total click counts for every link.
- **API Documentation**: Interactive Swagger UI for easy testing.

## Tech Stack
- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security** (JWT + BCrypt)
- **PostgreSQL** (with optimized indexing)
- **Spring Data JPA**
- **Maven**
- **Lombok**
- **Bucket4j** (Rate Limiting)
- **SpringDoc OpenAPI** (Swagger)

## Architecture Explanation
The project follows a **Layered Architecture**:
1. **Controller Layer**: Handles REST requests and validates DTOs.
2. **Service Layer**: Contains core business logic (aliasing, expiration, ownership checks).
3. **Repository Layer**: Abstraction over the database using Spring Data JPA.
4. **Model Layer**: JPA entities representing the relational database schema.
5. **Security Layer**: Custom filters and configurations for JWT authentication.

## Authentication Flow
1. **Registration**: User registers with username, email, and password (hashed via BCrypt).
2. **Login**: User provides credentials and receives a JWT token.
3. **Authorized Requests**: Include the token in the `Authorization: Bearer <token>` header.

## Database Optimization
- **Indexes**:
    - `users(username)`: Unique index for fast login/lookup.
    - `short_urls(short_code)`: Unique index for lightning-fast redirection ($O(1)$ lookup).
    - `short_urls(user_id)`: Index for optimized paginated queries of user links.
- **Audit Fields**: Automatic tracking of `createdAt` and `updatedAt` for every record.

## Setup Instructions

### Prerequisites
- JDK 17
- PostgreSQL 15+
- Maven

### Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

# JWT Secret
app.jwtSecret=your_very_secure_and_long_secret_key_here
app.jwtExpirationMs=86400000
```

### Running the App
```bash
mvn clean install
mvn spring-boot:run
```

## API Documentation
The API is fully documented using Swagger/OpenAPI.
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **API Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Sample Requests

### 1. Register User
`POST /api/auth/signup`
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

### 2. Login
`POST /api/auth/signin`
```json
{
  "username": "johndoe",
  "password": "password123"
}
```
**Response**: Grabs the `token` for further requests.

### 3. Create Short URL (Protected)
`POST /api/url/shorten`
```json
{
  "originalUrl": "https://www.github.com/someone/production-repo",
  "customAlias": "my-repo",
  "expiresAt": "2026-12-31T23:59:59"
}
```

### 4. Get My URLs (Paginated & Sorted)
`GET /api/url/my-urls?page=0&size=5&sort=clickCount,desc`

## Rate Limiting
- **Limit**: 10 requests per minute per user/IP.
- **Behavior**: Returns `429 Too Many Requests` when exceeded.

## Future Improvements
- **Caching**: Use Redis to cache redirects for even faster lookups.
- **Admin Dashboard**: Role-based access for system-wide analytics.
- **Custom Domain Support**: Allow users to use their own domains for shortening.
- **Report Generation**: Export analytics as PDF/CSV.

---
*Developed with best practices for Clean Code and Production Engineering.*
