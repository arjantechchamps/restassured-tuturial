package io.techchamps.tutorial.E_DTO;


import dto.JwtResponse;
import dto.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HelperWithDTO {

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
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
                .header("Authorization", "Bearer " + token);
    }

    public static String getAdminToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,"admin","admin1234");
    }

    public static String getUserToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,"user","user1234");
    }

}
