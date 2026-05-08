package com.example.url_shortner.controller;

import com.example.url_shortner.dto.CreateShortUrlRequest;
import com.example.url_shortner.dto.ShortUrlResponse;
import com.example.url_shortner.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Request flow starts here:
 * This controller handles incoming HTTP requests and directs them to the service layer.
 */
@RestController
@RequestMapping("/api/url")
@Tag(name = "URL Management", description = "Endpoints for creating and managing shortened URLs")
public class UrlShortenerController {

    private final UrlShortenerService service;

    // Constructor Injection
    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    /**
     * POST /api/url/shorten
     * Create a shortened version of the provided URL.
     */
    @PostMapping("/shorten")
    @Operation(summary = "Shorten a long URL", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ShortUrlResponse> shortenUrl(@Valid @RequestBody CreateShortUrlRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        ShortUrlResponse response = service.shortenUrl(request, baseUrl);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/url/redirect/{shortCode}
     * Redirect to the original URL.
     */
    @GetMapping("/redirect/{shortCode}")
    @Operation(summary = "Redirect to original URL by short code or alias")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortCode) {
        String originalUrl = service.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    /**
     * GET /api/url/{shortCode}
     * Retrieve URL details and analytics.
     */
    @GetMapping("/{shortCode}")
    @Operation(summary = "Get URL details and click count", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ShortUrlResponse> getUrlDetails(@PathVariable String shortCode) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        ShortUrlResponse response = service.getUrlDetails(shortCode, baseUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/url/{shortCode}
     * Delete a mapped URL.
     */
    @DeleteMapping("/{shortCode}")
    @Operation(summary = "Delete a shortened URL", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        service.deleteShortUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/url/my-urls
     * Get all URLs for current user with pagination and sorting.
     */
    @GetMapping("/my-urls")
    @Operation(summary = "Get current user's URLs with pagination and sorting", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<ShortUrlResponse>> getMyUrls(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return ResponseEntity.ok(service.getMyUrls(pageable, baseUrl));
    }

    /**
     * GET /api/url/search
     * Search URLs for current user.
     */
    @GetMapping("/search")
    @Operation(summary = "Search current user's URLs", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<ShortUrlResponse>> searchUrls(
            @RequestParam String query,
            @PageableDefault(size = 10) Pageable pageable) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return ResponseEntity.ok(service.searchMyUrls(query, pageable, baseUrl));
    }
}
