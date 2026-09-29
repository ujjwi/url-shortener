package com.urlshortener.controller;

import com.urlshortener.dto.CreateUrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.entity.Url;
import com.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for URL shortening operations.
 *
 * This is the "entry point" for all HTTP traffic related to URL shortening.
 * It handles two responsibilities:
 *   1. POST /api/urls       → Create a short URL (returns JSON)
 *   2. GET  /{shortCode}    → Redirect to the original URL (returns 302)
 *
 * Express equivalent:
 *   const router = express.Router();
 *   router.post('/api/urls', createUrl);
 *   router.get('/:shortCode', redirectUrl);
 *
 * Notice: The controller does NO business logic. It only:
 *   - Receives the HTTP request
 *   - Delegates to the service
 *   - Builds the HTTP response
 *
 * This is the same separation you'd ideally do in Express, but Spring
 * enforces it more naturally through the layered architecture.
 */
@RestController
@RequiredArgsConstructor
public class UrlController {

    // Injected via constructor (Lombok @RequiredArgsConstructor)
    private final UrlService urlService;

    // ─── Stage 8: Create Short URL ──────────────────────────────────

    /**
     * POST /api/urls
     *
     * Accepts: { "originalUrl": "https://example.com/long/path" }
     * Returns: { "shortCode": "aB32xK", "shortUrl": "...", ... }
     *
     * @RequestBody tells Spring: "Deserialize the JSON request body into
     * this Java object." Jackson (Spring's JSON library) does this automatically.
     *
     * Express equivalent:
     *   app.post('/api/urls', (req, res) => {
     *       const { originalUrl } = req.body;  // ← @RequestBody does this
     *       const result = urlService.create(originalUrl);
     *       res.status(201).json(result);       // ← ResponseEntity does this
     *   });
     *
     * HttpStatus.CREATED = 201 (resource created successfully)
     * We use 201 instead of 200 because that's the correct REST convention
     * for resource creation — same as you'd do in Express.
     */
    @PostMapping("/api/urls")
    public ResponseEntity<UrlResponse> createShortUrl(@RequestBody CreateUrlRequest request) {
        UrlResponse response = urlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── Stage 9: Redirect ──────────────────────────────────────────

    /**
     * GET /{shortCode}
     *
     * Looks up the short code and redirects (HTTP 302) to the original URL.
     *
     * @PathVariable extracts {shortCode} from the URL path.
     * Express equivalent: req.params.shortCode
     *
     * Express equivalent:
     *   app.get('/:shortCode', (req, res) => {
     *       const url = urlService.getByShortCode(req.params.shortCode);
     *       res.redirect(url.originalUrl);   // ← sends 302 + Location header
     *   });
     *
     * In Spring, we build the redirect response manually using ResponseEntity:
     *   - Status: 302 FOUND (temporary redirect)
     *   - Location header: the original URL
     *   - Empty body (browser follows the Location header automatically)
     *
     * Why 302 and not 301?
     *   301 = Permanent redirect → browser caches it forever, bypasses our server on future visits
     *   302 = Temporary redirect → browser always hits our server first (needed for analytics later!)
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        Url url = urlService.getByShortCode(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.getOriginalUrl()))
                .build();
    }
}
