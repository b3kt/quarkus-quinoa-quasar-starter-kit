package com.github.b3kt.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenEntityTest {

    @Test
    void equals_shouldReturnTrue_whenSameInstance() {
        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        assertTrue(entity.equals(entity));
    }

    @Test
    void equals_shouldReturnFalse_whenNull() {
        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        assertFalse(entity.equals(null));
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        assertFalse(entity.equals("string"));
    }

    @Test
    void equals_shouldReturnTrue_whenSameTokenHashAndUsername() {
        PasswordResetTokenEntity a = new PasswordResetTokenEntity(1L, "hash1", "user1", new Date(), false);
        PasswordResetTokenEntity b = new PasswordResetTokenEntity(2L, "hash1", "user1", new Date(), false);
        assertTrue(a.equals(b));
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentTokenHash() {
        PasswordResetTokenEntity a = new PasswordResetTokenEntity(1L, "hash1", "user1", new Date(), false);
        PasswordResetTokenEntity b = new PasswordResetTokenEntity(2L, "hash2", "user1", new Date(), false);
        assertFalse(a.equals(b));
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentUsername() {
        PasswordResetTokenEntity a = new PasswordResetTokenEntity(1L, "hash1", "user1", new Date(), false);
        PasswordResetTokenEntity b = new PasswordResetTokenEntity(2L, "hash1", "user2", new Date(), false);
        assertFalse(a.equals(b));
    }

    @Test
    void hashCode_shouldBeConsistent() {
        PasswordResetTokenEntity a = new PasswordResetTokenEntity(1L, "hash1", "user1", new Date(), false);
        PasswordResetTokenEntity b = new PasswordResetTokenEntity(2L, "hash1", "user1", new Date(), false);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_shouldDifferForDifferentValues() {
        PasswordResetTokenEntity a = new PasswordResetTokenEntity(1L, "hash1", "user1", new Date(), false);
        PasswordResetTokenEntity b = new PasswordResetTokenEntity(2L, "hash2", "user2", new Date(), false);
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}
