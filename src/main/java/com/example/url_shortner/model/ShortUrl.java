package com.example.url_shortner.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ShortUrl entity representing the database table.
 * It stores the original URL, generated short code, and tracking information.
 */
@Entity
@Table(name = "short_urls", indexes = {
        @Index(name = "idx_shorturl_code", columnList = "shortCode", unique = true),
        @Index(name = "idx_shorturl_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 50)
    private String shortCode;

    @Column(name = "custom_alias", unique = true, length = 50)
    private String customAlias;

    @Column(nullable = false)
    private long clickCount = 0;

    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public String getOriginalUrl() { return originalUrl; }
    public String getShortCode() { return shortCode; }
    public String getCustomAlias() { return customAlias; }
    public long getClickCount() { return clickCount; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public User getUser() { return user; }
    public void setClickCount(long clickCount) { this.clickCount = clickCount; }

    public static ShortUrlBuilder builder() {
        return new ShortUrlBuilder();
    }

    public static class ShortUrlBuilder {
        private String originalUrl;
        private String shortCode;
        private String customAlias;
        private long clickCount;
        private LocalDateTime expiresAt;
        private User user;

        ShortUrlBuilder() {}

        public ShortUrlBuilder originalUrl(String originalUrl) { this.originalUrl = originalUrl; return this; }
        public ShortUrlBuilder shortCode(String shortCode) { this.shortCode = shortCode; return this; }
        public ShortUrlBuilder customAlias(String customAlias) { this.customAlias = customAlias; return this; }
        public ShortUrlBuilder clickCount(long clickCount) { this.clickCount = clickCount; return this; }
        public ShortUrlBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public ShortUrlBuilder user(User user) { this.user = user; return this; }

        public ShortUrl build() {
            ShortUrl s = new ShortUrl();
            s.originalUrl = originalUrl;
            s.shortCode = shortCode;
            s.customAlias = customAlias;
            s.clickCount = clickCount;
            s.expiresAt = expiresAt;
            s.user = user;
            return s;
        }
    }

    /**
     * Helper to check if the URL has expired.
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
