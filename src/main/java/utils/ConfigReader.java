package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();

    public static void loadProperties(String configFile) {
        try {
            // Load config
            FileInputStream Config = new FileInputStream("src/test/resources/config/config.properties");
            properties.load(Config);
            Config.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }


}
