package utils;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Locale;

/**
 * Unit tests for MessageFormatter
 * Demonstrates message retrieval, formatting, and i18n capabilities
 */
public class MessageFormatterTest {

    @AfterMethod
    public void resetLocale() {
        MessageFormatter.resetLocale();
    }

    @Test
    public void testBasicMessageRetrieval() {
        String message = MessageFormatter.getMessage("nav.login.page");
        Assert.assertEquals(message, "Navigated to login page");
    }

    @Test
    public void testParameterizedMessage() {
        String message = MessageFormatter.getMessage("test.scenario.setup", "Login Test");
        Assert.assertEquals(message, "Setting up test scenario: Login Test");
    }

    @Test
    public void testMultipleParameters() {
        String message = MessageFormatter.getDatabaseMessage("insert.executed", 5, "users");
        Assert.assertEquals(message, "➕ Inserted 5 record(s) into users");
    }

    @Test
    public void testStepMessage() {
        String message = MessageFormatter.getStepMessage("navigating.login");
        Assert.assertEquals(message, "Step: Navigating to login page");
    }

    @Test
    public void testDatabaseMessage() {
        String message = MessageFormatter.getDatabaseMessage("connection.verified");
        Assert.assertEquals(message, "🗄️ Database connection verified");
    }

    @Test
    public void testDriverMessage() {
        String message = MessageFormatter.getDriverMessage("setup.chrome");
        Assert.assertEquals(message, "Setting up ChromeDriver using WebDriverManager");
    }

    @Test
    public void testConfigMessage() {
        String message = MessageFormatter.getConfigMessage("loaded", 15);
        Assert.assertEquals(message, "Configuration loaded successfully with 15 properties");
    }

    @Test
    public void testValidationMessage() {
        String message = MessageFormatter.getValidationMessage("success.verified", "Login Successful");
        Assert.assertEquals(message, "Success message verified: Login Successful");
    }

    @Test
    public void testAssertionMessage() {
        String message = MessageFormatter.formatAssertionMessage(
                "assert.expected.vs.actual",
                "Success",
                "Failed");
        Assert.assertEquals(message, "Expected message: Success but found: Failed");
    }

    @Test
    public void testSpanishLocale() {
        MessageFormatter.setLocale(new Locale("es"));
        String message = MessageFormatter.getMessage("success.login");
        Assert.assertEquals(message, "Inicio de sesión exitoso");
    }

    @Test
    public void testSpanishWithParameters() {
        MessageFormatter.setLocale(new Locale("es"));
        String message = MessageFormatter.getMessage("test.scenario.setup", "Prueba de Login");
        Assert.assertEquals(message, "Configurando escenario de prueba: Prueba de Login");
    }

    @Test
    public void testLocaleSwitch() {
        // Test English
        String englishMsg = MessageFormatter.getMessage("test.scenario.passed", "Test1");
        Assert.assertEquals(englishMsg, "Test scenario PASSED: Test1");

        // Switch to Spanish
        MessageFormatter.setLocale(new Locale("es"));
        String spanishMsg = MessageFormatter.getMessage("test.scenario.passed", "Test1");
        Assert.assertEquals(spanishMsg, "Escenario de prueba APROBADO: Test1");

        // Reset to English
        MessageFormatter.resetLocale();
        String englishAgain = MessageFormatter.getMessage("test.scenario.passed", "Test1");
        Assert.assertEquals(englishAgain, "Test scenario PASSED: Test1");
    }

    @Test
    public void testMissingMessageFallback() {
        String message = MessageFormatter.getMessage("nonexistent.key");
        Assert.assertEquals(message, "nonexistent.key");
    }

    @Test
    public void testHasMessage() {
        Assert.assertTrue(MessageFormatter.hasMessage("nav.login.page"));
        Assert.assertFalse(MessageFormatter.hasMessage("nonexistent.key"));
    }

    @Test
    public void testGetCurrentLocale() {
        Assert.assertEquals(MessageFormatter.getCurrentLocale(), Locale.ENGLISH);

        MessageFormatter.setLocale(new Locale("es"));
        Assert.assertEquals(MessageFormatter.getCurrentLocale().getLanguage(), "es");
    }

    @Test
    public void testSupportedLocales() {
        Locale[] locales = MessageFormatter.getSupportedLocales();
        Assert.assertTrue(locales.length >= 2);
        Assert.assertEquals(locales[0], Locale.ENGLISH);
    }

    @Test
    public void testErrorMessage() {
        String message = MessageFormatter.getMessage("error.email.required");
        Assert.assertEquals(message, "Email is required");
    }

    @Test
    public void testSuccessMessage() {
        String message = MessageFormatter.getMessage("success.login");
        Assert.assertEquals(message, "Login Successful");
    }

    @Test
    public void testConfigPropertyNotFound() {
        String message = MessageFormatter.getConfigMessage("property.not.found", "apiKey");
        Assert.assertEquals(message, "Property 'apiKey' not found in configuration");
    }

    @Test
    public void testDatabaseValidationMessage() {
        String message = MessageFormatter.getDatabaseMessage(
                "validation.column.failed",
                "email",
                "test@example.com",
                "wrong@example.com");
        Assert.assertTrue(message.contains("Validation failed"));
        Assert.assertTrue(message.contains("email"));
    }

    @Test
    public void testMessageWithSpecialCharacters() {
        String message = MessageFormatter.getDatabaseMessage("connection.verified");
        Assert.assertTrue(message.contains("🗄️"));

        message = MessageFormatter.getDatabaseMessage("query.executed", 10);
        Assert.assertTrue(message.contains("📊"));
    }
}
