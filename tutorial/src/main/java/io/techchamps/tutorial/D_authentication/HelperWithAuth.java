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

    public static String getToken(String username, String password) {
        return given().spec(createBasicRequestSpecification())
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

    public static RequestSpecification specWithAdminToken(){
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(getAdminToken());
    }

    public static RequestSpecification specWithUserToken(){
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(getAdminToken());
    }

    public static RequestSpecification createAuthRequestSpecification(String token) {
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(token);
    }

    public static String getAdminToken() {
        return getToken("admin","admin1234");
    }

    public static String getUserToken() {
        return getToken("user","user1234");
    }

}
