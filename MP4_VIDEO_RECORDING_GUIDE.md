# MP4 Video Recording Guide

## Overview
This framework now supports **automatic MP4 video recording** for all test cases using the new `VideoRecorderMP4` utility. Videos are recorded in industry-standard MP4 format with H.264 codec, ensuring compatibility with all major video players and CI/CD platforms.

## Key Features

✅ **MP4 Format**: Standard H.264 codec for universal compatibility  
✅ **Automatic Recording**: Captures all UI test executions  
✅ **FFmpeg Integration**: Automatic video generation when FFmpeg is available  
✅ **Configurable Frame Rate**: Default 10 FPS, adjustable from 1-30 FPS  
✅ **Memory Efficient**: Automatic cleanup of temporary frames  
✅ **Fallback Support**: Saves frames with conversion instructions if FFmpeg unavailable  
✅ **Smart Detection**: Automatically checks for FFmpeg availability  

## Architecture

### VideoRecorderMP4 Class
Location: `src/main/java/utils/VideoRecorderMP4.java`

The core MP4 recording engine that:
- Captures screen frames using Java AWT Robot
- Stores frames in memory during recording
- Uses FFmpeg to create MP4 videos with H.264 codec
- Provides fallback frame sequence if FFmpeg is unavailable

### VideoRecorder Facade
Location: `src/main/java/utils/VideoRecorder.java`

Simplified interface that delegates to VideoRecorderMP4:
```java
VideoRecorder.startRecording(testName);
String videoPath = VideoRecorder.stopRecording();
```

### Hooks Integration
Location: `src/test/java/hooks/Hooks.java`

Automatically starts/stops recording for all UI tests (Web and Mobile).

## FFmpeg Setup

### Option 1: Windows Installation (Recommended)

1. **Download FFmpeg**:
   ```powershell
   # Download from official site
   # Visit: https://ffmpeg.org/download.html#build-windows
   ```

2. **Extract and Configure**:
   ```powershell
   # Extract to C:\ffmpeg
   # Add C:\ffmpeg\bin to System PATH
   ```

3. **Verify Installation**:
   ```powershell
   ffmpeg -version
   ```

### Option 2: Using Chocolatey (Windows)

```powershell
# Install Chocolatey if not already installed
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install FFmpeg
choco install ffmpeg
```

### Option 3: Using Scoop (Windows)

```powershell
# Install Scoop if not already installed
iwr -useb get.scoop.sh | iex

# Install FFmpeg
scoop install ffmpeg
```

### Verification

After installation, verify FFmpeg is accessible:

```powershell
ffmpeg -version
```

You should see output like:
```
ffmpeg version 6.0
built with gcc ...
configuration: ...
```

## Usage

### Automatic Recording (Default)

Video recording is **automatically enabled** for all UI tests. No code changes required!

```java
@Test
public void testLoginFunctionality() {
    // Video recording automatically starts
    driver.get("https://example.com");
    // ... test steps ...
    // Video automatically stops after test
}
```

### Manual Control

For custom recording scenarios:

```java
// Start recording
VideoRecorder.startRecording("CustomTest_" + timestamp);

// Perform test actions
driver.get("https://example.com");
loginPage.login("user", "password");

// Stop recording and get video path
String videoPath = VideoRecorder.stopRecording();
System.out.println("Video saved: " + videoPath);
```

### Configuration

#### Set Frame Rate

Higher frame rate = smoother video but larger file size:

```java
// In test setup or configuration
VideoRecorder.setFrameRate(15); // 15 FPS (default is 10 FPS)
```

**Frame Rate Guidelines**:
- **5 FPS**: Minimal, for quick actions only
- **10 FPS** (Default): Good balance for most tests
- **15 FPS**: Smoother playback, recommended for demo videos
- **20-30 FPS**: Very smooth, larger files

#### Custom FFmpeg Path

If FFmpeg is not in system PATH:

```java
VideoRecorder.setFFmpegPath("C:\\custom\\path\\to\\ffmpeg.exe");
```

#### Check FFmpeg Availability

```java
if (VideoRecorder.isFFmpegAvailable()) {
    System.out.println("✅ FFmpeg available - MP4 videos will be created");
} else {
    System.out.println("⚠️ FFmpeg not found - frames will be saved for manual conversion");
}
```

## Video Output

### With FFmpeg Installed

When FFmpeg is available:

```
test-output/videos/
├── LoginTest_20251121_160530.mp4 (✅ Actual MP4 video)
├── RegistrationTest_20251121_160645.mp4
└── CheckoutTest_20251121_160823.mp4
```

**Features**:
- ✅ Ready-to-play MP4 videos
- ✅ H.264 codec (universal compatibility)
- ✅ Optimized file size (CRF 23 quality)
- ✅ Automatic frame cleanup after video creation

### Without FFmpeg

