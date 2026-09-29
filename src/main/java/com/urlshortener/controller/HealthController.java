package com.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller — verifies the application is running.
 *
 * Express equivalent:
 *   router.get("/api/health", (req, res) => res.json({ status: "UP" }));
 */

// @RestController = @Controller + @ResponseBody
// Tells Spring: "This class handles HTTP requests, and every method's
// return value should be serialized directly to the response body (as JSON)."
// In Express terms, it's like a router file where every handler does res.json().
@RestController

// @RequestMapping sets the base path for ALL endpoints in this controller.
// Like: const router = express.Router(); app.use("/api", router);
@RequestMapping("/api")
public class HealthController {

    // @GetMapping = handles HTTP GET requests.
    // Combined with @RequestMapping("/api"), this endpoint is: GET /api/health
    // Express equivalent: router.get("/health", handler)
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        // ResponseEntity gives you full control over the HTTP response:
        //   - status code (200, 404, etc.)
        //   - headers
        //   - body
        // It's like Express's res.status(200).json({...})

        Map<String, Object> response = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        );

        // ResponseEntity.ok() = HTTP 200 + body
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> appinfo() {
        // ResponseEntity gives you full control over the HTTP response:
        //   - status code (200, 404, etc.)
        //   - headers
        //   - body
        // It's like Express's res.status(200).json({...})

        Map<String, Object> response = Map.of(
                "app", "url-shortener", 
                "version", "0.0.1",
                "java", "25"
        );

        // ResponseEntity.ok() = HTTP 200 + body
        return ResponseEntity.ok(response);
    }
}
