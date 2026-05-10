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
class AdminResourceTest {

    @Inject
    UserRepository userRepository;

    @Test
    @Order(1)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void listUsers_shouldReturn200() {
        given()
                .when().get("/api/admin/users")
                .then().statusCode(200)
                .body("success", is(true))
                .body("data", notNullValue());
    }

    @Test
    @Order(2)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void listUsers_shouldContainAdmin() {
        given()
                .when().get("/api/admin/users")
                .then().statusCode(200)
                .body("data.find { it.username == 'admin' }", notNullValue());
    }

    @Test
    @Order(3)
    void listUsers_shouldReturn401_whenUnauthenticated() {
        given()
                .when().get("/api/admin/users")
                .then().statusCode(401);
    }

    @Test
    @Order(4)
    @TestSecurity(user = "regular", roles = {"user"})
    void listUsers_shouldReturn403_whenNotAdmin() {
        given()
                .when().get("/api/admin/users")
                .then().statusCode(403);
    }

    @Test
    @Order(5)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void getUser_shouldReturn200() {
        given()
                .when().get("/api/admin/users/admin")
                .then().statusCode(200)
                .body("success", is(true))
                .body("data.username", is("admin"));
    }

    @Test
    @Order(6)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void getUser_shouldReturn404_whenNotFound() {
        given()
                .when().get("/api/admin/users/nonexistent_user_xyz")
                .then().statusCode(404)
                .body("success", is(false));
    }

    @Test
    @Order(7)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void updateRoles_shouldReturn200() {
        given()
                .body("{\"roles\":[\"user\",\"admin\"]}")
                .contentType(ContentType.JSON)
                .when().put("/api/admin/users/admin/roles")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(8)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void updateRoles_shouldReturn404_whenNotFound() {
        given()
                .body("{\"roles\":[\"user\"]}")
                .contentType(ContentType.JSON)
                .when().put("/api/admin/users/nonexistent_user_xyz/roles")
                .then().statusCode(404);
    }

    @Test
    @Order(9)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void updateRoles_shouldReturn400_whenEmpty() {
        given()
                .body("{\"roles\":[]}")
                .contentType(ContentType.JSON)
                .when().put("/api/admin/users/admin/roles")
                .then().statusCode(400);
    }

    @Test
    @Order(10)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void activateUser_shouldReturn200() {
        given()
                .when().put("/api/admin/users/admin/activate")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(11)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void activateUser_shouldReturn404_whenNotFound() {
        given()
                .when().put("/api/admin/users/nonexistent_user_xyz/activate")
                .then().statusCode(404);
    }

    @Test
    @Order(12)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void deactivateUser_shouldReturn200() {
        given()
                .when().put("/api/admin/users/admin/deactivate")
                .then().statusCode(200)
                .body("success", is(true));

        userRepository.findByUsername("admin").ifPresent(u -> {
            u.setActive(true);
            userRepository.save(u);
        });
    }

    @Test
    @Order(13)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void deactivateUser_shouldReturn404_whenNotFound() {
        given()
                .when().put("/api/admin/users/nonexistent_user_xyz/deactivate")
                .then().statusCode(404);
    }

    @Test
    @Order(14)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void auditLogs_shouldReturn200() {
        given()
                .when().get("/api/admin/audit-logs")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(15)
    void auditLogs_shouldReturn401_whenUnauthenticated() {
        given()
                .when().get("/api/admin/audit-logs")
                .then().statusCode(401);
    }

    @Test
    @Order(16)
    @TestSecurity(user = "regular", roles = {"user"})
    void auditLogs_shouldReturn403_whenNotAdmin() {
        given()
                .when().get("/api/admin/audit-logs")
                .then().statusCode(403);
    }

    @Test
    @Order(17)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void auditLogsByEntity_shouldReturn200() {
        given()
                .when().get("/api/admin/audit-logs/entity/UserEntity")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(18)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void auditLogsByAction_shouldReturn200() {
        given()
                .when().get("/api/admin/audit-logs/action/UPDATE")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(19)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void auditLogsByAction_shouldReturn200_forCreate() {
        given()
                .when().get("/api/admin/audit-logs/action/CREATE")
                .then().statusCode(200)
                .body("success", is(true));
    }

    @Test
    @Order(20)
    @TestSecurity(user = "admin", roles = {"user", "admin"})
    void auditLogsByAction_shouldReturn200_forDelete() {
        given()
                .when().get("/api/admin/audit-logs/action/DELETE")
                .then().statusCode(200)
                .body("success", is(true));
    }
}
