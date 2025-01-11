package io.techchamps.tutorial.E_usedto;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.createBasicRequestSpecification;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.getAdminToken;
import static io.techchamps.tutorial.E_DTO.HelperWithDTO.createAuthRequestSpecification;

public class CreateUserTest {

    @Test
    public void addUser() {

        String token = getAdminToken(createBasicRequestSpecification());
        given()
                .spec(createAuthRequestSpecification(token))
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
                                 "street": "string",
                                 "houseNumber": "string",
                                 "zipcode": "string",
                                 "city": "string",
                                 "country": "string"
                               }
                             ],
                             "phoneNumbers": [
                               {
                                 "number": "string",
                                 "type": "PRIVATE"
                               }
                             ],
                             "interests": [
                               "SPORTS"
                             ]
                           }
                         }""")
                .log().all() // log request
                .when().post("/users")
                .then().assertThat().statusCode(201)
                .log().all();
    }
}
