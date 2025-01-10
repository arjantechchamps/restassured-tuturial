package io.techchamps.tutorial;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BasicLoginTest {

    @Test
    public void loginWithValidCredentials() {

        given()
                .log().all() // log request
                .header("Content-type", "application/json")
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .body("""
                        {
                          "username": "admin",
                          "password": "admin1234"
                        }""")
                .when()
                .post("/auth/signin")
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .body("token", notNullValue())
                .body("type", equalTo("Bearer"));

    }

    @Test
    public void loginWithInValidCredentials() {

        given()
                .log().all() // log request
                .header("Content-type", "application/json")
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .body("""
                        {
                          "username": "dontExist",
                          "password": "wrong1234"
                        }""")
                .when()
                .post("/auth/signin")
                .then()
                .log().all()
                .assertThat().statusCode(404)
                .body("error", equalTo("You do not have permission to access this resource."));
    }
}
