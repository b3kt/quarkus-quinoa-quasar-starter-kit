package com.github.b3kt.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name = "idx_password_reset_token_hash", columnList = "token_hash"),
    @Index(name = "idx_password_reset_username", columnList = "username")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordResetTokenEntity that = (PasswordResetTokenEntity) o;
        return Objects.equals(tokenHash, that.tokenHash) && 
               Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenHash, username);
    }
}
