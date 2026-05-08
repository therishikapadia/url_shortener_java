# Project Structure and Flow

The URL Shortener project follows a clean layered architecture, ensuring separation of concerns and maintainability.

## 1. Project Structure

- **controller**: `UrlShortenerController` handles the REST API endpoints and maps incoming HTTP requests.
- **service**: `UrlShortenerService` encapsulates the business logic, such as random short code generation and updating click counts.
- **repository**: `ShortUrlRepository` uses Spring Data JPA to interact with the PostgreSQL database.
- **model/entity**: `ShortUrl` represents the data structure stored in the database.
- **dto**: `CreateShortUrlRequest` and `ShortUrlResponse` are used for transferring data between the client and the application, keeping entities hidden.
- **exception**: Contains custom exceptions and a `GlobalExceptionHandler` to provide consistent error responses.

## 2. API Flow

1. **Shorten URL**: `POST /api/url/shorten`
   - Receives original URL -> Validates format -> Service generates unique short code -> Saves to DB -> Returns response DTO.
2. **Redirect**: `GET /{shortCode}`
   - Service looks up code -> Increments `clickCount` -> Redirects (302) to the original URL.
3. **Analytics**: `GET /api/url/{shortCode}`
   - Service retrieves full details and click statistics for the given code.
4. **Delete**: `DELETE /api/url/{shortCode}`
   - Service removes the mapping from the database.

## 3. Sample Requests (cURL)

### Shorten a URL
```bash
curl -X POST http://localhost:8080/api/url/shorten \
     -H "Content-Type: application/json" \
     -d '{"originalUrl": "https://github.com/features/copilot"}'
```

### Get URL Details/Analytics
```bash
curl -X GET http://localhost:8080/api/url/{shortCode}
```

### Redirect (Test in Browser or Follow Redirects)
```bash
curl -L http://localhost:8080/{shortCode}
```

### Delete a Short URL
```bash
curl -X DELETE http://localhost:8080/api/url/{shortCode}
```

