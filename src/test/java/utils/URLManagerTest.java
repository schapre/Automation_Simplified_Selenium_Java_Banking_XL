package utils;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit tests for URLManager utility class
 */
public class URLManagerTest {

    /**
     * Load configuration properties before running tests
     */
    @BeforeClass
    public void setUp() {
        ConfigReader.loadProperties("src/test/resources/config/config.properties");
    }

    @Test(description = "Verify TesterBud URLs are retrieved correctly from config")
    public void testGetTesterBudUrls() {
        // Test base URL
        String baseUrl = URLManager.getTesterBudBaseUrl();
        Assert.assertNotNull(baseUrl, "TesterBud base URL should not be null");
        Assert.assertEquals(baseUrl, "https://testerbud.com", "TesterBud base URL mismatch");

        // Test login URL
        String loginUrl = URLManager.getTesterBudLoginUrl();
        Assert.assertNotNull(loginUrl, "TesterBud login URL should not be null");
        Assert.assertEquals(loginUrl, "https://testerbud.com/practice-login-form", "TesterBud login URL mismatch");

        // Test register URL
        String registerUrl = URLManager.getTesterBudRegisterUrl();
        Assert.assertNotNull(registerUrl, "TesterBud register URL should not be null");
        Assert.assertEquals(registerUrl, "https://testerbud.com/register", "TesterBud register URL mismatch");

        // Test forget password URL
        String forgetPasswordUrl = URLManager.getTesterBudForgetPasswordUrl();
        Assert.assertNotNull(forgetPasswordUrl, "TesterBud forget password URL should not be null");
        Assert.assertEquals(forgetPasswordUrl, "https://testerbud.com/forget-password",
                "TesterBud forget password URL mismatch");
    }

    @Test(description = "Verify OrangeHRM URLs are retrieved correctly from config")
    public void testGetOrangeHrmUrls() {
        // Test base URL
        String baseUrl = URLManager.getOrangeHrmBaseUrl();
        Assert.assertNotNull(baseUrl, "OrangeHRM base URL should not be null");
        Assert.assertEquals(baseUrl, "https://opensource-demo.orangehrmlive.com", "OrangeHRM base URL mismatch");

        // Test login URL
        String loginUrl = URLManager.getOrangeHrmLoginUrl();
        Assert.assertNotNull(loginUrl, "OrangeHRM login URL should not be null");
        Assert.assertTrue(loginUrl.contains("auth/login"), "OrangeHRM login URL should contain auth/login");

        // Test dashboard URL
        String dashboardUrl = URLManager.getOrangeHrmDashboardUrl();
        Assert.assertNotNull(dashboardUrl, "OrangeHRM dashboard URL should not be null");
        Assert.assertTrue(dashboardUrl.contains("dashboard"), "OrangeHRM dashboard URL should contain dashboard");
    }

    @Test(description = "Verify web base URL (legacy support)")
    public void testGetWebBaseUrl() {
        String webBaseUrl = URLManager.getWebBaseUrl();
        Assert.assertNotNull(webBaseUrl, "Web base URL should not be null");
        Assert.assertTrue(webBaseUrl.startsWith("https://"), "Web base URL should start with https://");
    }

    @Test(description = "Verify custom URL retrieval by property key")
    public void testGetCustomUrl() {
        // Test retrieving existing property
        String apiBaseUrl = URLManager.getCustomUrl("api.baseUrl");
        Assert.assertNotNull(apiBaseUrl, "API base URL should not be null");
        Assert.assertTrue(apiBaseUrl.contains("orangehrmlive.com"), "API base URL should contain orangehrmlive.com");

        // Test retrieving TesterBud URL by property key
        String testerBudLogin = URLManager.getCustomUrl("app.testerbud.loginUrl");
        Assert.assertNotNull(testerBudLogin, "Custom TesterBud login URL should not be null");
        Assert.assertEquals(testerBudLogin, "https://testerbud.com/practice-login-form", "Custom URL mismatch");
    }

    @Test(description = "Verify dynamic URL building with path segments")
    public void testBuildUrl() {
        // Test building URL with single path segment
        String url1 = URLManager.buildUrl("https://testerbud.com", "practice-login-form");
        Assert.assertEquals(url1, "https://testerbud.com/practice-login-form", "Single path segment URL mismatch");

        // Test building URL with multiple path segments
        String url2 = URLManager.buildUrl("https://api.example.com", "v1", "users", "123");
        Assert.assertEquals(url2, "https://api.example.com/v1/users/123", "Multiple path segments URL mismatch");

        // Test building URL with path segment starting with slash
        String url3 = URLManager.buildUrl("https://testerbud.com", "/register");
        Assert.assertEquals(url3, "https://testerbud.com/register", "Path with leading slash should work");

        // Test building URL with base URL ending with slash
        String url4 = URLManager.buildUrl("https://testerbud.com/", "register");
        Assert.assertEquals(url4, "https://testerbud.com/register", "Base URL with trailing slash should be handled");
    }

    @Test(description = "Verify URL building with complex paths")
    public void testBuildComplexUrl() {
        String baseUrl = URLManager.getTesterBudBaseUrl();

        // Build user profile URL
        String userProfileUrl = URLManager.buildUrl(baseUrl, "user", "profile", "edit");
        Assert.assertEquals(userProfileUrl, "https://testerbud.com/user/profile/edit", "Complex path URL mismatch");

        // Build API endpoint URL
        String apiUrl = URLManager.buildUrl(baseUrl, "api", "v2", "authenticate");
        Assert.assertEquals(apiUrl, "https://testerbud.com/api/v2/authenticate", "API endpoint URL mismatch");
    }

    @Test(description = "Verify all URLs follow correct format")
    public void testUrlFormat() {
        // All URLs should start with https://
        Assert.assertTrue(URLManager.getTesterBudBaseUrl().startsWith("https://"),
                "TesterBud base URL should use HTTPS");
        Assert.assertTrue(URLManager.getOrangeHrmBaseUrl().startsWith("https://"),
                "OrangeHRM base URL should use HTTPS");

        // All URLs should not end with slash (except base URLs if needed)
        Assert.assertFalse(URLManager.getTesterBudLoginUrl().endsWith("/"), "Login URL should not end with slash");
        Assert.assertFalse(URLManager.getTesterBudRegisterUrl().endsWith("/"),
                "Register URL should not end with slash");
    }

    @Test(description = "Verify URL consistency across applications")
    public void testUrlConsistency() {
        // TesterBud URLs should all start with TesterBud base URL
        String testerBudBase = URLManager.getTesterBudBaseUrl();
        Assert.assertTrue(URLManager.getTesterBudLoginUrl().startsWith(testerBudBase),
                "TesterBud login URL should start with base URL");
        Assert.assertTrue(URLManager.getTesterBudRegisterUrl().startsWith(testerBudBase),
                "TesterBud register URL should start with base URL");
        Assert.assertTrue(URLManager.getTesterBudForgetPasswordUrl().startsWith(testerBudBase),
                "TesterBud forget password URL should start with base URL");

        // OrangeHRM URLs should all start with OrangeHRM base URL
        String orangeHrmBase = URLManager.getOrangeHrmBaseUrl();
        Assert.assertTrue(URLManager.getOrangeHrmLoginUrl().startsWith(orangeHrmBase),
                "OrangeHRM login URL should start with base URL");
        Assert.assertTrue(URLManager.getOrangeHrmDashboardUrl().startsWith(orangeHrmBase),
                "OrangeHRM dashboard URL should start with base URL");
    }
}
