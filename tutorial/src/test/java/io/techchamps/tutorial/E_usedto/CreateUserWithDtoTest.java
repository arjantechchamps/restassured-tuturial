package io.techchamps.tutorial.E_usedto;

import generated.dtos.AddressRequest;
import generated.dtos.ProfileRequest;
import generated.dtos.RoleRequest;
import generated.dtos.UserRequest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.createAuthRequestSpecification;

public class CreateUserWithDtoTest {

    @Test
    public void AddUserAndDelete() {

        UserRequest userRequest = new UserRequest();
        ProfileRequest profileRequest = new ProfileRequest();
        AddressRequest addressRequest = new AddressRequest();
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("USER");
        addressRequest.setCity("Enschede");
        addressRequest.setCountry("Netherlands");
        addressRequest.setStreet("TestStreet");
        addressRequest.setHouseNumber("5");
        addressRequest.setZipcode("1111AA");
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.SPORTS);
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.TRAVEL);
        profileRequest.addAddressesItem(addressRequest);


        // Request chaining.
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
