package io.techchamps.tutorial;

import generated.dtos.ErrorResponse;
import generated.dtos.LoginRequest;
import generated.dtos.JwtResponse;
import io.techchamps.tutorial.helpers.Helper;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BasicLoginTest {

    @Test
    public void loginWithValidCredentials() {
        // Given when then syntax
        given()
                //log the request
                .log().all() // log request
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .header("Content-type", "application/json")
                .body("""
                        {
                          "username": "admin",
                          "password": "admin1234"
                        }""")
                .when()
                .post("/auth/signin")
                .then()
                //log the response
                .log().all()
                .assertThat().statusCode(200)
                .body("token", notNullValue())
                .body("type", equalTo("Bearer"))
                .body("roles", hasItems("ROLE_USER", "ROLE_ADMIN"));

    }

    @Test
    public void loginWithInValidCredentials() {

        given()
                .log().all() // log request
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .header("Content-type", "application/json")
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

    @Test
    public void improvedloginWithValidCredentials() {
        //Use the Helper class
        given().spec(Helper.spec())
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
                .body("type", equalTo("Bearer"))
                .body("roles", hasItems("ROLE_USER", "ROLE_ADMIN"));
    }

    @Test
    public void improvedloginWithInValidCredentials() {
        //Use the Helper class
        given().spec(Helper.spec())
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

    @Test
    public void useDtoWithValidCredentials() {
        //Use dto to create the login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin1234");

        // extract the response as JwtResponse
       JwtResponse jwtResponse = given().spec(Helper.spec())
                .body(loginRequest)
                .when()
                .post("/auth/signin")
                .then()
                .log().all()
                .assertThat().statusCode(200)
               .extract().response().as(JwtResponse.class);
       // Assert that the jwtResponse contains expected values
       assertThat(jwtResponse.getToken(),notNullValue());
       assertThat(jwtResponse.getType(),equalTo("Bearer"));
       assertThat(jwtResponse.getRoles(),hasItems("ROLE_USER","ROLE_ADMIN"));
    }

    @Test
    public void useDtoWithInValidCredentials() {

        //Use dto to create the login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("dontExist");
        loginRequest.setPassword("wrong1234");

        //extract the response as ErrorResponse class
        ErrorResponse errorResponse =
        given().spec(Helper.spec())
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
                .extract().response().as(ErrorResponse.class);

        assertThat(errorResponse.getError(),equalTo("authentication_error"));
        assertThat(errorResponse.getMessage(),equalTo("Invalid username or password"));
    }
}