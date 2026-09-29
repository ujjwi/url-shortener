package com.urlshortener.dto;

/**
 * DTO for the POST /api/urls request body.
 *
 * Java Records (Java 16+):
 *   A record is an immutable data carrier. The compiler generates:
 *     - A constructor: new CreateUrlRequest("https://...")
 *     - A getter:      request.originalUrl()   (note: no "get" prefix!)
 *     - equals(), hashCode(), toString()
 *
 * Express equivalent:
 *   const { originalUrl } = req.body;
 *   — except here, the shape is enforced at compile time.
 *
 * Jackson (Spring's JSON library) automatically deserializes
 * { "originalUrl": "..." } into this record.
 */
public record CreateUrlRequest(
    String originalUrl
) {}
