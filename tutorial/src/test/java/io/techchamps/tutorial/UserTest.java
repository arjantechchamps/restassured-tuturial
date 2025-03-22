package io.techchamps.tutorial;


import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class UserTest {

    @Test
    public void addUser() {

        given()
                .log().all() // log request
                .header("Content-type", "application/json")
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
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
                .log().all();

        // problem is that this test will only work once
    }

    @Test
    public void AddUserAndDelete() {

    }
}