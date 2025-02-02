package io.techchamps.tutorial.E_usedto;

import Builders.UserRequestBuilder;
import generated.dtos.UserRequest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.createAuthRequestSpecification;

public class CreateUserWithUserBuilderTest {

    @Test
    public void AddUserAndDelete() {

        UserRequest userRequest = new UserRequestBuilder()
                .withUsername("SomeOne")
                .build();

        String token = getAdminToken(createBasicRequestSpecification());
        int userid = given()
                .spec(createAuthRequestSpecification(token))
                .body(userRequest)
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
    }
}
