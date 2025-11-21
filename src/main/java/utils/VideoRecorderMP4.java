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
 * MP4 Video Recording utility class that creates actual MP4 video files
 * Uses Java AWT Robot for screen capture and FFmpeg for MP4 video generation
 * Automatically detects FFmpeg and creates MP4 videos
 */
public class VideoRecorderMP4 {

    private static final Logger logger = LoggerFactory.getLogger(VideoRecorderMP4.class);

    private static boolean isRecording = false;
    private static String videoFolderPath = "test-output/videos/";
    private static String currentVideoName;
    private static ScheduledExecutorService scheduler;
    private static Robot robot;
    private static List<BufferedImage> capturedFrames;
    private static Rectangle screenRect;
    private static int frameCount = 0;
    private static long recordingStartTime;
    private static int frameRate = 10; // 10 FPS for smoother playback
    private static boolean ffmpegAvailable = false;
    private static String ffmpegPath = "ffmpeg"; // Default assumes FFmpeg in PATH

    static {
        // Check FFmpeg availability at startup
        checkFFmpegAvailability();
    }

    /**
     * Check if FFmpeg is available on the system
     */
    private static void checkFFmpegAvailability() {
        try {
            ProcessBuilder pb = new ProcessBuilder(ffmpegPath, "-version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            ffmpegAvailable = (exitCode == 0);

            if (ffmpegAvailable) {
                logger.info("✅ FFmpeg detected and available for MP4 video recording");
            } else {
                logger.warn("⚠️ FFmpeg not found. Videos will be saved as frame sequences only.");
                logger.warn("💡 Install FFmpeg to enable automatic MP4 video generation");
            }
        } catch (Exception e) {
            ffmpegAvailable = false;
            logger.warn("⚠️ FFmpeg not detected: {}", e.getMessage());
            logger.warn("💡 Download FFmpeg from https://ffmpeg.org/download.html");
        }
    }

    /**
     * Set custom FFmpeg path
     * 
     * @param path Path to FFmpeg executable
     */
    public static void setFFmpegPath(String path) {
        ffmpegPath = path;
        checkFFmpegAvailability();
    }

    /**
     * Check if FFmpeg is available
     * 
     * @return true if FFmpeg is available
     */
    public static boolean isFFmpegAvailable() {
        return ffmpegAvailable;
    }

    /**
     * Start video recording with screen capture
     * 
     * @param testName Name of the test being recorded
     */
    public static synchronized void startRecording(String testName) {
        try {
            if (isRecording) {
                logger.warn("⚠️ Recording already in progress. Stopping previous recording...");
                stopRecording();
            }

            // Create videos directory if it doesn't exist
            File videoDir = new File(videoFolderPath);
            if (!videoDir.exists()) {
                videoDir.mkdirs();
                logger.info("📁 Created video directory: {}", videoFolderPath);
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
                    logger.error("❌ Error capturing frame: {}", e.getMessage(), e);
                }
            }, 0, captureIntervalMs, TimeUnit.MILLISECONDS);

            isRecording = true;
            logger.info("🎥 MP4 video recording started: {}", currentVideoName);
            logger.info("📊 Frame rate: {} FPS", frameRate);
            logger.info("📺 Screen resolution: {}x{}", screenRect.width, screenRect.height);

        } catch (Exception e) {
            logger.error("❌ Failed to start video recording: {}", e.getMessage(), e);
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

                // Log progress every 50 frames
                if (frameCount % 50 == 0) {
                    logger.debug("📹 Captured {} frames...", frameCount);
                }
            }
        }
    }

    /**
     * Stop video recording and create MP4 video file
     * 
     * @return Path to the recorded video file
     */
    public static synchronized String stopRecording() {
        try {
            if (!isRecording) {
                logger.warn("⚠️ No recording in progress");
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
                    Thread.currentThread().interrupt();
                }
            }

            long recordingDuration = System.currentTimeMillis() - recordingStartTime;

            logger.info("✅ Recording stopped");
            logger.info("📊 Captured {} frames in {:.2f} seconds", frameCount, recordingDuration / 1000.0);

            // Create the actual video file
            if (capturedFrames != null && !capturedFrames.isEmpty()) {
                return createMP4Video();
            } else {
                logger.warn("⚠️ No frames captured, cannot create video");
                return null;
            }

        } catch (Exception e) {
            logger.error("❌ Failed to stop recording: {}", e.getMessage(), e);
            return null;
        } finally {
            // Clear frames from memory
            if (capturedFrames != null) {
                capturedFrames.clear();
            }
        }
    }

    /**
     * Create MP4 video file from captured frames using FFmpeg
     * 
     * @return Path to the created video file
     */
    private static String createMP4Video() {
        String videoPath = videoFolderPath + currentVideoName + ".mp4";
        File frameDir = null;

        try {
            logger.info("🎬 Processing {} captured frames for MP4 video...", capturedFrames.size());

            if (capturedFrames.isEmpty()) {
                logger.warn("⚠️ No frames to process");
                return null;
            }

            // Create temporary frame directory for this recording
            frameDir = new File(videoFolderPath + "temp_frames_" + currentVideoName);
            if (!frameDir.exists()) {
                frameDir.mkdirs();
            }

            // Save frames as individual PNG files
            logger.info("💾 Saving {} frames to temporary directory...", capturedFrames.size());

            for (int i = 0; i < capturedFrames.size(); i++) {
                BufferedImage frame = capturedFrames.get(i);
                File frameFile = new File(frameDir, String.format("frame_%06d.png", i));
                ImageIO.write(frame, "PNG", frameFile);

                // Log progress for large frame counts
                if ((i + 1) % 100 == 0) {
                    logger.debug("💾 Saved {}/{} frames", i + 1, capturedFrames.size());
                }
            }

            logger.info("✅ All frames saved to: {}", frameDir.getAbsolutePath());

            // If FFmpeg is available, create actual MP4 video
            if (ffmpegAvailable) {
                logger.info("🎥 Creating MP4 video using FFmpeg...");

                // Build FFmpeg command
                String inputPattern = frameDir.getAbsolutePath() + File.separator + "frame_%06d.png";

                ProcessBuilder pb = new ProcessBuilder(
                        ffmpegPath,
                        "-y", // Overwrite output file if exists
                        "-framerate", String.valueOf(frameRate),
                        "-i", inputPattern,
                        "-c:v", "libx264", // H.264 codec for MP4
                        "-pix_fmt", "yuv420p", // Compatible pixel format
                        "-preset", "fast", // Encoding speed preset
                        "-crf", "23", // Quality (lower is better, 23 is default)
                        videoPath);

                pb.redirectErrorStream(true);
                Process process = pb.start();

                // Wait for FFmpeg to complete
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    File videoFile = new File(videoPath);
                    if (videoFile.exists() && videoFile.length() > 0) {
                        logger.info("✅ MP4 video created successfully!");
                        logger.info("📁 Video path: {}", videoPath);
                        logger.info("📊 File size: {:.2f} MB", videoFile.length() / (1024.0 * 1024.0));

                        // Clean up temporary frames
                        deleteDirectory(frameDir);
                        logger.debug("🧹 Temporary frames cleaned up");

                        return videoPath;
                    } else {
                        logger.error("❌ Video file was not created or is empty");
                    }
                } else {
                    logger.error("❌ FFmpeg failed with exit code: {}", exitCode);
                }
            } else {
                // FFmpeg not available - keep frames and create instruction file
                logger.warn("⚠️ FFmpeg not available - frames saved for manual conversion");
                createConversionInstructions(frameDir.getAbsolutePath(), videoPath);
                logger.info("📄 Conversion instructions created: {}", videoPath.replace(".mp4", "_instructions.txt"));

                return frameDir.getAbsolutePath();
            }

        } catch (Exception e) {
            logger.error("❌ Failed to create MP4 video: {}", e.getMessage(), e);
        }

        return null;
    }

    /**
     * Create instructions file for manual video conversion
     */
    private static void createConversionInstructions(String framePath, String videoPath) {
        try {
            String instructionsPath = videoPath.replace(".mp4", "_instructions.txt");
            File instructionsFile = new File(instructionsPath);

            try (java.io.FileWriter writer = new java.io.FileWriter(instructionsFile)) {
                writer.write("═════════════════════════════════════════════════════════\n");
                writer.write("   MP4 VIDEO CONVERSION INSTRUCTIONS\n");
                writer.write("═════════════════════════════════════════════════════════\n\n");
                writer.write("Test Name: " + currentVideoName + "\n");
                writer.write("Frames Location: " + framePath + "\n");
                writer.write("Frame Count: " + capturedFrames.size() + "\n");
                writer.write("Frame Rate: " + frameRate + " FPS\n");
                writer.write("Recording Duration: " + ((System.currentTimeMillis() - recordingStartTime) / 1000.0)
                        + " seconds\n\n");

                writer.write("─────────────────────────────────────────────────────────\n");
                writer.write("INSTALL FFMPEG:\n");
                writer.write("─────────────────────────────────────────────────────────\n");
                writer.write("1. Download FFmpeg from: https://ffmpeg.org/download.html\n");
                writer.write("2. Extract to C:\\ffmpeg\\\n");
                writer.write("3. Add C:\\ffmpeg\\bin to your system PATH\n");
                writer.write("4. Verify installation: ffmpeg -version\n\n");

                writer.write("─────────────────────────────────────────────────────────\n");
                writer.write("CONVERT TO MP4 VIDEO:\n");
                writer.write("─────────────────────────────────────────────────────────\n");
                writer.write("Open Command Prompt and run:\n\n");
                writer.write("cd \"" + new File(framePath).getParent() + "\"\n");
                writer.write("ffmpeg -framerate " + frameRate + " -i \"" + framePath + "\\frame_%06d.png\" ");
                writer.write("-c:v libx264 -pix_fmt yuv420p -preset fast -crf 23 \"" + videoPath + "\"\n\n");

                writer.write("─────────────────────────────────────────────────────────\n");
                writer.write("ALTERNATIVE OPTIONS:\n");
                writer.write("─────────────────────────────────────────────────────────\n");
                writer.write("High Quality (larger file):\n");
                writer.write("ffmpeg -framerate " + frameRate + " -i \"" + framePath + "\\frame_%06d.png\" ");
                writer.write("-c:v libx264 -pix_fmt yuv420p -preset slow -crf 18 \""
                        + videoPath.replace(".mp4", "_hq.mp4") + "\"\n\n");

                writer.write("Fast Compression (lower quality):\n");
                writer.write("ffmpeg -framerate " + frameRate + " -i \"" + framePath + "\\frame_%06d.png\" ");
                writer.write("-c:v libx264 -pix_fmt yuv420p -preset ultrafast -crf 28 \""
                        + videoPath.replace(".mp4", "_fast.mp4") + "\"\n\n");

                writer.write("═════════════════════════════════════════════════════════\n");
            }

            logger.info("📄 Conversion instructions saved: {}", instructionsPath);

        } catch (IOException e) {
            logger.error("❌ Failed to create instructions file: {}", e.getMessage(), e);
        }
    }

    /**
     * Delete directory and all its contents
     */
    private static void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }

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
     * Set frame rate for video recording
     * 
     * @param fps Frames per second (1-30)
     */
    public static void setFrameRate(int fps) {
        if (fps > 0 && fps <= 30) {
            frameRate = fps;
            logger.info("📊 Frame rate set to: {} FPS", frameRate);
        } else {
            logger.warn("⚠️ Invalid frame rate: {}. Must be between 1 and 30", fps);
        }
    }

    /**
     * Get current frame rate
     * 
     * @return Current frame rate
     */
    public static int getFrameRate() {
        return frameRate;
    }

    /**
     * Get number of captured frames
     * 
     * @return Frame count
     */
    public static int getFrameCount() {
        return frameCount;
    }

    /**
     * Clean up old video files
     * 
     * @param daysToKeep Number of days to keep video files
     * @return Number of files deleted
     */
    public static int cleanupOldVideos(int daysToKeep) {
        File videoDir = new File(videoFolderPath);
        if (!videoDir.exists()) {
            return 0;
        }

        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        File[] videoFiles = videoDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4") ||
                name.toLowerCase().endsWith("_instructions.txt"));

        if (videoFiles == null) {
            return 0;
        }

        int deletedCount = 0;
        for (File file : videoFiles) {
            if (file.lastModified() < cutoffTime) {
                if (file.delete()) {
                    deletedCount++;
                }
            }
        }

        logger.info("🧹 Cleaned up {} old video files", deletedCount);
        return deletedCount;
    }
}
