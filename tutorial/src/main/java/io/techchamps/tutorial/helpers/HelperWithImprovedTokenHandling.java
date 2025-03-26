package io.techchamps.tutorial.helpers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.techchamps.tutorial.config.PropertieHelper;
import io.techchamps.tutorial.dto.JwtResponse;
import io.techchamps.tutorial.dto.LoginRequest;

import java.time.Instant;

import static io.restassured.RestAssured.given;

public class HelperWithImprovedTokenHandling {
    private static final ThreadLocal<TokenInfo> adminToken = new ThreadLocal<>();
    private static final ThreadLocal<TokenInfo> userToken = new ThreadLocal<>();

    // Struct to store token and expiration time
    private static class TokenInfo {
        String token;
        Instant expiry;

        TokenInfo(String token, Instant expiry) {
            this.token = token;
            this.expiry = expiry;
        }
    }

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(PropertieHelper.getProperty("baseUri"))
                .setPort(PropertieHelper.getIntProperty("port"))
                .setBasePath(PropertieHelper.getProperty("basePath"))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static RequestSpecification specWithAdminToken() {
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(getAdminToken());
    }

    public static RequestSpecification specWithUserToken() {
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(getAdminToken());
    }

    public static RequestSpecification specwithToken(String token) {
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
        TokenInfo tokenInfo = adminToken.get();
        if (tokenInfo == null || tokenInfo.expiry.isBefore(Instant.now())) {
            String token = getToken(PropertieHelper.getProperty("adminUsername"), PropertieHelper.getProperty("adminPassword"));
            adminToken.set(new TokenInfo(token, Instant.now().plusSeconds(14 * 60)));
        }
        return adminToken.get().token;

    }

    public static String getUserToken() {
        TokenInfo tokenInfo = userToken.get();
        if (tokenInfo == null || tokenInfo.expiry.isBefore(Instant.now())) {
            String token = getToken(PropertieHelper.getProperty("userUsername"), PropertieHelper.getProperty("userPassword"));
            userToken.set(new TokenInfo(token, Instant.now().plusSeconds(14 * 60)));
        }
        return userToken.get().token;

    }
}
