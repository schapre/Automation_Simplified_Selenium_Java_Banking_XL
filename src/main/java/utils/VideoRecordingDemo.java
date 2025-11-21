package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo class to test the new WebM video recording functionality
 */
public class VideoRecordingDemo {
    private static final Logger logger = LoggerFactory.getLogger(VideoRecordingDemo.class);

    public static void main(String[] args) {
        try {
            System.out.println("🎬 Starting WebM Video Recording Demo...");

            // Check video recording configuration
            String config = VideoManager.getConfigurationSummary();
            System.out.println(config);

            // Start recording a demo
            String testName = "Demo_WebM_Recording";
            VideoManager.startRecording(testName);

            System.out.println("📹 Recording for 5 seconds...");

            // Simulate some activity for 5 seconds
            for (int i = 0; i < 5; i++) {
                System.out.println("  Frame capture in progress... " + (i + 1) + "/5");
                Thread.sleep(1000); // Wait 1 second
            }

            // Stop recording
            String videoPath = VideoManager.stopRecording(true);

            System.out.println("✅ Demo recording completed!");
            System.out.println("📁 Video location: " + videoPath);

            // Display video statistics
            String stats = VideoManager.getVideoStatistics();
            System.out.println("📊 " + stats);

            System.out.println("\n🎯 Next Steps:");
            System.out.println("1. Check the generated WebM file for conversion instructions");
            System.out.println("2. Look for the associated _frames directory");
            System.out.println("3. Install FFmpeg to convert frames to actual video");

        } catch (Exception e) {
            logger.error("Demo video recording failed", e);
            System.err.println("❌ Demo failed: " + e.getMessage());
        }
    }
}