package io.techchamps.tutorial;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class GetAllUsersTest {


    @Test
    //This doesn't work because we need to authenticate first so how do we do that?
    public void getAllUsers() {
        given().spec(Helper.createBasicRequestSpecification())
                .log().all() // log request
                .when().get("/users")
                .then()
                .log().all()
                .assertThat().statusCode(200);
    }

    @Test
    public void getAllUsersWithAuthentication() {

        // first login and save the token from the response
        String token = given().spec(Helper.createBasicRequestSpecification())
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

        given().spec(Helper.createBasicRequestSpecification())
                .header("Authorization", "Bearer " + token)
                .log().all() // log request
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .log().all();
    }

}
