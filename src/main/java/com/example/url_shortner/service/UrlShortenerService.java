package com.example.url_shortner.service;

import com.example.url_shortner.dto.CreateShortUrlRequest;
import com.example.url_shortner.dto.ShortUrlResponse;
import com.example.url_shortner.exception.ResourceNotFoundException;
import com.example.url_shortner.model.ShortUrl;
import com.example.url_shortner.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;


/**
 * Service Layer Purpose:
 * This class contains the business logic for the application.
 * It coordinates between the controller and the repository.
 */
@Service
public class UrlShortenerService {

    private final ShortUrlRepository repository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    // Constructor Injection instead of Field Injection
    public UrlShortenerService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    /**
     * Business logic to shorten a URL:
     * 1. Generate a random alphanumeric code.
     * 2. Ensure its uniqueness.
     * 3. Save to database.
     */
    @Transactional
    public ShortUrlResponse shortenUrl(CreateShortUrlRequest request, String baseUrl) {
        String shortCode = generateUniqueShortCode();

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .clickCount(0)
                .build();

        ShortUrl savedUrl = repository.save(shortUrl);
        return mapToResponse(savedUrl, baseUrl);
    }

    /**
     * Logic to retrieve original URL and increment click count.
     */
    @Transactional
    public String getOriginalUrl(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        repository.save(shortUrl);

        return shortUrl.getOriginalUrl();
    }

    /**
     * Logic to get URL details and analytics.
     */
    @Transactional(readOnly = true)
    public ShortUrlResponse getUrlDetails(String shortCode, String baseUrl) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));

        return mapToResponse(shortUrl, baseUrl);
    }

    /**
     * Logic to delete a shortened URL.
     */
    @Transactional
    public void deleteShortUrl(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));

        repository.delete(shortUrl);
    }

    private String generateUniqueShortCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            code = sb.toString();
        } while (repository.existsByShortCode(code));
        return code;
    }

    private ShortUrlResponse mapToResponse(ShortUrl entity, String baseUrl) {
        return ShortUrlResponse.builder()
                .originalUrl(entity.getOriginalUrl())
                .shortCode(entity.getShortCode())
                .shortenedUrl(baseUrl + "/" + entity.getShortCode())
                .clickCount(entity.getClickCount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}


