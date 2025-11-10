package utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class ApiClientFactory {
    private static RequestSpecification apiClient;

    public static void initApiClient() {
        apiClient = RestAssured.given()
            .baseUri(ConfigReader.getProperty("api.baseUrl"))
            .header("Authorization", "Bearer " + ConfigReader.getProperty("api.authToken"));
    }

    public static RequestSpecification getApiClient() {
        return apiClient;
    }

    public static void resetApiClient() {
        apiClient = null;
    }
}
