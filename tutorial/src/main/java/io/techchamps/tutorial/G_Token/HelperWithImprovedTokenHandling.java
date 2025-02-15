package io.techchamps.tutorial.G_Token;


import config.ConfigProperties;
import dto.JwtResponse;
import dto.LoginRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;

public class HelperWithImprovedTokenHandling {
    private static final Map<String, TokenInfo> tokenCache = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRY_SECONDS = 15 * 60; // 15 minutes

    private static class TokenInfo {
        private final String token;
        private final Instant expiryTime;

        public TokenInfo(String token, Instant expiryTime) {
            this.token = token;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }

        public String getToken() {
            return token;
        }
    }

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigProperties.getProperty("baseUri"))
                .setPort(ConfigProperties.getIntProperty("port"))
                .setBasePath(ConfigProperties.getProperty("basePath"))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    private static String getToken(RequestSpecification requestSpecification, String username, String password, String roleKey) {
        TokenInfo tokenInfo = tokenCache.get(roleKey);

        if (tokenInfo != null && !tokenInfo.isExpired()) {
            return tokenInfo.getToken();
        }

        // Request new token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String token = given().spec(requestSpecification)
                .body(loginRequest)
                .when()
                .post("/auth/signin")
                .then()
                .assertThat().statusCode(200)
                .extract().response().as(JwtResponse.class).getToken();

        // Store the token with its expiration time
        tokenCache.put(roleKey, new TokenInfo(token, Instant.now().plusSeconds(TOKEN_EXPIRY_SECONDS)));

        return token;
    }

    public static RequestSpecification createAuthRequestSpecification(String token) {
        return given().spec(createBasicRequestSpecification())
                .auth().oauth2(token);
    }

    public static String getAdminToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,
                ConfigProperties.getProperty("adminUsername"),
                ConfigProperties.getProperty("adminPassword"),
                "admin");
    }

    public static String getUserToken(RequestSpecification requestSpecification) {
        return getToken(requestSpecification,
                ConfigProperties.getProperty("userUsername"),
                ConfigProperties.getProperty("userPassword"),
                "user");
    }
}
