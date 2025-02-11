package io.techchamps.tutorial.F_PropertieFile;

import config.ConfigProperties;
import dto.JwtResponse;
import dto.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HelperWithPropertieFile {

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigProperties.getProperty("baseUri"))
                .setPort(ConfigProperties.getIntProperty("port"))
                .setBasePath(ConfigProperties.getProperty("basePath"))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static String getToken(RequestSpecification requestSpecification,String username, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        return given().spec(requestSpecification)
                .body(loginRequest)
                .when()
                .post("/auth/signin")
                .then()
                .assertThat().statusCode(200)
                .extract().response().as(JwtResponse.class).getToken();
    }

    public static RequestSpecification createAuthRequestSpecification(String token) {
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(token);
    }

    public static String getAdminToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,ConfigProperties.getProperty("adminUsername"),ConfigProperties.getProperty("adminPassword"));
    }

    public static String getUserToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,ConfigProperties.getProperty("userUsername"),ConfigProperties.getProperty("userPassword"));
    }

}
