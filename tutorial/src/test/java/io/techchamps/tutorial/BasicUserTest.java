package io.techchamps.tutorial;


import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BasicUserTest {

    @Test
    public void createUser() {
        given()
                .log().all() // log request
                .header("Content-type", "application/json")
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .body("""
                        {
                          "name": "testuser",
                          "username": "testuser",
                          "email": "testuser@test.nl",
                          "password": "test1234",
                          "roles": [
                            "USER"
                          ]
                        }""")
                .when()
                .post("auth/signup")
                .then()
                .log().all()
                .assertThat().statusCode(200);
    }

    @Test
    public void createUserAdvanced() {

        String token =
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
                        .extract().path("token");

        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .queryParam("email", "testuser@test.nl")
                .when()
                .get("users/email")
                .then().log().all().extract().response();


    }

}
