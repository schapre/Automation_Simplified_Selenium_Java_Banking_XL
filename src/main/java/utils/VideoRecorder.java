package utils;

/**
 * Video Recording facade class
 * Provides a simple interface for video recording operations
 * Now uses MP4 video recorder for actual video file generation with FFmpeg
 */
public class VideoRecorder {

    /**
     * Start video recording in MP4 format
     * 
     * @param testName Name of the test being recorded
     */
    public static void startRecording(String testName) {
        VideoRecorderMP4.startRecording(testName);
    }

    /**
     * Stop video recording and generate MP4 file
     * 
     * @return Path to the recorded video file
     */
    public static String stopRecording() {
        return VideoRecorderMP4.stopRecording();
    }

    /**
     * Check if FFmpeg is available for video generation
     * 
     * @return true if FFmpeg is available
     */
    public static boolean isFFmpegAvailable() {
        return VideoRecorderMP4.isFFmpegAvailable();
    }

    /**
     * Set custom FFmpeg path
     * 
     * @param path Path to FFmpeg executable
     */
    public static void setFFmpegPath(String path) {
        VideoRecorderMP4.setFFmpegPath(path);
    }

    /**
     * Set frame rate for video recording
     * 
     * @param fps Frames per second (1-30)
     */
    public static void setFrameRate(int fps) {
        VideoRecorderMP4.setFrameRate(fps);
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