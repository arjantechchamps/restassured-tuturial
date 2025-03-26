package io.techchamps.tutorial.basic;


import generated.dtos.SignUpRequest;
import generated.dtos.SignupResponse;
import generated.dtos.UserResponse;
import io.restassured.response.Response;
import io.techchamps.tutorial.helpers.HelperWithAuth;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class RequestChainingTest {

    // do a signup call
    // do a get user by username
    // delete the user by id

    @Test
    public void RequestChainTest() {
        //extract the entire response
        Response signupResponse = given()
                .spec(HelperWithAuth.spec())
                .when()
                .body("""
                        {
                          "name": "somebody",
                          "username": "somebody",
                          "email": "somebody@test.nl",
                          "password": "somebody1234"
                        }
                        """)
                .post("/auth/signup")
                .then()
                .assertThat().statusCode(200)
                .extract().response();

        //extract the entire response
        Response getByUserNameResponse = given()
                .spec(HelperWithAuth.specWithAdminOauth())
                //use the username from the signupresponse as a pathParameter
                .pathParam("username", signupResponse.body().path("username"))
                .get("/users/username/{username}")
                .then()
                .assertThat().statusCode(200)
                .extract().response();

        given().spec(HelperWithAuth.specWithAdminOauth())
                .when()
                //use the id from the getByUserNameResponse as a pathParameter
                .pathParam("id", getByUserNameResponse.body().path("id"))
                .delete("/users/{id}")
                .then().statusCode(200);

        given()
                .spec(HelperWithAuth.specWithAdminOauth())
                //use the username from the signupresponse again as a pathParameter
                .pathParam("username", signupResponse.body().path("username"))
                .get("/users/username/{username}")
                .then()
                .assertThat().statusCode(404);
    }

    @Test
    public void requestChainTestWithDTO() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("somebody");
        signUpRequest.setName("somebody");
        signUpRequest.setEmail("somebody@test.nl");
        signUpRequest.setPassword("somebody1234");
        //extract the entire response
        SignupResponse signupResponse = given()
                .spec(HelperWithAuth.spec())
                .when()
                .body(signUpRequest)
                .post("/auth/signup")
                .then()
                .assertThat().statusCode(200)
                .extract().as(SignupResponse.class);

        //extract the entire response
        UserResponse getByUserNameResponse = given()
                .spec(HelperWithAuth.specWithAdminOauth())
                //use the username from the signupresponse as a pathParameter
                .pathParam("username", signupResponse.getUsername())
                .get("/users/username/{username}")
                .then()
                .assertThat().statusCode(200)
                .extract().as(UserResponse.class);

        given().spec(HelperWithAuth.specWithAdminOauth())
                .when()
                //use the id from the getByUserNameResponse as a pathParameter
                .pathParam("id", getByUserNameResponse.getId())
                .delete("/users/{id}")
                .then().statusCode(200);

        given()
                .spec(HelperWithAuth.specWithAdminOauth())
                //use the username from the signupresponse again as a pathParameter
                .pathParam("username", signupResponse.getUsername())
                .get("/users/username/{username}")
                .then()
                .assertThat().statusCode(404);
    }
}