package com.github.b3kt.infrastructure.persistence.entity;

import com.github.b3kt.domain.model.User;
import com.github.b3kt.infrastructure.persistence.listener.AuditEntityListener;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA Entity for User.
 * This is the persistence representation of the domain User entity.
 */
@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username", unique = true),
    @Index(name = "idx_email", columnList = "email")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Convert to domain User entity.
     */
    public User toDomain() {
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPasswordHash(this.passwordHash);
        user.setRoles(new HashSet<>(this.roles));
        user.setActive(this.active);
        return user;
    }

    /**
     * Create from domain User entity.
     */
    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRoles(new HashSet<>(user.getRoles()));
        entity.setActive(user.isActive());
        return entity;
    }
}

