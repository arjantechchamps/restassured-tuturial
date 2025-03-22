package io.techchamps.tutorial.D_authentication;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HelperWithAuth {

    // Creates and returns a basic request specification without authentication
    public static RequestSpecification spec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .build();
    }

    // Returns a request specification with OAuth2 authentication using a provided token
    public static RequestSpecification specWithOauth(String token) {
        return given().spec(spec())
                .auth().oauth2(token);
    }

    // Returns a request specification with OAuth2 authentication for an admin user
    public static RequestSpecification specWithAdminOauth() {
        return given().spec(spec())
                .auth().oauth2(getAdminToken());
    }

    // Returns a request specification with OAuth2 authentication for a regular user
    public static RequestSpecification specWithUserOauth() {
        return given().spec(spec())
                .auth().oauth2(getUserToken());
    }

    // Sends a POST request to authenticate a user and retrieves the authentication token
    public static String getToken(String username, String password) {
        return given().spec(spec())
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

    // Retrieves the authentication token for an admin user
    public static String getAdminToken() {
        return getToken("admin", "admin1234");
    }

    // Retrieves the authentication token for a regular user
    public static String getUserToken() {
        return getToken("user", "user1234");
    }

}
