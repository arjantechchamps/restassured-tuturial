package io.techchamps.tutorial.F_DTO;


import dto.JwtResponse;
import dto.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HelperWithDTO {

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .log(LogDetail.ALL)
                .build();
    }
    public static RequestSpecification specWithAdminToken(){
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(getAdminToken());
    }

    public static RequestSpecification specWithUserToken(){
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
        return getToken("admin","admin1234");
    }

    public static String getUserToken() {
        return getToken("user","user1234");
    }

}
