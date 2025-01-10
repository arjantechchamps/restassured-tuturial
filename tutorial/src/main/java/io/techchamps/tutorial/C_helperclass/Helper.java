package io.techchamps.tutorial.C_helperclass;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;


public class Helper {

    public static RequestSpecification createBasicRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
