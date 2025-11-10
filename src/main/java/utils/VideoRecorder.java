package utils;

/**
 * Video Recording facade class
 * Provides a simple interface for video recording operations
 * Now uses WebM video recorder for actual video file generation
 */
public class VideoRecorder {

    /**
     * Start video recording
     * 
     * @param testName Name of the test being recorded
     */
    public static void startRecording(String testName) {
        VideoRecorderWebM.startRecording(testName);
    }

    /**
     * Stop video recording
     * 
     * @return Path to the recorded video file
     */
    public static String stopRecording() {
        return VideoRecorderWebM.stopRecording();
    }

    /**
     * Check if recording is currently active
     * 
     * @return true if recording is active
     */
    public static boolean isRecording() {
        return VideoRecorderWebM.isRecording();
    }

    /**
     * Get current video name being recorded
     * 
     * @return Current video name
     */
    public static String getCurrentVideoName() {
        return VideoRecorderWebM.getCurrentVideoName();
    }

    /**
     * Get video folder path
     * 
     * @return Video folder path
     */
    public static String getVideoFolderPath() {
        return VideoRecorderWebM.getVideoFolderPath();
    }

    /**
     * Set custom video folder path
     * 
     * @param path Custom video folder path
     */
    public static void setVideoFolderPath(String path) {
        VideoRecorderWebM.setVideoFolderPath(path);
    }

    /**
     * Clean up old recording files
     * 
     * @param daysToKeep Number of days to keep recording files
     */
    public static void cleanupOldRecordings(int daysToKeep) {
        VideoRecorderWebM.cleanupOldRecordings(daysToKeep);
    }

    /**
     * Get recording start time
     * 
     * @return Recording start time in milliseconds
     */
    public static long getRecordingStartTime() {
        return VideoRecorderWebM.getRecordingStartTime();
    }
}