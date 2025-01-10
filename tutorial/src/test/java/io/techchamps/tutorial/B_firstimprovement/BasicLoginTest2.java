package io.techchamps.tutorial.B_firstimprovement;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BasicLoginTest2 {

    private RequestSpecification requestSpecification;

    @BeforeEach
    public void setup() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .build();
    }

    @Test
    public void loginWithValidCredentials() {
        given().spec(requestSpecification)
                .log().all() // log request
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
        given().spec(requestSpecification)
                .log().all() // log request
                .body("""
                        {
                          "username": "dontExist",
                          "password": "wrong1234"
                        }""")
                .when()
                .post("/auth/signin")
                .then()
                .log().all()
                .assertThat().statusCode(401)
                .body("error", equalTo("authentication_error"))
                .body("message", equalTo("Invalid username or password"));
    }
}
