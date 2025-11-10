@echo off
echo 🎬 Setting up FFmpeg for WebM video recording...
echo.

REM Check if FFmpeg is already installed
where ffmpeg >nul 2>nul
if %ERRORLEVEL%==0 (
    echo ✅ FFmpeg is already installed and available in PATH
    ffmpeg -version | findstr "ffmpeg version"
    echo.
    goto :run_demo
)

echo 📥 FFmpeg not found in PATH. Setting up portable FFmpeg...

REM Create tools directory
if not exist "tools" mkdir tools
cd tools

REM Download FFmpeg (portable version)
echo 📥 Downloading FFmpeg portable version...
if not exist "ffmpeg.zip" (
    echo Please download FFmpeg from: https://github.com/BtbN/FFmpeg-Builds/releases
    echo Look for: "ffmpeg-master-latest-win64-gpl.zip"
    echo Save it as "tools/ffmpeg.zip" and run this script again
    echo.
    pause
    exit /b 1
)

REM Extract FFmpeg
echo 📦 Extracting FFmpeg...
if not exist "ffmpeg" (
    powershell -command "Expand-Archive -Path 'ffmpeg.zip' -DestinationPath '.'"
    for /d %%d in (ffmpeg-*) do ren "%%d" "ffmpeg"
)

REM Add to PATH for this session
set PATH=%CD%\ffmpeg\bin;%PATH%

cd ..

echo ✅ FFmpeg setup completed!
echo.

:run_demo
echo 🧪 Testing video recording with WebM output...
echo.

REM Run a simple test
mvn test -Dtest=*Login* -Dvideo.recording.enabled=true

echo.
echo 🎥 Check the test-output/videos/ directory for WebM files
echo.
echo 📋 To permanently add FFmpeg to PATH:
echo    1. Add this to your system PATH: %CD%\tools\ffmpeg\bin
echo    2. Or copy ffmpeg.exe to a directory already in PATH
echo.
pause