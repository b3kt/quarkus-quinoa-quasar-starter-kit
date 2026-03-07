package com.github.b3kt.presentation.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusTest
public class AuthResourceTest {

    @Test
    public void testLoginAndGetCurrentUser() {
        // 1. Login to get token
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin123");

        String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("data.token", notNullValue())
                .extract().path("data.token");

        // 2. Get current user with token
        given()
                .header("Authorization", "Bearer " + token)
                .when().get("/api/auth/me")
                .then()
                .statusCode(200)
                .body("data.username", is("admin"));
    }

    @Test
    public void testRegisterAndChangePassword() {
        // 1. Register a new user
        String uniqueUser = "testuser_" + System.currentTimeMillis();
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", uniqueUser);
        registerRequest.put("email", uniqueUser + "@example.com");
        registerRequest.put("password", "testpassword");

        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when().post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("data.username", is(uniqueUser))
                .body("data.email", is(uniqueUser + "@example.com"));

        // 2. Login with new user
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", uniqueUser);
        loginRequest.put("password", "testpassword");

        String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("data.token", notNullValue())
                .extract().path("data.token");

        // 3. Change password
        Map<String, String> changePasswordRequest = new HashMap<>();
        changePasswordRequest.put("oldPassword", "testpassword");
        changePasswordRequest.put("newPassword", "newpassword123");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(changePasswordRequest)
                .when().post("/api/auth/change-password")
                .then()
                .statusCode(200)
                .body("message", is("Password changed successfully"));

        // 4. Try to login with old password (should fail)
        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/api/auth/login")
                .then()
                .statusCode(401);

        // 5. Login with new password (should succeed)
        loginRequest.put("password", "newpassword123");
        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("data.token", notNullValue());
    }
}
