package utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(ApiClientFactory.class);
    private static RequestSpecification apiClient;

    public static void initApiClient() {
        String baseUri = ConfigReader.getProperty("api.baseUrl");
        logger.info("Initializing API Client with base URI: {}", baseUri);
        apiClient = RestAssured.given()
                .baseUri(baseUri)
                .header("Authorization", "Bearer " + ConfigReader.getProperty("api.authToken"));
        logger.info("API Client initialized successfully");
    }

    public static RequestSpecification getApiClient() {
        return apiClient;
    }

    public static void resetApiClient() {
        logger.info("Resetting API Client");
        apiClient = null;
    }
}
