package utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Video Management utility class
 * Provides enhanced control over video recording operations
 */
public class VideoManager {

    private static final String DEFAULT_VIDEO_FOLDER = "test-output/videos/";
    private static boolean recordingEnabled = true;

    /**
     * Enable or disable video recording globally
     * 
     * @param enabled true to enable recording
     */
    public static void setRecordingEnabled(boolean enabled) {
        recordingEnabled = enabled;
        System.out.println(enabled ? "🎥 Video recording enabled" : "🚫 Video recording disabled");
    }

    /**
     * Check if video recording is enabled
     * 
     * @return true if recording is enabled
     */
    public static boolean isRecordingEnabled() {
        // Check config property if available
        try {
            String configValue = ConfigReader.getProperty("video.recording.enabled");
            if (configValue != null) {
                return Boolean.parseBoolean(configValue);
            }
        } catch (Exception e) {
            // Fall back to default if config not available
        }
        return recordingEnabled;
    }

    /**
     * Start video recording if enabled
     * 
     * @param testName Name of the test
     */
    public static void startRecording(String testName) {
        if (isRecordingEnabled()) {
            VideoRecorder.startRecording(testName);
        } else {
            System.out.println("🚫 Video recording is disabled - skipping for: " + testName);
        }
    }

    /**
     * Stop video recording if active
     * 
     * @param attachToReport Whether to attach to report
     * @return Path to recorded video or null
     */
    public static String stopRecording(boolean attachToReport) {
        if (VideoRecorder.isRecording()) {
            return AllureManager.stopVideoRecording(attachToReport);
        }
        return null;
    }

    /**
     * Get video statistics
     * 
     * @return Video statistics as formatted string
     */
    public static String getVideoStatistics() {
        File videoDir = new File(getVideoFolder());
        if (!videoDir.exists()) {
            return "No videos directory found";
        }

        File[] videoFiles = videoDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4") ||
                name.toLowerCase().endsWith(".webm") ||
                name.toLowerCase().endsWith(".avi") ||
                name.toLowerCase().endsWith(".mov"));

        if (videoFiles == null || videoFiles.length == 0) {
            return "No video files found";
        }

        long totalSize = 0;
        for (File file : videoFiles) {
            totalSize += file.length();
        }

        double totalSizeMB = totalSize / (1024.0 * 1024.0);

        return String.format("Videos: %d files, Total size: %.2f MB",
                videoFiles.length, totalSizeMB);
    }

    /**
     * Get the configured video folder path
     * 
     * @return Video folder path
     */
    public static String getVideoFolder() {
        try {
            String configFolder = ConfigReader.getProperty("video.recording.folder");
            if (configFolder != null && !configFolder.isEmpty()) {
                return configFolder;
            }
        } catch (Exception e) {
            // Fall back to default
        }
        return DEFAULT_VIDEO_FOLDER;
    }

    /**
     * Clean up videos older than specified days
     * 
     * @param daysToKeep Number of days to keep videos
     * @return Number of files deleted
     */
    public static int cleanupOldVideos(int daysToKeep) {
        File videoDir = new File(getVideoFolder());
        if (!videoDir.exists()) {
            return 0;
        }

        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        File[] videoFiles = videoDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4") ||
                name.toLowerCase().endsWith(".webm") ||
                name.toLowerCase().endsWith(".avi") ||
                name.toLowerCase().endsWith(".mov"));

        if (videoFiles == null) {
            return 0;
        }

        int deletedCount = 0;
        for (File file : videoFiles) {
            if (file.lastModified() < cutoffTime) {
                if (file.delete()) {
                    deletedCount++;
                    System.out.println("🗑️ Deleted old video: " + file.getName());
                }
            }
        }

        return deletedCount;
    }

    /**
     * Get list of recent video files
     * 
     * @param limit Maximum number of files to return
     * @return Array of recent video files
     */
    public static File[] getRecentVideos(int limit) {
        File videoDir = new File(getVideoFolder());
        if (!videoDir.exists()) {
            return new File[0];
        }

        File[] videoFiles = videoDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4") ||
                name.toLowerCase().endsWith(".webm") ||
                name.toLowerCase().endsWith(".avi") ||
                name.toLowerCase().endsWith(".mov"));

        if (videoFiles == null || videoFiles.length == 0) {
            return new File[0];
        }

        // Sort by last modified time (newest first)
        Arrays.sort(videoFiles, Comparator.comparing(File::lastModified).reversed());

        // Return only the requested number of files
        if (videoFiles.length > limit) {
            return Arrays.copyOf(videoFiles, limit);
        }

        return videoFiles;
    }

    /**
     * Initialize video directory
     */
    public static void initializeVideoDirectory() {
        String videoFolder = getVideoFolder();
        File dir = new File(videoFolder);

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("✅ Created video directory: " + videoFolder);
            } else {
                System.err.println("❌ Failed to create video directory: " + videoFolder);
            }
        }
    }

    /**
     * Get video recording configuration summary
     * 
     * @return Configuration summary
     */
    public static String getConfigurationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("🎥 Video Recording Configuration:\n");
        summary.append("  - Enabled: ").append(isRecordingEnabled()).append("\n");
        summary.append("  - Folder: ").append(getVideoFolder()).append("\n");

        try {
            String format = ConfigReader.getProperty("video.recording.format");
            String framerate = ConfigReader.getProperty("video.recording.framerate");
            String quality = ConfigReader.getProperty("video.recording.quality");

            if (format != null)
                summary.append("  - Format: ").append(format).append("\n");
            if (framerate != null)
                summary.append("  - Frame Rate: ").append(framerate).append(" fps\n");
            if (quality != null)
                summary.append("  - Quality: ").append(quality).append("\n");
        } catch (Exception e) {
            summary.append("  - Additional config not available\n");
        }

        return summary.toString();
    }
}