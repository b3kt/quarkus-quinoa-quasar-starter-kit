package com.github.b3kt.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void canAuthenticate_shouldReturnTrue_whenActiveAndUsernameSet() {
        User user = new User("testuser", "test@example.com", "hash", Set.of("user"));
        assertTrue(user.canAuthenticate());
    }

    @Test
    void canAuthenticate_shouldReturnFalse_whenInactive() {
        User user = new User("testuser", "test@example.com", "hash", Set.of("user"));
        user.setActive(false);
        assertFalse(user.canAuthenticate());
    }

    @Test
    void canAuthenticate_shouldReturnFalse_whenUsernameIsNull() {
        User user = new User();
        user.setActive(true);
        user.setUsername(null);
        assertFalse(user.canAuthenticate());
    }

    @Test
    void canAuthenticate_shouldReturnFalse_whenUsernameIsEmpty() {
        User user = new User();
        user.setActive(true);
        user.setUsername("");
        assertFalse(user.canAuthenticate());
    }
}
