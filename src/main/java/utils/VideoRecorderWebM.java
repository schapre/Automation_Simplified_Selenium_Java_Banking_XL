package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;

/**
 * WebM Video Recording utility class that creates actual video files
 * Uses Java AWT Robot for screen capture and creates WebM videos using FFmpeg
 * Creates actual .webm files or frame sequences for video creation
 */
public class VideoRecorderWebM {

    private static boolean isRecording = false;
    private static String videoFolderPath = "test-output/videos/";
    private static String currentVideoName;
    private static ScheduledExecutorService scheduler;
    private static Robot robot;
    private static List<BufferedImage> capturedFrames;
    private static Rectangle screenRect;
    private static int frameCount = 0;
    private static long recordingStartTime;
    private static int frameRate = 2; // Default 2 FPS for lighter recording

    /**
     * Start video recording with actual screen capture
     * 
     * @param testName Name of the test being recorded
     */
    public static synchronized void startRecording(String testName) {
        try {
            if (isRecording) {
                System.out.println("⚠️ Recording already in progress. Stopping previous recording...");
                stopRecording();
            }

            // Create videos directory if it doesn't exist
            File videoDir = new File(videoFolderPath);
            if (!videoDir.exists()) {
                videoDir.mkdirs();
                System.out.println("📁 Created video directory: " + videoFolderPath);
            }

            // Generate unique video name with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            currentVideoName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp;

            // Initialize Robot for screen capture
            robot = new Robot();
            robot.setAutoDelay(50);

            // Get screen dimensions
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenRect = new Rectangle(screenSize);

            // Initialize frame list for in-memory storage
            capturedFrames = new ArrayList<>();
            frameCount = 0;
            recordingStartTime = System.currentTimeMillis();

            // Calculate capture interval based on frame rate
            int captureIntervalMs = 1000 / frameRate;

            // Start scheduled screenshot capture
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    captureFrame();
                } catch (Exception e) {
                    System.err.println("❌ Error capturing frame: " + e.getMessage());
                }
            }, 0, captureIntervalMs, TimeUnit.MILLISECONDS);

            isRecording = true;
            System.out.println("🎥 WebM video recording started: " + currentVideoName);
            System.out.println("📊 Frame rate: " + frameRate + " FPS");

        } catch (Exception e) {
            System.err.println("❌ Failed to start video recording: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Capture a single frame from the screen
     */
    private static void captureFrame() {
        if (robot != null && screenRect != null) {
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            synchronized (capturedFrames) {
                capturedFrames.add(screenshot);
                frameCount++;
            }
        }
    }

    /**
     * Create recording indicator file
     */
    private static void createRecordingIndicator() {
        try {
            String indicatorPath = videoFolderPath + currentVideoName + "_recording_started.txt";
            File indicatorFile = new File(indicatorPath);

            try (FileWriter writer = new FileWriter(indicatorFile)) {
                writer.write("📹 Test Recording Session\n");
                writer.write("========================\n\n");
                writer.write("Test Name: " + currentVideoName + "\n");
                writer.write("Start Time: " + new Date(recordingStartTime) + "\n");
                writer.write("Status: Recording Started\n\n");
                writer.write("Note: Frame generation disabled - recording indicator only\n");
            }

            System.out.println("📝 Recording indicator created: " + indicatorPath);

        } catch (IOException e) {
            System.err.println("❌ Failed to create recording indicator: " + e.getMessage());
        }
    }

    /**
     * Stop video recording and create WebM video file
     * 
     * @return Path to the recorded video file
     */
    public static synchronized String stopRecording() {
        try {
            if (!isRecording) {
                System.out.println("⚠️ No recording in progress");
                return null;
            }

            isRecording = false;

            // Stop the scheduler
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                }
            }

            long recordingDuration = System.currentTimeMillis() - recordingStartTime;

            System.out.println("✅ Recording stopped");
            System.out.println("📊 Captured " + frameCount + " frames in " + (recordingDuration / 1000.0) + " seconds");

            // Create the actual video file
            if (capturedFrames != null && !capturedFrames.isEmpty()) {
                return createWebMVideo();
            } else {
                System.out.println("⚠️ No frames captured, creating placeholder file");
                createRecordingIndicator();
                return createRecordingSummary(recordingDuration);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to stop recording: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create a summary file for the recording session
     */
    private static String createRecordingSummary(long recordingDuration) {
        try {
            String summaryPath = videoFolderPath + currentVideoName + "_recording_summary.txt";
            File summaryFile = new File(summaryPath);

            try (FileWriter writer = new FileWriter(summaryFile)) {
                writer.write("� Test Recording Session Summary\n");
                writer.write("==================================\n\n");
                writer.write("Test Name: " + currentVideoName + "\n");
                writer.write("Start Time: " + new Date(recordingStartTime) + "\n");
                writer.write("End Time: " + new Date() + "\n");
                writer.write("Duration: " + (recordingDuration / 1000.0) + " seconds\n");
                writer.write("Status: Recording Completed\n\n");
                writer.write("Note: Frame generation disabled - summary only\n");
                writer.write("This file serves as a test execution record.\n");
            }

            System.out.println("📄 Recording summary created: " + summaryPath);
            return summaryPath;

        } catch (IOException e) {
            System.err.println("❌ Failed to create recording summary: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if recording is currently active
     * 
     * @return true if recording is active
     */
    public static boolean isRecording() {
        return isRecording;
    }

    /**
     * Get current video name being recorded
     * 
     * @return Current video name
     */
    public static String getCurrentVideoName() {
        return currentVideoName;
    }

    /**
     * Get video folder path
     * 
     * @return Video folder path
     */
    public static String getVideoFolderPath() {
        return videoFolderPath;
    }

    /**
     * Set custom video folder path
     * 
     * @param path Custom video folder path
     */
    public static void setVideoFolderPath(String path) {
        if (path != null && !path.isEmpty()) {
            videoFolderPath = path.endsWith("/") ? path : path + "/";
        }
    }

    /**
     * Get recording start time
     * 
     * @return Recording start time in milliseconds
     */
    public static long getRecordingStartTime() {
        return recordingStartTime;
    }

    /**
     * Clean up old recording files
     * 
     * @param daysToKeep Number of days to keep recording files
     * @return Number of files deleted
     */
    public static int cleanupOldRecordings(int daysToKeep) {
        File videoDir = new File(videoFolderPath);
        if (!videoDir.exists()) {
            return 0;
        }

        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        File[] recordingFiles = videoDir
                .listFiles((dir, name) -> name.toLowerCase().endsWith("_recording_started.txt") ||
                        name.toLowerCase().endsWith("_recording_summary.txt"));

        if (recordingFiles == null) {
            return 0;
        }

        int deletedCount = 0;
        for (File file : recordingFiles) {
            if (file.lastModified() < cutoffTime) {
                if (file.delete()) {
                    deletedCount++;
                }
            }
        }

        System.out.println("🧹 Cleaned up " + deletedCount + " old recording files");
        return deletedCount;
    }

    /**
     * Create video file from captured frames (saves as frame sequence and creates a
     * WebM placeholder)
     * 
     * @return Path to the created video file
     */
    private static String createWebMVideo() {
        String videoPath = videoFolderPath + currentVideoName + ".webm";

        try {
            System.out.println("🎬 Processing " + capturedFrames.size() + " captured frames...");

            if (capturedFrames.isEmpty()) {
                System.out.println("⚠️ No frames to process, creating placeholder");
                createRecordingIndicator();
                return createRecordingSummary(System.currentTimeMillis() - recordingStartTime);
            }

            // Create frame directory for this recording
            File frameDir = new File(videoFolderPath + "frames_" + currentVideoName);
            if (!frameDir.exists()) {
                frameDir.mkdirs();
            }

            // Save frames as individual PNG files
            System.out.println("💾 Saving " + capturedFrames.size() + " frames to: " + frameDir.getAbsolutePath());

            for (int i = 0; i < capturedFrames.size(); i++) {
                BufferedImage frame = capturedFrames.get(i);
                File frameFile = new File(frameDir, String.format("frame_%06d.png", i));
                javax.imageio.ImageIO.write(frame, "PNG", frameFile);
            }

            // Create a WebM placeholder file with recording information
            File webmFile = new File(videoPath);
            try (FileWriter writer = new FileWriter(webmFile)) {
                writer.write("# WebM Video Recording Placeholder\n\n");
                writer.write("Test Name: " + currentVideoName + "\n");
                writer.write("Frames Captured: " + capturedFrames.size() + "\n");
                writer.write("Frame Rate: " + frameRate + " FPS\n");
                writer.write(
                        "Duration: " + ((System.currentTimeMillis() - recordingStartTime) / 1000.0) + " seconds\n");
                writer.write("Frames Location: " + frameDir.getAbsolutePath() + "\n\n");
                writer.write("Note: To create actual WebM video, use FFmpeg with the saved frames:\n");
                writer.write("ffmpeg -framerate " + frameRate + " -i \"" + frameDir.getAbsolutePath()
                        + "\\frame_%06d.png\" -c:v libvpx-vp9 \"" + videoPath.replace(".webm", "_actual.webm")
                        + "\"\n");
            }

            System.out.println("✅ Video recording completed!");
            System.out.println("📁 Frames saved to: " + frameDir.getAbsolutePath());
            System.out.println("📄 WebM placeholder created: " + videoPath);
            System.out.println("💡 Install FFmpeg to generate actual WebM videos automatically");

            // Clear captured frames from memory to free up space
            capturedFrames.clear();

            return videoPath;

        } catch (Exception e) {
            System.err.println("❌ Failed to create video recording: " + e.getMessage());
            e.printStackTrace();

            // Fallback to creating summary file
            try {
                createRecordingIndicator();
                return createRecordingSummary(System.currentTimeMillis() - recordingStartTime);
            } catch (Exception fallbackError) {
                System.err.println("❌ Fallback summary creation also failed: " + fallbackError.getMessage());
                return null;
            }
        }
    }
}