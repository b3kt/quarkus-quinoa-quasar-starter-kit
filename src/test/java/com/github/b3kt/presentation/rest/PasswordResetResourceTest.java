package com.github.b3kt.presentation.rest;

import com.github.b3kt.infrastructure.persistence.entity.PasswordResetTokenEntity;
import com.github.b3kt.infrastructure.persistence.repository.PasswordResetTokenRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HexFormat;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordResetResourceTest {

    static final String ORIGINAL_PASSWORD = "admin123";
    static final String NEW_PASSWORD = "N3wP@ssword!";
    static final String RESET_BACK_PASSWORD = "Admin@123";

    @Inject
    PasswordResetTokenRepository tokenRepository;

    private String extractedToken;

    private static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    void forgotPassword_shouldReturn200_whenUserExists() {
        String response = given()
                .body("{\"username\":\"admin\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/forgot-password")
                .then().statusCode(200)
                .body("success", is(true))
                .body("message", containsString("reset token"))
                .extract().asString();

        if (response.contains("\"token\"")) {
            extractedToken = response.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        }
    }

    @Test
    @Order(2)
    void forgotPassword_shouldReturn200_whenUserNotExists() {
        given()
                .body("{\"username\":\"nonexistent_user_abc123\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/forgot-password")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(3)
    void forgotPassword_shouldReturn400_whenValidationFails() {
        given()
                .body("{\"username\":\"\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/forgot-password")
                .then().statusCode(400);
    }

    @Test
    @Order(4)
    void resetPassword_shouldReturn400_whenInvalidToken() {
        given()
                .body("{\"token\":\"invalidtoken123\",\"newPassword\":\"" + NEW_PASSWORD + "\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/reset-password")
                .then().statusCode(400)
                .body("success", is(false));
    }

    @Test
    @Order(5)
    void resetPassword_shouldReturn400_whenTokenEmpty() {
        given()
                .body("{\"token\":\"\",\"newPassword\":\"" + NEW_PASSWORD + "\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/reset-password")
                .then().statusCode(400);
    }

    @Test
    @Order(6)
    void resetPassword_shouldReturn400_whenPasswordTooShort() {
        given()
                .body("{\"token\":\"sometoken\",\"newPassword\":\"short\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/reset-password")
                .then().statusCode(400);
    }

    @Test
    @Order(7)
    void resetPassword_shouldReturn200_withValidToken() {
        String rawToken = given()
                .body("{\"username\":\"admin\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/forgot-password")
                .then().statusCode(200)
                .extract().path("data.token");

        if (rawToken != null) {
            given()
                    .body("{\"token\":\"" + rawToken + "\",\"newPassword\":\"" + NEW_PASSWORD + "\"}")
                    .contentType(ContentType.JSON)
                    .when().post("/api/auth/reset-password")
                    .then().statusCode(200)
                    .body("success", is(true));
        }
    }

    @Test
    @Order(8)
    void resetPassword_shouldReturn400_whenTokenAlreadyUsed() {
        String rawToken = given()
                .body("{\"username\":\"admin\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/forgot-password")
                .then().statusCode(200)
                .extract().path("data.token");

        if (rawToken != null) {
            given()
                    .body("{\"token\":\"" + rawToken + "\",\"newPassword\":\"" + NEW_PASSWORD + "\"}")
                    .contentType(ContentType.JSON)
                    .when().post("/api/auth/reset-password")
                    .then().statusCode(200);

            given()
                    .body("{\"token\":\"" + rawToken + "\",\"newPassword\":\"" + NEW_PASSWORD + "\"}")
                    .contentType(ContentType.JSON)
                    .when().post("/api/auth/reset-password")
                    .then().statusCode(400)
                    .body("success", is(false));
        }
    }

    @Test
    @Order(9)
    void loginWithNewPassword_shouldSucceed() {
        given()
                .body("{\"username\":\"admin\",\"password\":\"" + NEW_PASSWORD + "\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(10)
    void resetAdminPasswordBack_shouldSucceed() {
        String rawToken = given()
                .body("{\"username\":\"admin\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/forgot-password")
                .then().statusCode(200)
                .extract().path("data.token");

        if (rawToken != null) {
            given()
                    .body("{\"token\":\"" + rawToken + "\",\"newPassword\":\"" + RESET_BACK_PASSWORD + "\"}")
                    .contentType(ContentType.JSON)
                    .when().post("/api/auth/reset-password")
                    .then().statusCode(200)
                    .body("success", is(true));
        }
    }

    @Test
    @Order(11)
    void loginWithResetBackPassword_shouldSucceed() {
        given()
                .body("{\"username\":\"admin\",\"password\":\"" + RESET_BACK_PASSWORD + "\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(12)
    @Transactional
    void resetPassword_shouldReturn400_whenTokenExpired() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        String rawToken = HexFormat.of().formatHex(tokenBytes);
        String tokenHash = hashToken(rawToken);

        PasswordResetTokenEntity expiredEntity = new PasswordResetTokenEntity();
        expiredEntity.setTokenHash(tokenHash);
        expiredEntity.setUsername("admin");
        expiredEntity.setExpiresAt(new Date(System.currentTimeMillis() - 60_000));
        expiredEntity.setUsed(false);
        tokenRepository.persist(expiredEntity);

        given()
                .body("{\"token\":\"" + rawToken + "\",\"newPassword\":\"" + NEW_PASSWORD + "\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/reset-password")
                .then().statusCode(400)
                .body("success", is(false));
    }
}
