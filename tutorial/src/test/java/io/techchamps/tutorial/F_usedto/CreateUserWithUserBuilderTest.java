package io.techchamps.tutorial.F_usedto;

import generated.dtos.ProfileRequest;
import generated.dtos.UserRequest;
import generated.dtos.UserResponse;
import io.techchamps.tutorial.Builders.AdressRequestBuilder;
import io.techchamps.tutorial.Builders.ProfileRequestBuilder;
import io.techchamps.tutorial.Builders.UserRequestBuilder;
import io.techchamps.tutorial.helpers.HelperWithDTO;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserWithUserBuilderTest {

    @Test
    public void AddUserAndDelete() {

        UserRequest userRequest = new UserRequestBuilder()
                // Use with to override the defaults
                .withUsername("SomeName")
                .withAddress(new AdressRequestBuilder().withCity("TestCity"))
                .withProfile(new ProfileRequestBuilder().withInterest(ProfileRequest.InterestsEnum.FOOD))
                .build();

        UserResponse userResponse = given()
                .spec(HelperWithDTO.specWithAdminToken())
                .body(userRequest)
                .log().all() // log request
                .when().post("/users")
                .then().assertThat().statusCode(201)
                .log().all()
                .extract().as(UserResponse.class);

        assertThat(userResponse.getName(), equalTo(userRequest.getName()));

        given()
                .spec(HelperWithDTO.specWithAdminToken())
                .log().all()
                .when()
                .pathParam("id", userResponse.getId())
                .delete("/users/{id}")
                .then()
                .log().all()
                .statusCode(200);
    }
}
