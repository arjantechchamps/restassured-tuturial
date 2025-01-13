package io.techchamps.tutorial.E_usedto;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;


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
                               "street": "Keizersgracht",
                               "houseNumber": "123",
                               "zipcode": "1015CJ",
                               "city": "Amsterdam",
                               "country": "Netherlands"
                             }
                           ],
                           "phoneNumbers": [
                             {
                               "number": "+31 6 12345678",
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

        // problem is that this test will only work once
    }

    @Test
    public void AddUserAndDelete() {
        String token = getAdminToken(createBasicRequestSpecification());
        int userid = given()
                .spec(createAuthRequestSpecification(token))
                .body("""
                           {
                             "name": "Joe",
                             "username": "Joe",
                             "email": "Joe@test.nl",
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
                             },
                                                     {
                               "street": "Willemskade",
                               "houseNumber": "2",
                               "zipcode": "1015CJ",
                               "city": "Amsterdam",
                               "country": "Netherlands"
                             }
                           ],
                           "phoneNumbers": [
                             {
                               "number": "+31 6 12345678",
                               "type": "PRIVATE"
                             },
                                                          {
                               "number": "+31 6 12345679",
                               "type": "BUSINESS"
                             }
                           ],
                           "interests": [
                             "SPORTS",
                             "TRAVEL"
                           ]
                         }
                        }""")
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
}
