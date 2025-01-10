package io.techchamps.tutorial.D_authentication;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HelperWithAuth {

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static String getToken(RequestSpecification requestSpecification,String username, String password) {
        return given().spec(requestSpecification)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }""", username, password))
                .when()
                .post("/auth/signin")
                .then()
                .assertThat().statusCode(200)
                .extract().response().path("token");
    }

    public static RequestSpecification createAuthRequestSpecification(String token) {
        return given().spec(createBasicRequestSpecification())
                .header("Authorization", "Bearer " + token);
    }

    public static String getAdminToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,"admin","admin1234");
    }

    public static String getUserToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,"user","user1234");
    }


}