When FFmpeg is not available:

```
test-output/videos/
├── temp_frames_LoginTest_20251121_160530/
│   ├── frame_000001.png
│   ├── frame_000002.png
│   └── ... (all captured frames)
└── LoginTest_20251121_160530_instructions.txt (📄 Conversion guide)
```

The instructions file contains:
- FFmpeg installation steps
- Exact command to convert frames to MP4
- Alternative conversion options (quality/speed)

## Video Quality Settings

The framework uses optimized settings for test recordings:

| Parameter | Value | Description |
|-----------|-------|-------------|
| **Codec** | H.264 (libx264) | Universal compatibility |
| **Pixel Format** | yuv420p | Compatible with all players |
| **CRF** | 23 | Quality (lower = better, 18-28 range) |
| **Preset** | fast | Encoding speed |
| **Frame Rate** | 10 FPS (default) | Configurable 1-30 FPS |

### Quality Comparison

| CRF Value | Quality | File Size | Use Case |
|-----------|---------|-----------|----------|
| 18 | Excellent | Large | Important demos |
| 23 | Good (Default) | Medium | Standard test recording |
| 28 | Acceptable | Small | Long test runs |

To change quality, modify the CRF value in `VideoRecorderMP4.java`:

```java
// In createMP4Video() method, change:
"-crf", "23", // Default quality
// To:
"-crf", "18", // Higher quality
```

## Integration with Reporting

### Allure Reports

Videos are automatically attached to Allure reports:

```java
// Automatic attachment in Hooks.java
if (videoPath != null && !videoPath.isEmpty()) {
    AllureManager.attachVideo(videoPath, scenario.getName());
}
```

### Extent Reports

Videos can be embedded in Extent reports:

```java
String videoPath = VideoRecorder.stopRecording();
if (videoPath != null) {
    extentTest.addScreenCaptureFromPath(videoPath, "Test Video");
}
```

## Performance Considerations

### Memory Usage

- **During Recording**: ~50-100 MB for 30-second test (10 FPS)
- **Frame Storage**: Temporary, cleared after video creation
- **Final MP4**: ~2-5 MB per minute (varies with screen size)

### Frame Rate Impact

| FPS | Frames/Minute | Memory Usage | File Size |
|-----|---------------|--------------|-----------|
| 5   | 300           | ~25 MB       | ~1 MB/min |
| 10  | 600           | ~50 MB       | ~2 MB/min |
| 15  | 900           | ~75 MB       | ~3 MB/min |
| 30  | 1800          | ~150 MB      | ~6 MB/min |

### Optimization Tips

1. **Use appropriate frame rate**: 10 FPS is sufficient for most tests
2. **Clean up old videos regularly**: Use `VideoManager.cleanupOldVideos(7)` to keep last 7 days
3. **Disable for API tests**: Recording is auto-disabled for non-UI tests
4. **Run on CI with FFmpeg**: Install FFmpeg in CI environment for automatic MP4 generation

## CI/CD Integration

### GitHub Actions

```yaml
name: Test with Video Recording

jobs:
  test:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Install FFmpeg
        run: choco install ffmpeg -y
      
      - name: Run Tests
        run: mvn clean test
      
      - name: Upload Videos
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-videos
          path: test-output/videos/**/*.mp4
```

### Jenkins

```groovy
pipeline {
    agent any
    stages {
        stage('Setup') {
            steps {
                // Install FFmpeg if not available
                bat 'choco install ffmpeg -y'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn clean test'
            }
        }
        stage('Archive Videos') {
            steps {
                archiveArtifacts artifacts: 'test-output/videos/**/*.mp4', allowEmptyArchive: true
            }
        }
    }
}
```

### Azure DevOps

```yaml
steps:
- task: PowerShell@2
  inputs:
    targetType: 'inline'
    script: 'choco install ffmpeg -y'
  displayName: 'Install FFmpeg'

- task: Maven@3
  inputs:
    goals: 'clean test'
  displayName: 'Run Tests'

- task: PublishBuildArtifacts@1
  inputs:
    pathToPublish: 'test-output/videos'
    artifactName: 'test-videos'
  condition: always()
```

## Troubleshooting

### Issue: "FFmpeg not detected"

**Solution**: 
1. Install FFmpeg using one of the methods above
2. Verify with `ffmpeg -version`
3. Restart terminal/IDE after installation
4. Check system PATH includes FFmpeg bin directory

### Issue: "Video file not created"

**Possible Causes**:
- FFmpeg not in PATH
- Insufficient disk space
- No frames captured (very short test)

**Solution**:
```java
// Check FFmpeg availability
if (!VideoRecorder.isFFmpegAvailable()) {
    System.out.println("Install FFmpeg for automatic video creation");
}

// Check recording status
if (VideoRecorderMP4.isRecording()) {
    System.out.println("Recording in progress...");
}
```

