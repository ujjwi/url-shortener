package com.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a shortened URL.
 *
 * This class maps to the "urls" table in PostgreSQL.
 * Each instance of this class = one row in the table.
 *
 * Express/Sequelize equivalent:
 *   const Url = sequelize.define('Url', {
 *       id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
 *       shortCode: { type: DataTypes.STRING(10), unique: true, allowNull: false },
 *       originalUrl: { type: DataTypes.STRING(2048), allowNull: false },
 *       createdAt: { type: DataTypes.DATE, defaultValue: DataTypes.NOW }
 *   });
 *
 * Mongoose equivalent:
 *   const urlSchema = new Schema({
 *       shortCode: { type: String, required: true, unique: true },
 *       originalUrl: { type: String, required: true },
 *       createdAt: { type: Date, default: Date.now }
 *   });
 */

// ---------------------
// JPA ANNOTATIONS
// ---------------------

// @Entity tells JPA: "This Java class represents a database table."
// Without it, this is just a regular class. With it, Hibernate tracks it,
// generates SQL for it, and maps its fields to columns.
@Entity

// @Table customizes the table name. Without it, JPA would name the table "Url"
// (the class name). We want "urls" (lowercase, plural) — a common DB convention.
@Table(name = "urls")

// ---------------------
// LOMBOK ANNOTATIONS
// ---------------------
// These generate boilerplate code AT COMPILE TIME so you don't have to write it.
// Lombok is NOT a Spring thing — it's a separate Java library.

// @Getter — generates getters for ALL fields: getId(), getShortCode(), etc.
// @Setter — generates setters for ALL fields: setId(...), setShortCode(...), etc.
// Express parallel: In JS you'd just access obj.shortCode directly. Java uses
// getters/setters by convention (and JPA/Hibernate REQUIRES them).
@Getter
@Setter

// @NoArgsConstructor — generates: public Url() {}
// JPA REQUIRES a no-arg constructor to create entity instances via reflection.
// (Hibernate calls new Url() internally, then uses setters to populate fields.)
@NoArgsConstructor

// @AllArgsConstructor — generates: public Url(Long id, String shortCode, ...)
// Convenient for creating instances in your own code.
@AllArgsConstructor
public class Url {

    // @Id marks this field as the PRIMARY KEY.
    // Every JPA entity MUST have exactly one @Id field.
    @Id

    // @GeneratedValue tells the DB to auto-generate this value.
    // GenerationType.IDENTITY = use the database's auto-increment feature.
    // PostgreSQL will use a BIGSERIAL column (auto-incrementing bigint).
    //
    // Other strategies exist (SEQUENCE, TABLE, AUTO) but IDENTITY is the
    // simplest to start with.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column customizes how this field maps to a database column.
    //   - name: column name in the DB (default would be "shortCode" → "short_code"
    //           with some naming strategies, but we're being explicit)
    //   - nullable = false: adds a NOT NULL constraint
    //   - unique = true: adds a UNIQUE constraint
    //   - length: sets VARCHAR length (default is 255)
    @Column(name = "short_code", nullable = false, unique = true, length = 10)
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    // LocalDateTime is Java's modern date-time type (java.time package).
    // JPA maps it to a TIMESTAMP column in PostgreSQL.
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
