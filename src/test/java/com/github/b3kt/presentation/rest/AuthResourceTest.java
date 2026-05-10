package com.github.b3kt.presentation.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.b3kt.infrastructure.repository.UserRepository;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthResourceTest {

    @Inject
    UserRepository userRepository;

    static final String USERNAME = "authtestuser";
    static final String EMAIL = "authtest@example.com";
    static final String PASSWORD = "TestP@ss1";

    @Test
    @Order(1)
    void register_shouldReturn201() {
        given()
                .body("{\"username\":\"" + USERNAME + "\",\"email\":\"" + EMAIL + "\",\"password\":\"" + PASSWORD + "\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/register")
                .then().statusCode(201)
                .body("success", is(true))
                .body("data.username", is(USERNAME))
                .body("data.email", is(EMAIL));
    }

    @Test
    @Order(2)
    void register_shouldReturn400_whenUsernameTaken() {
        given()
                .body("{\"username\":\"" + USERNAME + "\",\"email\":\"other@example.com\",\"password\":\"" + PASSWORD + "\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/register")
                .then().statusCode(400)
                .body("success", is(false));
    }

    @Test
    @Order(3)
    void register_shouldReturn400_whenValidationFails() {
        given()
                .body("{\"username\":\"\",\"email\":\"bad\",\"password\":\"1\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/register")
                .then().statusCode(400);
    }

    @Test
    @Order(4)
    void login_shouldReturn200() {
        given()
                .body("{\"username\":\"admin\",\"password\":\"admin123\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .body("success", is(true))
                .body("data.token", notNullValue())
                .body("data.username", is("admin"));
    }

    @Test
    @Order(5)
    void login_shouldReturn401_whenWrongPassword() {
        given()
                .body("{\"username\":\"admin\",\"password\":\"wrongpassword\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/login")
                .then().statusCode(401)
                .body("success", is(false));
    }

    @Test
    @Order(6)
    void login_shouldReturn401_whenUserNotFound() {
        given()
                .body("{\"username\":\"nonexistent_12345\",\"password\":\"TestP@ss1\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/login")
                .then().statusCode(401)
                .body("success", is(false));
    }

    @Test
    @Order(7)
    void login_shouldReturn401_whenUserInactive() {
        userRepository.findByUsername("admin").ifPresent(u -> {
            u.setActive(false);
            userRepository.save(u);
        });

        try {
            given()
                    .body("{\"username\":\"admin\",\"password\":\"admin123\"}")
                    .contentType(ContentType.JSON)
                    .when().post("/api/auth/login")
                    .then().statusCode(401)
                    .body("success", is(false));
        } finally {
            userRepository.findByUsername("admin").ifPresent(u -> {
                u.setActive(true);
                userRepository.save(u);
            });
        }
    }

    @Test
    @Order(8)
    void login_shouldReturn400_whenValidationFails() {
        given()
                .body("{\"username\":\"\",\"password\":\"\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/login")
                .then().statusCode(400);
    }

    @Test
    @Order(9)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void logout_shouldReturn200() {
        given()
                .contentType(ContentType.JSON)
                .when().post("/api/auth/logout")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(10)
    void logout_shouldReturn401_whenUnauthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when().post("/api/auth/logout")
                .then().statusCode(401);
    }

    @Test
    @Order(11)
    void me_shouldReturn200() {
        String token = given()
                .body("{\"username\":\"admin\",\"password\":\"admin123\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/auth/login")
                .then().statusCode(200)
                .extract().path("data.token");

        given()
                .auth().oauth2(token)
                .when().get("/api/auth/me")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(12)
    void me_shouldReturn401_whenUnauthenticated() {
        given()
                .when().get("/api/auth/me")
                .then().statusCode(401);
    }
}
