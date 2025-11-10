package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties = new Properties();

    static {
        try {
            // Load master config
            InputStream masterConfig = ConfigLoader.class.getClassLoader().getResourceAsStream("config/config.properties");
            Properties masterProps = new Properties();
            masterProps.load(masterConfig);

            String platform = masterProps.getProperty("platform");
            if (platform == null || platform.isEmpty()) {
                throw new IllegalArgumentException("Platform is not defined in config.properties");
            }

            // Load platform-specific config
            String platformConfigFile = String.format("config/config_%s.properties", platform.toLowerCase());
            InputStream platformConfig = ConfigLoader.class.getClassLoader().getResourceAsStream(platformConfigFile);
            if (platformConfig == null) {
                throw new IllegalArgumentException("Platform-specific config file not found: " + platformConfigFile);
            }

            properties.load(platformConfig);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration files", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
