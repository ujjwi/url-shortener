package com.urlshortener.dto;

import java.time.LocalDateTime;

/**
 * DTO for URL responses returned to the client.
 *
 * This is what the caller sees — never the raw Url entity.
 * Decoupling entity from response means:
 *   - DB schema changes don't break the API contract
 *   - You control exactly what fields the client sees
 *   - You can add computed fields (like shortUrl) that don't exist in the DB
 *
 * Express equivalent:
 *   res.json({
 *       shortCode: doc.shortCode,
 *       shortUrl: `http://localhost:8080/${doc.shortCode}`,
 *       originalUrl: doc.originalUrl,
 *       createdAt: doc.createdAt
 *   });
 */
public record UrlResponse(
    String shortCode,
    String shortUrl,
    String originalUrl,
    LocalDateTime createdAt
) {}
