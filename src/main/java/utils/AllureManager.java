package utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for Allure reporting features
 */
public class AllureManager {

    private static final String SCREENSHOT_FOLDER = "test-output/screenshots";

    /**
     * Take screenshot for Allure report
     * 
     * @param driver WebDriver instance
     * @return Screenshot as byte array
     */
    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] takeScreenshotAsBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Take screenshot and attach to Allure report
     * Also saves screenshot to separate folder
     * 
     * @param driver WebDriver instance
     * @param name   Screenshot name
     */
    public static void attachScreenshot(WebDriver driver, String name) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Attach to Allure report
        Allure.addAttachment(name, new ByteArrayInputStream(screenshot));

        // Save to separate screenshots folder
        saveScreenshotToFile(screenshot, name);
    }

    /**
     * Save screenshot to file system in a separate folder
     * 
     * @param screenshot Screenshot as byte array
     * @param name       Screenshot name
     */
    private static void saveScreenshotToFile(byte[] screenshot, String name) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_FOLDER);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // Create timestamp for unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

            // Sanitize name for filename
            String sanitizedName = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");

            // Create screenshot file
            String fileName = sanitizedName + "_" + timestamp + ".png";
            File screenshotFile = new File(screenshotDir, fileName);

            // Write screenshot to file
            try (FileOutputStream fos = new FileOutputStream(screenshotFile)) {
                fos.write(screenshot);
            }

            System.out.println("📸 Screenshot saved: " + screenshotFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("❌ Failed to save screenshot to file: " + e.getMessage());
        }
    }

    /**
     * Attach text to Allure report
     * 
     * @param name    Attachment name
     * @param content Text content
     */
    @Attachment(value = "{name}", type = "text/plain")
    public static String attachText(String name, String content) {
        return content;
    }

    /**
     * Add step to Allure report
     * 
     * @param description Step description
     */
    @Step("{description}")
    public static void addStep(String description) {
        // This method is used for adding steps to Allure report
    }

    /**
     * Attach JSON response to Allure report
     * 
     * @param json JSON content
     */
    @Attachment(value = "JSON Response", type = "application/json")
    public static String attachJson(String json) {
        return json;
    }

    /**
     * Attach HTML content to Allure report
     * 
     * @param html HTML content
     */
    @Attachment(value = "HTML Source", type = "text/html")
    public static String attachHtml(String html) {
        return html;
    }

    /**
     * Add environment information
     * 
     * @param key   Environment key
     * @param value Environment value
     */
    public static void setEnvironmentInfo(String key, String value) {
        System.setProperty("allure.results.directory", "target/allure-results");
        // Environment info can be added via environment.properties file
    }

    /**
     * Attach video recording to Allure report
     * 
     * @param videoPath Path to the video file or frame folder
     * @param name      Video attachment name
     */
    public static void attachVideo(String videoPath, String name) {
        if (videoPath != null && !videoPath.isEmpty()) {
            try {
                java.io.File videoFile = new java.io.File(videoPath);

                // Check if it's a video file
                if (videoFile.exists() && videoFile.isFile() &&
                        (videoPath.endsWith(".webm") || videoPath.endsWith(".mp4") || videoPath.endsWith(".avi"))) {

                    // Attach actual video file
                    byte[] videoBytes = java.nio.file.Files.readAllBytes(videoFile.toPath());
                    attachVideoBytes(name, videoBytes);
                    System.out.println("✅ Video file attached to Allure report: " + name + " (" +
                            (videoBytes.length / 1024) + " KB)");

                } else if (videoFile.exists() && videoFile.isDirectory()) {
                    // Legacy: Handle frame folders
                    attachFrameFolder(videoFile, name);
                } else {
                    // Try to find video file by name pattern
                    java.io.File parentDir = videoFile.getParentFile();
                    if (parentDir != null && parentDir.exists()) {
                        java.io.File[] videoFiles = parentDir.listFiles((dir, fileName) -> fileName
                                .startsWith(videoFile.getName()) &&
                                (fileName.endsWith(".webm") || fileName.endsWith(".mp4") || fileName.endsWith(".avi")));

                        if (videoFiles != null && videoFiles.length > 0) {
                            // Use the first matching video file
                            byte[] videoBytes = java.nio.file.Files.readAllBytes(videoFiles[0].toPath());
                            attachVideoBytes(name, videoBytes);
                            System.out.println("✅ Video file attached to Allure report: " + name + " (" +
                                    (videoBytes.length / 1024) + " KB)");
                        } else {
                            System.err.println("❌ No video file found for: " + videoPath);
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ Failed to attach video: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Attach frame folder to Allure report (legacy support)
     * 
     * @param videoFolder Video frame folder
     * @param name        Video attachment name
     */
    private static void attachFrameFolder(java.io.File videoFolder, String name) {
        try {
            // Attach the recording summary
            java.io.File summaryFile = new java.io.File(videoFolder, "README.txt");
            if (!summaryFile.exists()) {
                summaryFile = new java.io.File(videoFolder, "recording_info.txt");
            }

            if (summaryFile.exists()) {
                String summaryContent = new String(java.nio.file.Files.readAllBytes(summaryFile.toPath()));
                attachText(name + " - Recording Info", summaryContent);
            }

            // Attach first and last frames as screenshots
            java.io.File[] frameFiles = videoFolder.listFiles((dir, fileName) -> fileName.endsWith(".png"));
            if (frameFiles != null && frameFiles.length > 0) {
                java.util.Arrays.sort(frameFiles);

                // Attach first frame
                attachImageFile(frameFiles[0], name + " - First Frame");

                // Attach last frame if different
                if (frameFiles.length > 1) {
                    attachImageFile(frameFiles[frameFiles.length - 1], name + " - Last Frame");
                }

                System.out.println("✅ Video frames attached to Allure report: " + name + " ("
                        + frameFiles.length + " frames)");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to attach frame folder: " + e.getMessage());
        }
    }

    /**
     * Attach an image file to Allure report
     * 
     * @param imageFile Image file to attach
     * @param name      Attachment name
     */
    private static void attachImageFile(java.io.File imageFile, String name) {
        try {
            byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(imageBytes), ".png");
        } catch (Exception e) {
            System.err.println("❌ Failed to attach image file: " + e.getMessage());
        }
    }

    /**
     * Attach video as byte array to Allure report
     * 
     * @param videoBytes Video content as byte array
     * @param name       Video attachment name
     */
    @Attachment(value = "{name}", type = "video/webm")
    public static byte[] attachVideoBytes(String name, byte[] videoBytes) {
        System.out.println("✅ Video bytes attached to Allure report: " + name);
        return videoBytes;
    }

    /**
     * Start video recording for current test
     * 
     * @param testName Name of the test
     */
    @Step("Starting video recording for test: {testName}")
    public static void startVideoRecording(String testName) {
        VideoRecorder.startRecording(testName);
    }

    /**
     * Stop video recording and attach to Allure report
     * 
     * @param attachToReport Whether to attach video to report
     * @return Path to the recorded video
     */
    @Step("Stopping video recording")
    public static String stopVideoRecording(boolean attachToReport) {
        String videoPath = VideoRecorder.stopRecording();

        if (attachToReport && videoPath != null) {
            String videoName = "Test_Recording_" + System.currentTimeMillis();
            attachVideo(videoPath, videoName);
        }

        return videoPath;
    }

    /**
     * Stop video recording and attach to Allure report (default: attach enabled)
     * 
     * @return Path to the recorded video
     */
    public static String stopVideoRecording() {
        return stopVideoRecording(true);
    }
}