package com.github.b3kt.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @Test
    void useJpaRepository_shouldReturnFalse_whenRepositoryTypeNotSet() {
        DatabaseConfig config = new DatabaseConfig();
        assertFalse(config.useJpaRepository());
    }
}
