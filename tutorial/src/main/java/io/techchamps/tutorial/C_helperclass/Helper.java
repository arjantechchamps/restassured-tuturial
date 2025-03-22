package io.techchamps.tutorial.C_helperclass;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;


public class Helper {

    // Creates and returns a basic request specification
    public static RequestSpecification spec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8085)
                .setBasePath("/api")
                .addHeader("Content-Type", "application/json")
                .log(LogDetail.ALL)
                .build();
    }
}
