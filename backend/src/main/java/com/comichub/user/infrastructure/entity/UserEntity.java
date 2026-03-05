package com.comichub.user.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String authProvider;

    protected UserEntity() {}

    public UserEntity(UUID id, String email, Instant createdAt, String authProvider) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.authProvider = authProvider;
    }

    public UUID getId()             { return id; }
    public String getEmail()        { return email; }
    public Instant getCreatedAt()   { return createdAt; }
    public String getAuthProvider() { return authProvider; }
}
