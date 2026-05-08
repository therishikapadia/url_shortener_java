package com.example.url_shortner.repository;

import com.example.url_shortner.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository role:
 * This interface handles all database operations for the ShortUrl entity.
 * Spring Data JPA provides implementation for standard methods like findById, save, etc.
 */
@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    /**
     * Find a ShortUrl by its unique short code.
     */
    Optional<ShortUrl> findByShortCode(String shortCode);

    /**
     * Check if a short code already exists in the database.
     */
    boolean existsByShortCode(String shortCode);
}

