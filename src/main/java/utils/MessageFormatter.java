package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message Formatter Utility
 * Provides centralized message management with internationalization support
 * and parameterized message formatting.
 * 
 * Features:
 * - Externalized message resources (messages.properties)
 * - i18n support (multiple languages)
 * - Parameterized message formatting
 * - Fallback mechanism for missing messages
 * - Thread-safe locale management
 */
public class MessageFormatter {

    private static final Logger logger = LoggerFactory.getLogger(MessageFormatter.class);
    private static final String BUNDLE_NAME = "messages";
    private static ThreadLocal<Locale> currentLocale = ThreadLocal.withInitial(() -> Locale.ENGLISH);
    private static ResourceBundle resourceBundle;

    static {
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale.get());
            logger.debug("Message resources loaded successfully for locale: {}", currentLocale.get());
        } catch (MissingResourceException e) {
            logger.error("Failed to load message resources for bundle: {}", BUNDLE_NAME, e);
            throw new RuntimeException("Message resource bundle not found: " + BUNDLE_NAME, e);
        }
    }

    /**
     * Get formatted message with parameters
     * 
     * @param key    Message key from properties file
     * @param params Optional parameters for message formatting
     * @return Formatted message string
     */
    public static String getMessage(String key, Object... params) {
        try {
            String message = resourceBundle.getString(key);
            if (params != null && params.length > 0) {
                return MessageFormat.format(message, params);
            }
            return message;
        } catch (MissingResourceException e) {
            logger.warn("Message key not found: {}. Using key as message.", key);
            return key;
        } catch (IllegalArgumentException e) {
            logger.error("Error formatting message for key: {}", key, e);
            return resourceBundle.getString(key);
        }
    }

    /**
     * Get message without parameters
     * 
     * @param key Message key from properties file
     * @return Message string
     */
    public static String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }

    /**
     * Set locale for message retrieval
     * 
     * @param locale Desired locale (e.g., Locale.ENGLISH, Locale.SPANISH)
     */
    public static void setLocale(Locale locale) {
        if (locale == null) {
            logger.warn("Attempted to set null locale. Using default: {}", Locale.ENGLISH);
            locale = Locale.ENGLISH;
        }
        currentLocale.set(locale);
        try {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            logger.info("Locale changed to: {}", locale);
        } catch (MissingResourceException e) {
            logger.error("Resource bundle not found for locale: {}. Falling back to default.", locale);
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
        }
    }

    /**
     * Get current locale
     * 
     * @return Current locale
     */
    public static Locale getCurrentLocale() {
        return currentLocale.get();
    }

    /**
     * Reset locale to default (English)
     */
    public static void resetLocale() {
        setLocale(Locale.ENGLISH);
    }

    /**
     * Format assertion message with expected and actual values
     * 
     * @param messageKey Message key from properties
     * @param expected   Expected value
     * @param actual     Actual value
     * @return Formatted assertion message
     */
    public static String formatAssertionMessage(String messageKey, Object expected, Object actual) {
        return getMessage(messageKey, expected, actual);
    }

    /**
     * Get error message with single parameter
     * 
     * @param errorKey Error message key
     * @param value    Parameter value
     * @return Formatted error message
     */
    public static String getErrorMessage(String errorKey, Object value) {
        return getMessage("error." + errorKey, value);
    }

    /**
     * Get success message with single parameter
     * 
     * @param successKey Success message key
     * @param value      Parameter value
     * @return Formatted success message
     */
    public static String getSuccessMessage(String successKey, Object value) {
        return getMessage("success." + successKey, value);
    }

    /**
     * Get validation message
     * 
     * @param validationKey Validation message key
     * @param params        Parameters for message formatting
     * @return Formatted validation message
     */
    public static String getValidationMessage(String validationKey, Object... params) {
        return getMessage("validation." + validationKey, params);
    }

    /**
     * Get database message
     * 
     * @param dbKey  Database message key
     * @param params Parameters for message formatting
     * @return Formatted database message
     */
    public static String getDatabaseMessage(String dbKey, Object... params) {
        return getMessage("db." + dbKey, params);
    }

    /**
     * Get step execution message
     * 
     * @param stepKey Step message key
     * @param params  Parameters for message formatting
     * @return Formatted step message
     */
    public static String getStepMessage(String stepKey, Object... params) {
        return getMessage("step." + stepKey, params);
    }

    /**
     * Get driver management message
     * 
     * @param driverKey Driver message key
     * @param params    Parameters for message formatting
     * @return Formatted driver message
     */
    public static String getDriverMessage(String driverKey, Object... params) {
        return getMessage("driver." + driverKey, params);
    }

    /**
     * Get configuration message
     * 
     * @param configKey Configuration message key
     * @param params    Parameters for message formatting
     * @return Formatted configuration message
     */
    public static String getConfigMessage(String configKey, Object... params) {
        return getMessage("config." + configKey, params);
    }

    /**
     * Check if message key exists in resource bundle
     * 
     * @param key Message key
     * @return true if key exists, false otherwise
     */
    public static boolean hasMessage(String key) {
        try {
            resourceBundle.getString(key);
            return true;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    /**
     * Get all available locales supported by this framework
     * 
     * @return Array of supported locales
     */
    public static Locale[] getSupportedLocales() {
        return new Locale[] {
                Locale.ENGLISH,
                new Locale("es"), // Spanish
                // Add more locales as resource files are added
        };
    }
}
