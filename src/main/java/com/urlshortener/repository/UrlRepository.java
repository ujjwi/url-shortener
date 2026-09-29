package com.urlshortener.repository;

import com.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access layer for the Url entity.
 *
 * By extending JpaRepository<Url, Long>, Spring auto-generates
 * implementations for standard CRUD operations:
 *   save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * Express/Sequelize equivalent:
 *   This is like having a model with all the built-in query methods,
 *   but you never write the SQL or the implementation — Spring does it
 *   by parsing the method name at startup.
 *
 * Node equivalent of what we get for free:
 *   save(url)              → Url.create(data) or Url.upsert(data)
 *   findById(id)           → Url.findByPk(id)
 *   findAll()              → Url.findAll()
 *   deleteById(id)         → Url.destroy({ where: { id } })
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    /**
     * Find a URL by its short code.
     *
     * Spring parses the method name "findByShortCode" and generates:
     *   SELECT * FROM urls WHERE short_code = ?
     *
     * Returns Optional<Url> — Java's way of saying "this might be null."
     * Similar to how in Node you'd check: const url = await Url.findOne(...); if (!url) ...
     * Optional forces you to handle the "not found" case explicitly.
     */
    Optional<Url> findByShortCode(String shortCode);

    /**
     * Check if a short code already exists in the database.
     *
     * Spring generates: SELECT COUNT(*) > 0 FROM urls WHERE short_code = ?
     *
     * We'll use this for collision detection during short-code generation.
     */
    boolean existsByShortCode(String shortCode);
}
