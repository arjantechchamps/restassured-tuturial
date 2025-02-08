package io.techchamps.tutorial.D_authentication;

import io.techchamps.tutorial.C_helperclass.Helper;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;

public class GetAllUsersTest2 {


    @Test
    public void getAllUsersWithAuthentication() {


        given()
                .spec(createAuthRequestSpecification(getAdminToken()))
                .log().all() // log request
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .log().all();
    }
}

