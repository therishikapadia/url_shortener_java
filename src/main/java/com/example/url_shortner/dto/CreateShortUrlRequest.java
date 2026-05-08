package com.example.url_shortner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO usage:
 * This class is used to capture the request body when creating a new short URL.
 * It ensures validation rules are applied before reaching the service layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortUrlRequest {

    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
             message = "Invalid URL format")
    private String originalUrl;

    @Size(min = 3, max = 50, message = "Alias must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]*$", message = "Alias can only contain alphanumeric characters, hyphens, and underscores")
    private String customAlias;

    private LocalDateTime expiresAt;

    public String getOriginalUrl() { return originalUrl; }
    public String getCustomAlias() { return customAlias; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
