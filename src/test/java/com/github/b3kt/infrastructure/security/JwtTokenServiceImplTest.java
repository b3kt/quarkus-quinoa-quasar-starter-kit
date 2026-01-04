package com.github.b3kt.infrastructure.security;

import com.github.b3kt.application.properties.RbacProperties;
import com.github.b3kt.domain.model.User;
import com.github.b3kt.infrastructure.persistence.entity.RoleEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@QuarkusTest
public class JwtTokenServiceImplTest {

    @Inject
    JwtTokenServiceImpl jwtTokenService;

    @InjectMock
    RbacProperties rbacProperties;

    @Test
    public void generateToken_shouldNotThrowNPE_whenKaryawanIdIsNull() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(new HashSet<>());
        // karyawanId and karyawanNama are null by default

        Mockito.when(rbacProperties.enabled()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> {
            String token = jwtTokenService.generateToken(user);
            assertNotNull(token);
        });
    }

    @Test
    public void generateToken_shouldWork_whenKaryawanIdIsPresent() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(new HashSet<>());
        user.setKaryawanId(123L);
        user.setKaryawanNama("Test Karyawan");

        Mockito.when(rbacProperties.enabled()).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> {
            String token = jwtTokenService.generateToken(user);
            assertNotNull(token);
        });
    }
}
