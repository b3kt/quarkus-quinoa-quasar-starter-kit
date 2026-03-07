package com.github.b3kt.plugin;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class PluginResourceTest {

    @Test
    public void testSamplePluginEndpoints() {
        // 1. Create data through sample plugin endpoint
        Map<String, String> request = new HashMap<>();
        request.put("name", "Test Item");
        request.put("description", "Created via plugin");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/ext/sample/data")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data.name", is("Test Item"))
                .body("data.id", notNullValue());

        // 2. Get data through sample plugin endpoint
        given()
                .when().get("/api/ext/sample/data")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("data[0].name", is("Test Item"));
    }

    @Test
    public void testNonExistentPlugin() {
        given()
                .when().get("/api/ext/invalid/data")
                .then()
                .statusCode(404)
                .body("success", is(false))
                .body("error", is("Plugin not found: invalid"));
    }

    @Test
    public void testDisabledPlugin() {
        // 1. Check it's enabled first
        given()
                .when().get("/api/ext/sample/data")
                .then()
                .statusCode(200);

        // 2. Disable it
        given()
                .when().post("/api/ext/sample/disable")
                .then()
                .statusCode(200);

        // 3. Try to access (should fail)
        given()
                .when().get("/api/ext/sample/data")
                .then()
                .statusCode(403)
                .body("success", is(false))
                .body("error", is("Plugin is disabled: sample"));

        // 4. Re-enable it
        given()
                .when().post("/api/ext/sample/enable")
                .then()
                .statusCode(200);

        // 5. Check it's enabled again
        given()
                .when().get("/api/ext/sample/data")
                .then()
                .statusCode(200);
    }
}
