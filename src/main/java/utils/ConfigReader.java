package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static Properties properties = new Properties();

    public static void loadProperties(String configFile) {
        try {
            logger.info("Loading configuration from: src/test/resources/config/config.properties");
            // Load config
            FileInputStream Config = new FileInputStream("src/test/resources/config/config.properties");
            properties.load(Config);
            Config.close();
            logger.info(MessageFormatter.getConfigMessage("loaded", properties.size()));
            if (properties.isEmpty()) {
                logger.warn(MessageFormatter.getConfigMessage("file.empty"));
            }

        } catch (IOException e) {
            logger.error("Failed to load configuration file", e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn(MessageFormatter.getConfigMessage("property.not.found", key));
        } else {
            logger.debug(MessageFormatter.getConfigMessage("property.retrieved", key, value));
        }
        return value;
    }

}
