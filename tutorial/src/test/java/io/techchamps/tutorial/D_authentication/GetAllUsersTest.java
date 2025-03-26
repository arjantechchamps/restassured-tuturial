package io.techchamps.tutorial.D_authentication;

import io.techchamps.tutorial.helpers.Helper;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class GetAllUsersTest {


    @Test
    public void getAllUsersWithAuthentication() {

        // first login and save the token from the response
        String token = given().spec(Helper.spec())
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

        given().spec(Helper.spec())
                .header("Authorization", "Bearer " + token)
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .log().all();
    }
}
