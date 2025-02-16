package io.techchamps.tutorial.F_usedto;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.techchamps.tutorial.D_authentication.HelperWithAuth.*;
import static org.hamcrest.Matchers.*;



public class CreateUserTest {

    @Test
    public void addUser() {

        given()
                .spec(createAuthRequestSpecification(getAdminToken()))
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
                .assertThat().body("id",is(notNullValue()))
                .log().all();

        // problem is that this test will only work once
    }

    @Test
    public void AddUserAndDelete() {

        // Request chaining.

        int userid = given()
                .spec(createAuthRequestSpecification(getAdminToken()))
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
                .spec(createAuthRequestSpecification(getAdminToken()))
                .log().all()
                .when()
                .pathParam("id", userid)
                .delete("/users/{id}")
                .then()
                .log().all()
                .statusCode(200);

        // This is not really readable and maintainable so lets use DTO's instead
    }
}
