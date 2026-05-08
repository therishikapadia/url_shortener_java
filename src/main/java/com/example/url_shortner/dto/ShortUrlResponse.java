package com.example.url_shortner.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO usage:
 * This class is used to return URL data to the client.
 * Using DTOs instead of entities prevents exposing sensitive database fields.
 */
@Data
@Builder
@NoArgsConstructor
public class ShortUrlResponse {
    private String originalUrl;
    private String shortCode;
    private String shortenedUrl;
    private String customAlias;
    private long clickCount;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public ShortUrlResponse(String originalUrl, String shortCode, String shortenedUrl, String customAlias, long clickCount, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.shortenedUrl = shortenedUrl;
        this.customAlias = customAlias;
        this.clickCount = clickCount;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static ShortUrlResponseBuilder builder() {
        return new ShortUrlResponseBuilder();
    }

    public static class ShortUrlResponseBuilder {
        private String originalUrl;
        private String shortCode;
        private String shortenedUrl;
        private String customAlias;
        private long clickCount;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;

        ShortUrlResponseBuilder() {}

        public ShortUrlResponseBuilder originalUrl(String originalUrl) { this.originalUrl = originalUrl; return this; }
        public ShortUrlResponseBuilder shortCode(String shortCode) { this.shortCode = shortCode; return this; }
        public ShortUrlResponseBuilder shortenedUrl(String shortenedUrl) { this.shortenedUrl = shortenedUrl; return this; }
        public ShortUrlResponseBuilder customAlias(String customAlias) { this.customAlias = customAlias; return this; }
        public ShortUrlResponseBuilder clickCount(long clickCount) { this.clickCount = clickCount; return this; }
        public ShortUrlResponseBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public ShortUrlResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ShortUrlResponse build() {
            return new ShortUrlResponse(originalUrl, shortCode, shortenedUrl, customAlias, clickCount, expiresAt, createdAt);
        }
    }
}