### Issue: "Video quality is poor"

**Solution**: Increase frame rate or adjust quality:
```java
// Higher frame rate for smoother playback
VideoRecorder.setFrameRate(15); // Or 20/30

// For higher quality (in VideoRecorderMP4.java):
// Change CRF from 23 to 18
```

### Issue: "Large video files"

**Solution**: Reduce frame rate or increase compression:
```java
// Lower frame rate
VideoRecorder.setFrameRate(5); // Minimal recording

// For smaller files (in VideoRecorderMP4.java):
// Change CRF from 23 to 28
```

### Issue: "Out of memory during recording"

**Solution**:
1. Reduce frame rate: `VideoRecorder.setFrameRate(5)`
2. Increase JVM heap: `mvn test -DargLine="-Xmx2g"`
3. Record shorter tests
4. Enable video only for failed tests

## Best Practices

### 1. Selective Recording

Record only when needed:

```java
@Test
public void criticalTest() {
    VideoRecorder.startRecording("CriticalTest");
    try {
        // Test steps
    } finally {
        String video = VideoRecorder.stopRecording();
        // Attach only if test fails
        if (testFailed) {
            attachVideoToReport(video);
        }
    }
}
```

### 2. Naming Convention

Use descriptive video names:

```java
String testName = String.format("%s_%s_%s", 
    testClass.getSimpleName(),
    testMethod.getName(),
    timestamp
);
VideoRecorder.startRecording(testName);
```

### 3. Regular Cleanup

Clean old videos periodically:

```java
@AfterSuite
public void cleanupOldVideos() {
    int deleted = VideoRecorderMP4.cleanupOldVideos(7); // Keep last 7 days
    System.out.println("Cleaned up " + deleted + " old videos");
}
```

### 4. CI Environment Detection

Adjust settings for CI:

```java
boolean isCIEnvironment = System.getenv("CI") != null;
if (isCIEnvironment) {
    VideoRecorder.setFrameRate(5); // Lower FPS for CI
}
```

## API Reference

### VideoRecorder (Facade)

| Method | Description |
|--------|-------------|
| `startRecording(String testName)` | Start recording with test name |
| `stopRecording()` | Stop and create MP4 video |
| `isFFmpegAvailable()` | Check if FFmpeg is detected |
| `setFFmpegPath(String path)` | Set custom FFmpeg path |
| `setFrameRate(int fps)` | Set frame rate (1-30 FPS) |

### VideoRecorderMP4 (Core)

| Method | Description |
|--------|-------------|
| `startRecording(String testName)` | Start screen capture |
| `stopRecording()` | Stop and generate MP4 |
| `isRecording()` | Check if currently recording |
| `getFrameCount()` | Get number of captured frames |
| `getVideoFolderPath()` | Get output directory |
| `setVideoFolderPath(String path)` | Set output directory |
| `cleanupOldVideos(int days)` | Remove old videos |

### VideoManager (Management)

| Method | Description |
|--------|-------------|
| `setRecordingEnabled(boolean)` | Enable/disable globally |
| `isRecordingEnabled()` | Check if enabled |
| `getVideoStatistics()` | Get storage statistics |
| `cleanupOldVideos(int days)` | Clean old videos |
| `getRecentVideos(int limit)` | Get recent video files |

## Configuration Properties

Add to `config.properties`:

```properties
# Video Recording Configuration
video.recording.enabled=true
video.recording.folder=test-output/videos/
video.recording.format=mp4
video.recording.framerate=10
video.recording.ffmpeg.path=ffmpeg
```

## Comparison: WebM vs MP4

| Feature | WebM (Old) | MP4 (New) | 
|---------|------------|-----------|
| **Format** | VP9 codec | H.264 codec |
| **Compatibility** | Limited | Universal ✅ |
| **File Size** | Smaller | Medium |
| **Quality** | Good | Excellent ✅ |
| **CI/CD Support** | Limited | Excellent ✅ |
| **Player Support** | Chrome/Firefox | All players ✅ |
| **Report Integration** | Limited | Excellent ✅ |

## Summary

The MP4 video recording implementation provides:

✅ **Automatic recording** for all UI test cases  
✅ **Universal MP4 format** with H.264 codec  
✅ **FFmpeg integration** with automatic detection  
✅ **Configurable quality** and frame rate  
✅ **Memory efficient** with automatic cleanup  
✅ **CI/CD ready** with simple FFmpeg installation  
✅ **Fallback support** when FFmpeg unavailable  
✅ **Report integration** with Allure and Extent  

**Next Steps**:
1. Install FFmpeg using the guide above
2. Run your tests - videos will be created automatically
3. Check `test-output/videos/` for MP4 files
4. Integrate with your reporting framework

For questions or issues, refer to the Troubleshooting section or check the logs for detailed recording information.
