package com.example.url_shortner.dto;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ShortUrlResponse {
    private String originalUrl;
    private String shortCode;
    private String shortenedUrl;
    private long clickCount;
    private LocalDateTime createdAt;
}

