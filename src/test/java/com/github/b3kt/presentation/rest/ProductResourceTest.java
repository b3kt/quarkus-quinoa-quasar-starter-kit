package com.github.b3kt.presentation.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ProductResourceTest {

    @Test
    public void testUnauthorizedAccess() {
        given()
                .when().get("/api/products")
                .then()
                .statusCode(401);
    }

    @Test
    public void testAdminAccess() {
        // First login as admin to get token
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin123");

        String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().path("data.token");

        // Use token to access products (Admin has READ permission)
        given()
                .header("Authorization", "Bearer " + token)
                .when().get("/api/products")
                .then()
                .statusCode(200)
                .body("data.size()", is(2));

        // Admin has CREATE permission
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("New Product")
                .when().post("/api/products")
                .then()
                .statusCode(200);
    }
}
