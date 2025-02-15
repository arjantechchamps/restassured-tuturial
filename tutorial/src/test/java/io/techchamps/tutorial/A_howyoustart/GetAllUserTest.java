package io.techchamps.tutorial.A_howyoustart;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class GetAllUserTest {

        @Test
    //This doesn't work because we need to authenticate first so how do we do that?
    public void getAllUsers() {
        given()
                .log().all() // log request
                .header("Content-type", "application/json")
                .baseUri("http://localhost")
                .port(8085)
                .basePath("/api")
                .log().all() // log request
                .when().get("/users")
                .then()
                .log().all()
                .assertThat().statusCode(200);
    }
}
