package com.chuwa.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Mapping object for a database table
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique Account identifier
    @Column(nullable = false, unique = true, length = 128)
    private String email;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    // Save password (BCrypt)
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
