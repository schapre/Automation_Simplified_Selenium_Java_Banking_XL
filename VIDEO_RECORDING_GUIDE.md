# 🎬 Video Recording Integration Guide

## Overview

Your automation framework now includes comprehensive **video recording capabilities** that automatically capture WebM videos during test execution. This enhancement provides visual documentation of test runs for better debugging and reporting.

## 🎥 Features

### Automatic Recording
- **Auto-start**: Video recording starts automatically for UI tests (Web/Mobile)
- **Auto-stop**: Recording stops after each test scenario
- **Auto-attach**: Videos are automatically attached to Allure reports

### Smart Integration
- **Conditional Recording**: Only records for UI-based tests (skips API-only tests)
- **Configurable**: Enable/disable recording via configuration
- **Cleanup**: Automatic cleanup of old video files

### Multiple Formats
- **Primary**: WebM format for web compatibility
- **Fallback**: AVI/MOV formats supported
- **Quality**: Configurable frame rate and quality settings

## 🚀 Quick Start

### 1. Run Tests with Video Recording

```bash
# Use the new video-enabled test runner
.\run-tests-with-video.bat

# Or run manually
mvn clean test
```

### 2. View Recorded Videos

Videos are automatically saved to:
- **Location**: `test-output/videos/`
- **Naming**: `TestName_YYYYMMDD_HHMMSS.webm`
- **Integration**: Attached to Allure reports

### 3. Configure Recording Settings

Edit `src/test/resources/config/config.properties`:

```properties
# Video Recording settings
video.recording.enabled=true
video.recording.folder=test-output/videos/
video.recording.format=webm
video.recording.framerate=15
video.recording.quality=1.0
```

## 🛠️ Configuration Options

### Basic Settings

| Property | Default | Description |
|----------|---------|-------------|
| `video.recording.enabled` | `true` | Enable/disable video recording |
| `video.recording.folder` | `test-output/videos/` | Directory to save videos |
| `video.recording.format` | `webm` | Video format (webm/avi/mov) |
| `video.recording.framerate` | `15` | Frames per second |
| `video.recording.quality` | `1.0` | Video quality (0.1-1.0) |

### Advanced Configuration

```java
// Programmatically control recording
VideoManager.setRecordingEnabled(false); // Disable recording
VideoManager.startRecording("CustomTest"); // Manual start
String videoPath = VideoManager.stopRecording(true); // Manual stop
```

## 📋 Available Scripts

### 1. Enhanced Test Runner

```bash
.\run-tests-with-video.bat
```

**Features:**
- Creates video directory
- Runs tests with video recording
- Generates all reports (Spark + Allure)
- Opens reports and video folder
- Provides execution summary

### 2. Original Test Runner (Updated)

```bash
.\run-tests-and-reports.bat
```

Now includes video recording functionality alongside existing reporting.

## 🔧 Video Management

### Cleanup Old Videos

```java
// Clean up videos older than 7 days
int deletedCount = VideoManager.cleanupOldVideos(7);
System.out.println("Cleaned up " + deletedCount + " old videos");
```

### Get Video Statistics

```java
String stats = VideoManager.getVideoStatistics();
System.out.println(stats);
// Output: Videos: 5 files, Total size: 45.67 MB
```

### Recent Videos

```java
File[] recentVideos = VideoManager.getRecentVideos(5);
for (File video : recentVideos) {
    System.out.println("Recent video: " + video.getName());
}
```

## 📊 Integration with Reports

### Allure Report Integration

Videos are automatically:
- **Attached** to test results
- **Viewable** directly in Allure reports
- **Linked** to specific test scenarios
- **Downloadable** from report interface

### Spark Report Enhancement

Screenshots continue to work alongside video recording:
- **Failure Screenshots**: Captured on test failures
- **Video Recordings**: Full test execution captured
- **Dual Documentation**: Both visual formats available

## 🎯 Best Practices

### 1. Selective Recording

```properties
# Enable only for critical test suites
video.recording.enabled=true
```

### 2. Storage Management

```java
// Regular cleanup in CI/CD pipelines
@AfterSuite
public void cleanup() {
    VideoManager.cleanupOldVideos(3); // Keep only 3 days
}
```

### 3. Performance Optimization

```properties
# Balance quality vs file size
video.recording.framerate=10
video.recording.quality=0.8
```

### 4. CI/CD Integration

```yaml
# Example Jenkins pipeline step
- name: Run Tests with Video
  run: |
    ./run-tests-with-video.bat
    
- name: Archive Videos
  uses: actions/upload-artifact@v3
  with:
    name: test-videos
    path: test-output/videos/
```

## 🚨 Troubleshooting

### Common Issues

**1. Video Not Recording**
- Check `video.recording.enabled=true` in config
- Verify UI tests are running (not API-only)
- Ensure proper screen permissions on Mac/Linux

**2. Large File Sizes**
- Reduce `video.recording.framerate`
- Lower `video.recording.quality`
- Implement regular cleanup

**3. Performance Impact**
- Recording may slightly slow test execution
- Consider disabling for smoke tests
- Enable only for detailed test runs

**4. File Format Issues**
- WebM is recommended for web reports
- AVI fallback for compatibility
- Check browser support for video playback

### Debug Information

```java
// Get configuration summary
String config = VideoManager.getConfigurationSummary();
System.out.println(config);
```

## 📈 Benefits

### Enhanced Debugging
- **Visual Evidence**: See exactly what happened during test failures
- **Step-by-Step**: Watch test execution in real-time
- **Error Context**: Understand failure points better

### Improved Reporting
- **Professional Documentation**: Videos enhance test reports
- **Stakeholder Communication**: Non-technical viewers can understand tests
- **Audit Trail**: Complete visual record of test execution

### Team Collaboration
- **Shared Understanding**: Videos clarify test behavior
- **Knowledge Transfer**: New team members learn faster
- **Issue Reproduction**: Easier to reproduce and fix bugs

## 🎉 Integration Complete!

Your automation framework now supports:

✅ **Automatic video recording** for UI tests  
✅ **WebM format** for optimal web compatibility  
✅ **Allure report integration** with video attachments  
✅ **Configurable settings** for flexible usage  
✅ **Smart cleanup** for storage management  
✅ **Enhanced debugging** with visual evidence  

### Next Steps

1. **Run your first test** with video recording
2. **Check the video output** in `test-output/videos/`
3. **View videos in Allure reports** for integrated experience
4. **Configure settings** based on your needs
5. **Implement cleanup strategies** for production use

---

**Happy Testing with Video Recording!** 🎬📹✨