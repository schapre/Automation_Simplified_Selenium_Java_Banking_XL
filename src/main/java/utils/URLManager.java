package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URLManager - Centralized URL management for all pages
 * Provides URL retrieval from configuration with proper logging
 */
public class URLManager {
    private static final Logger logger = LoggerFactory.getLogger(URLManager.class);

    // TesterBud URLs
    private static final String TESTERBUD_BASE_URL = ConfigReader.getProperty("app.testerbud.baseUrl");
    private static final String TESTERBUD_LOGIN_URL = ConfigReader.getProperty("app.testerbud.loginUrl");
    private static final String TESTERBUD_REGISTER_URL = ConfigReader.getProperty("app.testerbud.registerUrl");
    private static final String TESTERBUD_FORGET_PASSWORD_URL = ConfigReader
            .getProperty("app.testerbud.forgetPasswordUrl");

    // OrangeHRM URLs
    private static final String ORANGEHRM_BASE_URL = ConfigReader.getProperty("app.orangehrm.baseUrl");
    private static final String ORANGEHRM_LOGIN_URL = ConfigReader.getProperty("app.orangehrm.loginUrl");
    private static final String ORANGEHRM_DASHBOARD_URL = ConfigReader.getProperty("app.orangehrm.dashboardUrl");

    // Legacy web.baseUrl support
    private static final String WEB_BASE_URL = ConfigReader.getProperty("web.baseUrl");

    /**
     * Get TesterBud base URL
     * 
     * @return TesterBud base URL
     */
    public static String getTesterBudBaseUrl() {
        logger.debug("Retrieved TesterBud base URL: {}", TESTERBUD_BASE_URL);
        return TESTERBUD_BASE_URL;
    }

    /**
     * Get TesterBud login page URL
     * 
     * @return TesterBud login URL
     */
    public static String getTesterBudLoginUrl() {
        logger.debug("Retrieved TesterBud login URL: {}", TESTERBUD_LOGIN_URL);
        return TESTERBUD_LOGIN_URL;
    }

    /**
     * Get TesterBud registration page URL
     * 
     * @return TesterBud registration URL
     */
    public static String getTesterBudRegisterUrl() {
        logger.debug("Retrieved TesterBud registration URL: {}", TESTERBUD_REGISTER_URL);
        return TESTERBUD_REGISTER_URL;
    }

    /**
     * Get TesterBud forget password page URL
     * 
     * @return TesterBud forget password URL
     */
    public static String getTesterBudForgetPasswordUrl() {
        logger.debug("Retrieved TesterBud forget password URL: {}", TESTERBUD_FORGET_PASSWORD_URL);
        return TESTERBUD_FORGET_PASSWORD_URL;
    }

    /**
     * Get OrangeHRM base URL
     * 
     * @return OrangeHRM base URL
     */
    public static String getOrangeHrmBaseUrl() {
        logger.debug("Retrieved OrangeHRM base URL: {}", ORANGEHRM_BASE_URL);
        return ORANGEHRM_BASE_URL;
    }

    /**
     * Get OrangeHRM login page URL
     * 
     * @return OrangeHRM login URL
     */
    public static String getOrangeHrmLoginUrl() {
        logger.debug("Retrieved OrangeHRM login URL: {}", ORANGEHRM_LOGIN_URL);
        return ORANGEHRM_LOGIN_URL;
    }

    /**
     * Get OrangeHRM dashboard page URL
     * 
     * @return OrangeHRM dashboard URL
     */
    public static String getOrangeHrmDashboardUrl() {
        logger.debug("Retrieved OrangeHRM dashboard URL: {}", ORANGEHRM_DASHBOARD_URL);
        return ORANGEHRM_DASHBOARD_URL;
    }

    /**
     * Get web base URL (legacy support for web.baseUrl property)
     * 
     * @return Web base URL
     */
    public static String getWebBaseUrl() {
        logger.debug("Retrieved web base URL: {}", WEB_BASE_URL);
        return WEB_BASE_URL;
    }

    /**
     * Get custom URL from config by property key
     * 
     * @param propertyKey Configuration property key
     * @return URL from configuration
     */
    public static String getCustomUrl(String propertyKey) {
        String url = ConfigReader.getProperty(propertyKey);
        logger.debug("Retrieved custom URL for property '{}': {}", propertyKey, url);
        return url;
    }

    /**
     * Build dynamic URL with path parameters
     * 
     * @param baseUrl      Base URL
     * @param pathSegments URL path segments
     * @return Complete URL
     */
    public static String buildUrl(String baseUrl, String... pathSegments) {
        StringBuilder url = new StringBuilder(baseUrl);

        // Ensure base URL doesn't end with slash
        if (url.charAt(url.length() - 1) == '/') {
            url.setLength(url.length() - 1);
        }

        // Append path segments
        for (String segment : pathSegments) {
            if (!segment.startsWith("/")) {
                url.append("/");
            }
            url.append(segment);
        }

        String finalUrl = url.toString();
        logger.debug("Built dynamic URL: {}", finalUrl);
        return finalUrl;
    }

    /**
     * Log URL navigation for debugging
     * 
     * @param pageName Page name being navigated to
     * @param url      URL being accessed
     */
    public static void logNavigation(String pageName, String url) {
        logger.info("Navigating to {} page: {}", pageName, url);
    }
}
