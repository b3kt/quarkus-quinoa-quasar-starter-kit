package com.github.b3kt.presentation.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

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
}
