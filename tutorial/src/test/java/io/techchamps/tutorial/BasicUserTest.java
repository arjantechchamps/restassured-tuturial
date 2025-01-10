package io.techchamps.tutorial;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;;

public class BasicUserTest {

    private RequestSpecification requestSpecification;

    @BeforeEach
    public void createBasicRequestSpecification() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .build();
    }

    @Test
    //This doesn't work because we need to authenticate first so how do we do that?
    public void getAllUsers() {
        given().spec(requestSpecification)
                .log().all() // log request
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .log().all();
    }
    @Test
    public void getAllUsersWithAuthentication() {

        String token =  given().spec(requestSpecification)
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
                .extract().response().body().path("token");

        given().spec(requestSpecification)
                .header("Authorization", "Bearer " + token)
                .log().all() // log request
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .log().all();
    }

}
