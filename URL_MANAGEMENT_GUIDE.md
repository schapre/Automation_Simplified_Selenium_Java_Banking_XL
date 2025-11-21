# URL Management Guide

## Overview
This document explains the centralized URL management system implemented in the test automation framework. All application URLs are now managed through the `URLManager` utility class and configured in a central properties file.

## Architecture

### URLManager Utility Class
Location: `src/main/java/utils/URLManager.java`

The `URLManager` class provides a centralized interface for accessing all application URLs used in the testing framework. It acts as a facade over `ConfigReader`, providing type-safe access to configured URLs.

**Key Features:**
- Centralized URL access through static methods
- Built-in logging for debugging and traceability
- URL building capabilities for dynamic URLs
- Support for multiple applications (TesterBud, OrangeHRM)
- Fallback mechanisms for critical URLs

### Configuration File
Location: `src/test/resources/config/config.properties`

All application URLs are configured in this file using a hierarchical naming convention:

```properties
# TesterBud Application URLs
app.testerbud.baseUrl=https://testerbud.com
app.testerbud.loginUrl=https://testerbud.com/practice-login-form
app.testerbud.registerUrl=https://testerbud.com/register
app.testerbud.forgetPasswordUrl=https://testerbud.com/forget-password

# OrangeHRM Application URLs
app.orangehrm.baseUrl=https://opensource-demo.orangehrmlive.com
app.orangehrm.loginUrl=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
app.orangehrm.dashboardUrl=https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index

# API URLs
api.baseUrl=https://opensource-demo.orangehrmlive.com/web/index.php/api/v2

# Legacy/General URLs
web.baseUrl=https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
```

## Usage Guide

### 1. Accessing Pre-configured URLs

#### TesterBud URLs
```java
// Navigate to TesterBud login page
driver.get(URLManager.getTesterBudLoginUrl());

// Navigate to TesterBud registration page
driver.get(URLManager.getTesterBudRegisterUrl());

// Navigate to TesterBud forget password page
driver.get(URLManager.getTesterBudForgetPasswordUrl());

// Get TesterBud base URL for building custom URLs
String baseUrl = URLManager.getTesterBudBaseUrl();
```

#### OrangeHRM URLs
```java
// Navigate to OrangeHRM login page
driver.get(URLManager.getOrangeHrmLoginUrl());

// Navigate to OrangeHRM dashboard
driver.get(URLManager.getOrangeHrmDashboardUrl());

// Get OrangeHRM base URL
String baseUrl = URLManager.getOrangeHrmBaseUrl();
```

#### Generic/Legacy URLs
```java
// Get web base URL (legacy configuration)
String webUrl = URLManager.getWebBaseUrl();

// Get API base URL
String apiUrl = URLManager.getApiBaseUrl();
```

### 2. Building Dynamic URLs

The `URLManager` provides a `buildUrl()` method for constructing URLs dynamically:

```java
// Build URL from base and path segments
String userProfileUrl = URLManager.buildUrl(
    URLManager.getTesterBudBaseUrl(), 
    "user", 
    "profile", 
    "edit"
);
// Result: https://testerbud.com/user/profile/edit

// Build URL with query parameters
String apiUrl = URLManager.buildUrl(
    URLManager.getApiBaseUrl(), 
    "users", 
    "search?name=john&role=admin"
);
```

### 3. Accessing Custom URLs

For custom or newly added URLs not yet in the URLManager methods:

```java
// Access any property directly
String customUrl = URLManager.getUrl("app.myapp.customUrl");
```

### 4. Initializing ConfigReader (for unit tests)

When writing unit tests that use URLManager, ensure ConfigReader is initialized:

```java
@BeforeClass
public void setUp() {
    ConfigReader.loadProperties("src/test/resources/config/config.properties");
}
```

**Note:** Cucumber tests automatically initialize ConfigReader in the `@Before` hook.

## Migration from Hardcoded URLs

### Before (Hardcoded)
```java
public class UI_LoginTestPage {
    public void navigateToLoginPage() {
        driver.get("https://testerbud.com/practice-login-form");
    }
}
```

### After (Centralized)
```java
import utils.URLManager;

public class UI_LoginTestPage {
    public void navigateToLoginPage() {
        driver.get(URLManager.getTesterBudLoginUrl());
    }
}
```

## Benefits

### 1. **Maintainability**
- Single source of truth for all URLs
- Easy to update URLs across entire framework
- No need to search through code to find hardcoded URLs

### 2. **Environment Management**
- Different URLs for different environments (dev, test, prod)
- Can load different config files based on environment
- Supports configuration profiles

### 3. **Testing**
- Easy to mock or override URLs in tests
- Can test against different environments without code changes
- Consistent URL format validation

### 4. **Debugging**
- All URL accesses are logged with DEBUG level
- Easy to trace which URLs are being used in tests
- Helps identify URL-related issues quickly

