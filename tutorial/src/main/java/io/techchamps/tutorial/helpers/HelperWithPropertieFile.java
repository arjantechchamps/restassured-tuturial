package io.techchamps.tutorial.helpers;


import generated.dtos.JwtResponse;
import generated.dtos.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.techchamps.tutorial.config.PropertieHelper;

import static io.restassured.RestAssured.given;

public class HelperWithPropertieFile {

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(PropertieHelper.getProperty("baseUri"))
                .setPort(PropertieHelper.getIntProperty("port"))
                .setBasePath(PropertieHelper.getProperty("basePath"))
                .addHeader("Content-Type", "application/json")
                .log(LogDetail.ALL)
                .build();
    }
    public static RequestSpecification specWithAdminOauth(){
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(getAdminToken());
    }

    public static RequestSpecification specWithUserOauth(){
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(getAdminToken());
    }
    public static RequestSpecification specwithToken(String token){
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(token);
    }

    public static String getToken(String username, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        return given().spec(createBasicRequestSpecification())
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

    public static String getAdminToken() {
        return getToken(PropertieHelper.getProperty("adminUsername"), PropertieHelper.getProperty("adminPassword"));
    }

    public static String getUserToken() {
        return getToken(PropertieHelper.getProperty("userUsername"),PropertieHelper.getProperty("userPassword"));
    }

}
