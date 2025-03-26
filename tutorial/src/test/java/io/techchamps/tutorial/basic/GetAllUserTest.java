package io.techchamps.tutorial.basic;

import io.techchamps.tutorial.helpers.Helper;
import io.techchamps.tutorial.helpers.HelperWithAuth;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class GetAllUserTest {

    @Test
    //This doesn't work because we need to authenticate first so how do we do that?
    public void getAllUsers() {
        given()
                .log().all() // log request
                .header("Content-type", "application/json")
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .log().all() // log request
                .when().get("/users")
                .then()
                .log().all();
//                .assertThat().statusCode(200);
    }

    @Test
    public void getAllUsersWithToken() {

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
             .extract().body().path("token");

        given().spec(Helper.spec())
                .auth().oauth2(token)
                .when().get("/users")
                .then()
                .log().all()
                .assertThat().statusCode(200);
    }

    @Test
    public void GetAllUserWithOath(){
        given().spec(HelperWithAuth.specWithAdminOauth())
                .when().get("/users")
                .then()
                .log().all()
                .assertThat().statusCode(200);
    }
}
