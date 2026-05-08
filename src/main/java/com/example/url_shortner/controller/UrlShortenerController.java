package com.example.url_shortner.controller;

import com.example.url_shortner.dto.CreateShortUrlRequest;
import com.example.url_shortner.dto.ShortUrlResponse;
import com.example.url_shortner.service.UrlShortenerService;
import jakarta.validation.Valid;
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
@RequestMapping("/")
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
    @PostMapping("api/url/shorten")
    public ResponseEntity<ShortUrlResponse> shortenUrl(@Valid @RequestBody CreateShortUrlRequest request) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        ShortUrlResponse response = service.shortenUrl(request, baseUrl);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /{shortCode}
     * Redirect to the original URL.
     * Uses 302 Found status as requested.
     */
    @GetMapping("{shortCode:[a-zA-Z0-9]{6}}")
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
    @GetMapping("api/url/{shortCode}")
    public ResponseEntity<ShortUrlResponse> getUrlDetails(@PathVariable String shortCode) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        ShortUrlResponse response = service.getUrlDetails(shortCode, baseUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/url/{shortCode}
     * Delete a mapped URL.
     */
    @DeleteMapping("api/url/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        service.deleteShortUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}

