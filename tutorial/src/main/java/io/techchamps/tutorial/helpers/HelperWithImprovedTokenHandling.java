package io.techchamps.tutorial.helpers;

import generated.dtos.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.techchamps.tutorial.config.PropertieHelper;

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
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return given().spec(spec())
                .body(loginRequest)
                .when()
                .post("/auth/signin")
                .then()
                .assertThat().statusCode(200)
                .extract().response().path("token");
    }

    // Retrieves the authentication token for an admin user
    public static String getAdminToken() {
        TokenInfo tokenInfo = adminToken.get();
        if (tokenInfo == null || tokenInfo.expiry.isBefore(Instant.now())) {
            String token = getToken(PropertieHelper.getProperty("adminUsername"), PropertieHelper.getProperty("adminPassword"));
            adminToken.set(new TokenInfo(token, Instant.now().plusSeconds(14 * 60)));
        }
        return adminToken.get().token;

    }

    // Retrieves the authentication token for a regular user
    public static String getUserToken() {
        TokenInfo tokenInfo = userToken.get();
        if (tokenInfo == null || tokenInfo.expiry.isBefore(Instant.now())) {
            String token = getToken(PropertieHelper.getProperty("userUsername"), PropertieHelper.getProperty("userPassword"));
            userToken.set(new TokenInfo(token, Instant.now().plusSeconds(14 * 60)));
        }
        return userToken.get().token;

    }
}
