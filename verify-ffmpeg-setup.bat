@echo off
REM Test FFmpeg Installation and MP4 Video Recording
echo =========================================
echo FFmpeg Installation Verification
echo =========================================
echo.

REM Check if FFmpeg is accessible
echo Checking FFmpeg installation...
C:\ffmpeg\bin\ffmpeg.exe -version | findstr /C:"ffmpeg version"
if %ERRORLEVEL% == 0 (
    echo.
    echo [SUCCESS] FFmpeg is installed and accessible!
    echo.
) else (
    echo.
    echo [ERROR] FFmpeg not found or not working properly
    exit /b 1
)

echo =========================================
echo Running URLManager Tests
echo =========================================
echo.
call mvn test -Dtest=URLManagerTest

echo.
echo =========================================
echo FFmpeg Setup Complete!
echo =========================================
echo.
echo Next Steps:
echo 1. FFmpeg is installed at: C:\ffmpeg\bin\
echo 2. FFmpeg is added to your PATH
echo 3. Run your UI tests to generate MP4 videos
echo 4. Videos will be saved in: test-output\videos\
echo.
echo Example:
echo   mvn clean test
echo.
echo Note: You may need to restart your IDE or terminal
echo for the PATH changes to take full effect.
echo.
pause
