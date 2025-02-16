package io.techchamps.tutorial.F_usedto;

import generated.dtos.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.createAuthRequestSpecification;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserWithDtoTest {

    @Test
    public void AddUserAndDelete() {

        // Request chaining.
        UserResponse response =
                given()
                        .spec(createAuthRequestSpecification(getAdminToken()))
                        .body(defaultUserRequest())
                        .log().all() // log request
                        .when().post("/users")
                        .then().assertThat().statusCode(201)
                        .log().all()
                        .extract().response().as(UserResponse.class);

        assertThat(response.getEmail(),equalTo(defaultUserRequest().getEmail()));
        assertThat(response.getName(),equalTo(defaultUserRequest().getName()));

        given()
                .spec(createAuthRequestSpecification(getAdminToken()))
                .log().all()
                .when()
                .pathParam("id", response.getId())
                .delete("/users/{id}")
                .then()
                .log().all()
                .statusCode(200);
    }

    private UserRequest defaultUserRequest (){
      UserRequest userRequest = new UserRequest();
      userRequest.setName("John");
      userRequest.setUsername("John");
      userRequest.setEmail("John@test.nl");
      userRequest.setPassword("test1234");
      userRequest.addRolesItem(defaultRole());
      userRequest.setProfile(defaultProfile());
      return userRequest;
    }

    private RoleRequest defaultRole(){
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("USER");
        return roleRequest;
    }

    private ProfileRequest defaultProfile(){
        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.SPORTS);
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.FOOD);
        profileRequest.addAddressesItem(defaultAdress());

        return  profileRequest;
    }

    private AddressRequest defaultAdress(){
        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setZipcode("1111AA");
        addressRequest.setCity("Enschede");
        addressRequest.setStreet("Some Street");
        addressRequest.setHouseNumber("5");
        addressRequest.setCountry("Netherlands");
        return addressRequest;
    }
}
