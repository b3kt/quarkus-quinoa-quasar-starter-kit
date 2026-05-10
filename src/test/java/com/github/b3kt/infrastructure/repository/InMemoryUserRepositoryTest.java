package com.github.b3kt.infrastructure.repository;

import com.github.b3kt.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotExists() {
        Optional<User> result = repository.findByUsername("nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void saveAndFindByUsername_shouldReturnUser() {
        User user = new User("testuser", "test@example.com", "hash", Set.of("user"));
        repository.save(user);

        Optional<User> result = repository.findByUsername("testuser");
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenNotExists() {
        assertFalse(repository.existsByUsername("nonexistent"));
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenExists() {
        User user = new User("testuser", "test@example.com", "hash", Set.of("user"));
        repository.save(user);
        assertTrue(repository.existsByUsername("testuser"));
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        repository.save(new User("user1", "a@b.com", "hash1", Set.of("user")));
        repository.save(new User("user2", "b@b.com", "hash2", Set.of("user")));

        List<User> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void save_shouldOverwriteExistingUser() {
        User user = new User("testuser", "original@example.com", "hash1", Set.of("user"));
        repository.save(user);

        User updated = new User("testuser", "updated@example.com", "hash2", Set.of("admin"));
        repository.save(updated);

        Optional<User> result = repository.findByUsername("testuser");
        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
    }
}
