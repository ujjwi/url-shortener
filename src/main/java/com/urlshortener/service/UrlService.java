package com.urlshortener.service;

import com.urlshortener.dto.CreateUrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.entity.Url;
import com.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service layer — contains ALL business logic for URL shortening.
 *
 * Express equivalent:
 *   This is like a service module (e.g. urlService.js) that sits between
 *   your route handlers and your database models.
 *   In Express you might write this logic directly in the route handler,
 *   but separating it makes the code testable and reusable.
 *
 * Key Spring concepts:
 *
 * @Service — marks this as a Spring-managed bean (singleton by default).
 *   Spring creates ONE instance at startup and injects it wherever needed.
 *   It's semantically identical to @Component but communicates intent:
 *   "this class holds business logic."
 *
 * Constructor Injection:
 *   In Express, you'd: const urlRepo = require('./urlRepository');
 *   In Spring, you declare dependencies as constructor parameters and
 *   Spring supplies them automatically. @RequiredArgsConstructor (Lombok)
 *   generates the constructor for us from the 'final' fields.
 *
 *   Why 'final'? It makes dependencies immutable after construction —
 *   the service can never accidentally swap its repository at runtime.
 */
@Service
@RequiredArgsConstructor
public class UrlService {

    // These are injected by Spring via the constructor that Lombok generates.
    // 'final' + @RequiredArgsConstructor = constructor injection without writing the constructor.
    private final UrlRepository urlRepository;

    // Base URL for constructing the full short URL.
    // Hardcoded for now — we'll extract this to application.properties later.
    private static final String BASE_URL = "http://localhost:8080/";

    /**
     * Creates a shortened URL.
     *
     * Flow:
     *   1. Generate a unique short code (with collision handling)
     *   2. Build and save the Url entity
     *   3. Map entity → DTO and return
     */
    public UrlResponse createShortUrl(CreateUrlRequest request) {

        // Generate a unique short code (placeholder — Stage 7 will add the real generator)
        String shortCode = generateUniqueCode();

        // Build the entity
        Url url = new Url();
        url.setShortCode(shortCode);
        url.setOriginalUrl(request.originalUrl());  // Note: record getter has no "get" prefix!
        url.setCreatedAt(LocalDateTime.now());

        // Save to DB — JpaRepository.save() does INSERT (new) or UPDATE (existing)
        // This is like: await Url.create({ shortCode, originalUrl, createdAt }) in Sequelize
        Url savedUrl = urlRepository.save(url);

        // Map entity → response DTO
        return mapToResponse(savedUrl);
    }

    /**
     * Looks up a URL by its short code.
     *
     * Returns the Url entity (not a DTO) because the redirect endpoint
     * doesn't need to return JSON — it just needs the originalUrl.
     */
    public Url getByShortCode(String shortCode) {
        // .orElseThrow() unwraps the Optional or throws if empty.
        // For now we throw a generic RuntimeException — Stage 11 will
        // replace this with a custom UrlNotFoundException + global handler.
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));
    }

    // ─── Private helpers ────────────────────────────────────────────

    /**
     * Generates a unique short code with collision retry.
     * Uses a simple random approach for now — Stage 7 will extract this
     * into a dedicated ShortCodeGenerator utility.
     */
    private String generateUniqueCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int codeLength = 7;
        int maxAttempts = 10;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            StringBuilder sb = new StringBuilder(codeLength);
            for (int i = 0; i < codeLength; i++) {
                int index = (int) (Math.random() * characters.length());
                sb.append(characters.charAt(index));
            }
            String code = sb.toString();

            // Collision check — if this code already exists, try again
            if (!urlRepository.existsByShortCode(code)) {
                return code;
            }
        }

        throw new RuntimeException("Failed to generate unique short code after " + maxAttempts + " attempts");
    }

    /**
     * Maps a Url entity to a UrlResponse DTO.
     *
     * This is the "serialization boundary" — we control exactly what the
     * client sees. The entity might have fields (like 'id') that we don't
     * want to expose.
     */
    private UrlResponse mapToResponse(Url url) {
        return new UrlResponse(
                url.getShortCode(),
                BASE_URL + url.getShortCode(),
                url.getOriginalUrl(),
                url.getCreatedAt()
        );
    }
}
