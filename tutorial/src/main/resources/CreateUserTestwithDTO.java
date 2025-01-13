package io.techchamps.tutorial.E_usedto;

import org.junit.jupiter.api.Test;
import tutorial.dtos.UserRequest;


import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.createAuthRequestSpecification;

public class CreateUserTestwithDTO {


    @Test
    public void AddUserAndDelete() {


        String token = getAdminToken(createBasicRequestSpecification());
        int userid = given()
                .spec(createAuthRequestSpecification(token))
                .body(createUserRequest())
                .log().all() // log request
                .when().post("/users")
                .then().assertThat().statusCode(201)
                .log().all()
                .extract().response().path("id");

        given()
                .spec(createAuthRequestSpecification(token))
                .log().all()
                .when()
                .pathParam("id", userid)
                .delete("/users/{id}")
                .then()
                .log().all()
                .statusCode(200);

        // This is not really readable and maintainable so lets use DTO's


    }
    public UserRequest creatUserRequest(){

        UserRequest userRequest = new UserRequest();

        return  userRequest;
    }
}
