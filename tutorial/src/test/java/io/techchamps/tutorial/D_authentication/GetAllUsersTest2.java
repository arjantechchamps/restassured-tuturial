package io.techchamps.tutorial.D_authentication;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class GetAllUsersTest2 {


    @Test
    public void getAllUsersWithAuthentication() {


        given()
                .spec(HelperWithAuth.specWithAdminToken())
                .log().all() // log request
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .log().all();
    }
}

