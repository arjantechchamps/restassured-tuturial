package io.techchamps.tutorial.C_HelperClass;

import io.techchamps.tutorial.C_helperclass.Helper;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BasicLoginTest3 {

    @Test
    public void loginWithValidCredentials() {
        given().spec(Helper.createBasicRequestSpecification())
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
        given().spec(Helper.createBasicRequestSpecification())
                .log().all() // log request
                .body("""
                        {
                          "username": "dontExist",
                          "password": "wrong1234"
                        }""")
                .when()
                .post("/auth/signin")
                .then()
                .log().all() // Log response
                .assertThat().statusCode(401)
                .body("error", equalTo("authentication_error"))
                .body("message",equalTo("Invalid username or password"));
    }
}
