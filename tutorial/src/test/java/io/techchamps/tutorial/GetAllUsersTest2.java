package io.techchamps.tutorial;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.HelperWithOauth.createOAuth2RequestSpecification;
import static io.techchamps.tutorial.HelperWithOauth.getAdminToken;

public class GetAllUsersTest2 {


    @Test
    public void getAllUsersWithAuthentication() {

        String token = getAdminToken(Helper.createBasicRequestSpecification());

        given()
                .spec(createOAuth2RequestSpecification(token))
                .log().all() // log request
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .log().all();
    }
}

