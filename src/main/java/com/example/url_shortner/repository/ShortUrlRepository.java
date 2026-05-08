package com.example.url_shortner.repository;

import com.example.url_shortner.model.ShortUrl;
import com.example.url_shortner.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Find all URLs belonging to a specific user with pagination and sorting.
     */
    Page<ShortUrl> findByUser(User user, Pageable pageable);

    /**
     * Search for URLs belonging to a specific user by original URL or short code/alias.
     */
    @Query("SELECT s FROM ShortUrl s WHERE s.user = :user AND " +
           "(LOWER(s.originalUrl) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.shortCode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ShortUrl> searchByUserAndQuery(@Param("user") User user, @Param("query") String query, Pageable pageable);
}
