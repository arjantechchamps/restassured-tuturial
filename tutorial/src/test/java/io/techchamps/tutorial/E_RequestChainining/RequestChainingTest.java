package io.techchamps.tutorial.E_RequestChainining;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class RequestChainingTest {

    // signup request
    // find user by username
    // delete user by id

    @Test
    public void RequestChainingTest(){

        Response signupResponse = given()
                .spec(createBasicRequestSpecification())
                .body("""
                        {
                          "name": "somebody",
                          "username": "somebody",
                          "email": "somebody@test.nl",
                          "password": "somebody1234"}
                        """)
                .post("/auth/signup")
                .then().assertThat().statusCode(200)
                .extract().response();

        Response findByUserName = given()
                .spec(createAuthRequestSpecification(getAdminToken()))
                .pathParam("username",signupResponse.body().path("username"))
                .get("/users/username/{username}")
                .then().assertThat().statusCode(200)
                .assertThat().body("username",equalTo(signupResponse.body().path("username")))
                .extract().response();

        given()
                .spec(createAuthRequestSpecification(getAdminToken()))
                .when()
                .pathParam("id",findByUserName.body().path("id"))
                .delete("/users/{id}")
                .then().assertThat().statusCode(200);

    }
}
