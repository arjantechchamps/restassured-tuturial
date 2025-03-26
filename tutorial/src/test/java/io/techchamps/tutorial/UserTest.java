package io.techchamps.tutorial;


import generated.dtos.UserRequest;
import generated.dtos.UserResponse;
import io.restassured.response.Response;
import io.techchamps.tutorial.Builders.UserRequestBuilder;
import io.techchamps.tutorial.helpers.HelperWithAuth;
import io.techchamps.tutorial.helpers.HelperWithDTO;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserTest {

    @Test
    public void AddUser() {

     Response response   = given().spec(HelperWithDTO.specWithAdminOauth())
                .body("""
                           {
                             "name": "John",
                             "username": "John",
                             "email": "John@test.nl",
                             "password": "test1234",
                             "roles": [
                            {
                              "name": "USER"
                            }
                          ],
                          "profile": {
                           "addresses": [
                             {
                               "street": "Keizersgracht",
                               "houseNumber": "123",
                               "zipcode": "1015CJ",
                               "city": "Amsterdam",
                               "country": "Netherlands"
                             }
                           ],
                           "interests": [
                             "SPORTS"
                           ]
                         }
                        }""")
                .log().all() // log request
                .when().post("/users")
                .then()
                .assertThat().statusCode(201)
                .assertThat().body("id", is(notNullValue()))
                .log().all()
             .extract().response();
        //Delete the user
        given().spec(HelperWithAuth.specWithAdminOauth())
                .when()
                //use the id from the getByUserNameResponse as a pathParameter
                .pathParam("id", response.body().path("id"))
                .delete("/users/{id}")
                .then().statusCode(200);
    }

    @Test
    public void AddUserWithBuilder() {
        //Use userRequestBuilder with default data
        UserRequest userRequest = new UserRequestBuilder()
                .build();
        //Extract the response as UserResponse
        UserResponse userResponse   = given().spec(HelperWithAuth.specWithAdminOauth())
                .body(userRequest)
                .log().all() // log request
                .when().post("/users")
                .then()
                .assertThat().statusCode(201)
                .extract().response().as(UserResponse.class);
        //Assert that the userResponse contains same values as the userRequest
        assertThat(userResponse.getName(),equalTo(userRequest.getName()));
        assertThat(userResponse.getProfile().getAddresses(),equalTo(userRequest.getProfile().getAddresses()));

        //Delete the user based on the id retrieved from the userResponse
        given().spec(HelperWithAuth.specWithAdminOauth())
                .when()
                //use the id from the getByUserNameResponse as a pathParameter
                .pathParam("id", userResponse.getId())
                .delete("/users/{id}")
                .then().statusCode(200);
    }
}