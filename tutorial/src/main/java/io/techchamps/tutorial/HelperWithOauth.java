package io.techchamps.tutorial;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HelperWithOauth {

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static String getAdminToken(RequestSpecification requestSpecification) {
        return given().spec(requestSpecification)
                .log().all()
                .body("""
                        {
                          "username": "admin",
                          "password": "admin1234"
                        }""")
                .when()
                .post("/auth/signin")
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .extract().response().path("token");
    }

    // RequestSpecification with OAuth2 Authorization header (Bearer token)
    public static RequestSpecification createOAuth2RequestSpecification(String token) {
        return given().spec(createBasicRequestSpecification())
                .header("Authorization", "Bearer " + token)
                .log().all();
    }
}
