package com.example.url_shortner.service;

import com.example.url_shortner.dto.CreateShortUrlRequest;
import com.example.url_shortner.dto.ShortUrlResponse;
import com.example.url_shortner.exception.ResourceNotFoundException;
import com.example.url_shortner.model.ShortUrl;
import com.example.url_shortner.model.User;
import com.example.url_shortner.repository.ShortUrlRepository;
import com.example.url_shortner.repository.UserRepository;
import com.example.url_shortner.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;


/**
 * Service Layer Purpose:
 * This class contains the business logic for the application.
 * It coordinates between the controller and the repository.
 * Abstraction: The service layer abstracts the repository and provides high-level operations.
 */
@Service
public class UrlShortenerService {

    private final ShortUrlRepository repository;
    private final UserRepository userRepository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    // Constructor Injection instead of Field Injection
    public UrlShortenerService(ShortUrlRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    /**
     * Business logic to shorten a URL:
     * 1. Check for custom alias or generate a random short code.
     * 2. Ensure its uniqueness.
     * 3. Associate with the current authenticated user.
     * 4. Save to database.
     */
    @Transactional
    public ShortUrlResponse shortenUrl(CreateShortUrlRequest request, String baseUrl) {
        User currentUser = getCurrentUser();
        String shortCode;

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            if (repository.existsByShortCode(request.getCustomAlias())) {
                throw new IllegalArgumentException("Alias already exists: " + request.getCustomAlias());
            }
            shortCode = request.getCustomAlias();
        } else {
            shortCode = generateUniqueShortCode();
        }

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .customAlias(request.getCustomAlias())
                .expiresAt(request.getExpiresAt())
                .clickCount(0)
                .user(currentUser)
                .build();

        ShortUrl savedUrl = repository.save(shortUrl);
        return mapToResponse(savedUrl, baseUrl);
    }

    /**
     * Logic to retrieve original URL and increment click count.
     * Checks for expiration before redirecting.
     */
    @Transactional
    public String getOriginalUrl(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));

        if (shortUrl.isExpired()) {
            throw new IllegalStateException("This short URL has expired.");
        }

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        repository.save(shortUrl);

        return shortUrl.getOriginalUrl();
    }

    /**
     * Logic to get URL details and analytics.
     * Restricts access to the owner of the URL.
     */
    @Transactional(readOnly = true)
    public ShortUrlResponse getUrlDetails(String shortCode, String baseUrl) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));

        validateOwnership(shortUrl);

        return mapToResponse(shortUrl, baseUrl);
    }

    /**
     * Logic to delete a shortened URL.
     * Restricts deletion to the owner.
     */
    @Transactional
    public void deleteShortUrl(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found for code: " + shortCode));

        validateOwnership(shortUrl);

        repository.delete(shortUrl);
    }

    /**
     * Get all URLs for the current user with pagination and sorting.
     */
    @Transactional(readOnly = true)
    public Page<ShortUrlResponse> getMyUrls(Pageable pageable, String baseUrl) {
        User currentUser = getCurrentUser();
        return repository.findByUser(currentUser, pageable)
                .map(url -> mapToResponse(url, baseUrl));
    }

    /**
     * Search URLs for the current user.
     */
    @Transactional(readOnly = true)
    public Page<ShortUrlResponse> searchMyUrls(String query, Pageable pageable, String baseUrl) {
        User currentUser = getCurrentUser();
        return repository.searchByUserAndQuery(currentUser, query, pageable)
                .map(url -> mapToResponse(url, baseUrl));
    }

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateOwnership(ShortUrl shortUrl) {
        User currentUser = getCurrentUser();
        if (!shortUrl.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to access this URL");
        }
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
                .shortenedUrl(baseUrl + "/api/url/redirect/" + entity.getShortCode())
                .customAlias(entity.getCustomAlias())
                .clickCount(entity.getClickCount())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