### 5. **Type Safety**
- Method names provide clear intent
- IDE autocomplete helps discover available URLs
- Compile-time checking prevents typos

## Best Practices

### 1. Adding New URLs

When adding a new application or URL:

**Step 1:** Add the URL to `config.properties` following the naming convention:
```properties
app.{application}.{purpose}Url={url}
```

**Step 2:** Add a getter method in `URLManager`:
```java
public static String getMyAppLoginUrl() {
    String url = ConfigReader.getProperty("app.myapp.loginUrl");
    logger.debug("Retrieved MyApp login URL: {}", url);
    return url;
}
```

**Step 3:** Use the new method in your page objects:
```java
driver.get(URLManager.getMyAppLoginUrl());
```

### 2. Naming Conventions

Follow these conventions for consistency:

- **Property Keys:** `app.{application}.{purpose}Url`
  - Examples: `app.testerbud.loginUrl`, `app.orangehrm.dashboardUrl`
  
- **Method Names:** `get{Application}{Purpose}Url()`
  - Examples: `getTesterBudLoginUrl()`, `getOrangeHrmDashboardUrl()`

### 3. Handling Missing URLs

Always provide meaningful fallbacks or error messages:

```java
public static String getCriticalUrl() {
    String url = ConfigReader.getProperty("app.critical.url");
    if (url == null || url.isEmpty()) {
        String fallbackUrl = "https://default-url.com";
        logger.warn("Critical URL not found, using fallback: {}", fallbackUrl);
        return fallbackUrl;
    }
    return url;
}
```

### 4. URL Validation

For critical URLs, add validation:

```java
public static String getValidatedUrl(String propertyKey) {
    String url = ConfigReader.getProperty(propertyKey);
    if (url == null) {
        throw new IllegalStateException("Required URL property not found: " + propertyKey);
    }
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        throw new IllegalArgumentException("Invalid URL format for " + propertyKey + ": " + url);
    }
    return url;
}
```

## Files Modified

The following files were updated to use URLManager:

### Page Objects
- `src/test/java/pages/UI_LoginTestPage.java`
- `src/test/java/pages/UI_RegistrationTestPage.java`
- `src/test/java/pages/UI_ForgetPasswordTestPage.java`

### Step Definitions
- `src/test/java/stepdefinitions/web/UI_OrangeHRM_LoginPage_StepDefinition.java`

### Test Classes
- `src/test/java/tests/examples/ExcelDataDrivenTest.java`

### New Files
- `src/main/java/utils/URLManager.java` - URL management utility
- `src/test/java/utils/URLManagerTest.java` - Unit tests (8 tests, all passing)

## Testing

### Unit Tests
Location: `src/test/java/utils/URLManagerTest.java`

Run unit tests:
```bash
mvn test -Dtest=URLManagerTest
```

**Test Coverage:**
- ✅ TesterBud URL retrieval
- ✅ OrangeHRM URL retrieval
- ✅ Web base URL retrieval
- ✅ Custom URL retrieval
- ✅ Basic URL building
- ✅ Complex URL building
- ✅ URL format validation
- ✅ URL consistency checks

All 8 tests pass successfully.

## Troubleshooting

### Issue: "Property not found in configuration"

**Cause:** ConfigReader not initialized before URLManager is called.

**Solution:** Ensure `ConfigReader.loadProperties()` is called before using URLManager:
```java
@BeforeClass
public void setUp() {
    ConfigReader.loadProperties("src/test/resources/config/config.properties");
}
```

### Issue: URLManager methods return null

**Cause:** Property not defined in config.properties.

**Solution:** Add the missing property to `src/test/resources/config/config.properties`.

### Issue: Hardcoded URL still present

**Cause:** Some files may still use old hardcoded URLs.

**Solution:** Search for hardcoded URLs:
```bash
# PowerShell
Select-String -Pattern 'driver\.get\(\s*"http' -Path .\src\**\*.java -Recurse

# Bash/Git Bash
grep -r 'driver\.get\s*("http' src/
```

## Related Documentation

- [ConfigReader Documentation](src/main/java/utils/ConfigReader.java)
- [Message Formatting Guide](INTEGRATION_SUMMARY.md)
- [Exception Logging Implementation](EXCEPTION_LOGGING_IMPLEMENTATION.md)
- [Logging Best Practices Audit](LOGGING_BEST_PRACTICES_AUDIT.md)

## Summary

The URLManager implementation provides a robust, maintainable solution for managing application URLs across the test automation framework. By centralizing URL configuration, the framework becomes more flexible, easier to maintain, and better prepared for multi-environment testing scenarios.

**Key Achievements:**
✅ All hardcoded URLs eliminated from page objects
✅ Centralized URL configuration in properties file
✅ Type-safe URL access through URLManager
✅ Comprehensive logging for debugging
✅ Full test coverage (8/8 tests passing)
✅ Consistent with existing framework patterns (ConfigReader, MessageFormatter)
