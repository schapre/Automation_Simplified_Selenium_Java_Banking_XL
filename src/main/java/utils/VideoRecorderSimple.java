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
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Video Recording utility class using Java AWT Robot
 * Captures screen recordings as a series of images for test documentation
 * Note: This creates a series of PNG images that can be converted to video
 * later
 */
public class VideoRecorderSimple {
    private static final Logger logger = LoggerFactory.getLogger(VideoRecorderSimple.class);

    private static boolean isRecording = false;
    private static String videoFolderPath = "test-output/videos/";
    private static String currentVideoName;
    private static ScheduledExecutorService scheduler;
    private static Robot robot;
    private static List<String> capturedImages;
    private static Rectangle screenRect;
    private static int frameCount = 0;

    /**
     * Start video recording (captures screenshots at regular intervals)
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
            }

            // Generate unique video name with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            currentVideoName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp;

            // Initialize Robot for screen capture
            robot = new Robot();

            // Get screen dimensions
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenRect = new Rectangle(screenSize);

            // Initialize image list
            capturedImages = new ArrayList<>();
            frameCount = 0;

            // Create directory for this test's images
            File testVideoDir = new File(videoFolderPath + currentVideoName);
            if (!testVideoDir.exists()) {
                testVideoDir.mkdirs();
            }

            // Start scheduled screenshot capture
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    captureFrame();
                } catch (Exception e) {
                    System.err.println("❌ Error capturing frame: " + e.getMessage());
                }
            }, 0, 500, TimeUnit.MILLISECONDS); // Capture every 500ms (2 FPS)

            isRecording = true;
            System.out.println("✅ Video recording started: " + currentVideoName);

        } catch (Exception e) {
            logger.error("Failed to start video recording: {}", currentVideoName, e);
            System.err.println("❌ Failed to start video recording: " + e.getMessage());
        }
    }

    /**
     * Capture a single frame
     */
    private static void captureFrame() {
        try {
            BufferedImage screenCapture = robot.createScreenCapture(screenRect);
            String imagePath = videoFolderPath + currentVideoName + "/frame_" + String.format("%04d", frameCount)
                    + ".png";
            ImageIO.write(screenCapture, "PNG", new File(imagePath));
            capturedImages.add(imagePath);
            frameCount++;
        } catch (IOException e) {
            System.err.println("❌ Failed to capture frame: " + e.getMessage());
        }
    }

    /**
     * Stop video recording
     * 
     * @return Path to the recorded video folder
     */
    public static synchronized String stopRecording() {
        try {
            if (!isRecording) {
                System.out.println("⚠️ No recording in progress");
                return null;
            }

            if (scheduler != null) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }

            isRecording = false;

            String videoFolderName = videoFolderPath + currentVideoName;
            System.out.println("✅ Video recording stopped: " + frameCount + " frames captured");
            System.out.println("📁 Images saved in: " + videoFolderName);

            // Create a summary file
            createRecordingSummary(videoFolderName);

            return videoFolderName;

        } catch (Exception e) {
            logger.error("Failed to stop video recording: {}", currentVideoName, e);
            System.err.println("❌ Failed to stop video recording: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create a summary file for the recording
     */
    private static void createRecordingSummary(String videoFolder) {
        try {
            File summaryFile = new File(videoFolder + "/recording_info.txt");
            java.io.FileWriter writer = new java.io.FileWriter(summaryFile);
            writer.write("Video Recording Summary\n");
            writer.write("=====================\n\n");
            writer.write("Test Name: " + currentVideoName + "\n");
            writer.write("Recording Date: " + new Date() + "\n");
            writer.write("Total Frames: " + frameCount + "\n");
            writer.write("Frame Rate: ~2 FPS\n");
            writer.write("Duration: ~" + (frameCount * 0.5) + " seconds\n");
            writer.write("\nNote: This recording consists of PNG image frames.\n");
            writer.write("To convert to video, use tools like FFmpeg:\n");
            writer.write("ffmpeg -framerate 2 -i frame_%04d.png -c:v libx264 -pix_fmt yuv420p " + currentVideoName
                    + ".mp4\n");
            writer.close();
            System.out.println("📄 Recording summary created: " + summaryFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to create recording summary: " + e.getMessage());
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
        videoFolderPath = path;
    }

    /**
     * Clean up old video folders (keep only recent ones)
     * 
     * @param daysToKeep Number of days to keep video files
     */
    public static void cleanupOldVideos(int daysToKeep) {
        File videoDir = new File(videoFolderPath);
        if (!videoDir.exists()) {
            return;
        }

        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        File[] videoFolders = videoDir.listFiles(File::isDirectory);

        if (videoFolders != null) {
            for (File folder : videoFolders) {
                if (folder.lastModified() < cutoffTime) {
                    try {
                        deleteDirectory(folder);
                        System.out.println("🗑️ Cleaned up old video folder: " + folder.getName());
                    } catch (Exception e) {
                        System.err.println("❌ Failed to delete folder: " + folder.getName());
                    }
                }
            }
        }
    }

    /**
     * Delete a directory and its contents
     */
    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    /**
     * Get frame count for current recording
     * 
     * @return Current frame count
     */
    public static int getFrameCount() {
        return frameCount;
    }

    /**
     * Create a GIF from captured frames (simplified version)
     * Note: This is a basic implementation. For production use, consider using
     * external libraries
     */
    public static String createVideoSummary() {
        if (!isRecording && currentVideoName != null) {
            try {
                String summaryPath = videoFolderPath + currentVideoName + "/" + currentVideoName + "_summary.txt";
                File summaryFile = new File(summaryPath);

                if (!summaryFile.exists()) {
                    java.io.FileWriter writer = new java.io.FileWriter(summaryFile);
                    writer.write("Video Recording: " + currentVideoName + "\n");
                    writer.write("Frames captured: " + frameCount + "\n");
                    writer.write("Location: " + videoFolderPath + currentVideoName + "\n");
                    writer.close();
                }

                return summaryPath;
            } catch (IOException e) {
                System.err.println("❌ Failed to create video summary: " + e.getMessage());
            }
        }
        return null;
    }
}