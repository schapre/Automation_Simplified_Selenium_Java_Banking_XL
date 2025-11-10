package utils;

import java.util.ArrayList;
import java.util.List;

public class PlatformSelector {

    public enum PlatformType {
        WEB, API, MOBILE
    }

    private static List<PlatformType> selectedPlatforms = new ArrayList<>();

    public static void initializePlatform(String configFilePath) {
        ConfigReader.loadProperties(configFilePath);
        String platformConfig = ConfigReader.getProperty("platform");

        selectedPlatforms.clear(); // Clear previous values

        String[] platforms = platformConfig.split(",");
        for (String platform : platforms) {
            try {
                PlatformType type = PlatformType.valueOf(platform.trim().toUpperCase());
                selectedPlatforms.add(type);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unsupported platform: " + platform.trim());
            }
        }
    }

    public static List<PlatformType> getPlatformTypes() {
        return selectedPlatforms;
    }
}
