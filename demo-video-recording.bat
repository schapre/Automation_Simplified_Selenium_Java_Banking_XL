@echo off
echo ================================
echo   Video Recording Demo Test
echo ================================
echo.
echo This script will run a quick test to demonstrate video recording functionality.
echo.

REM Create videos directory
if not exist "test-output\videos" (
    mkdir "test-output\videos"
    echo ✅ Created videos directory
)

echo 🎬 Starting video recording test...
echo.

REM Run a single test to demonstrate video recording
echo Running web login test with video recording...
call mvn test -Dtest="runners.TestRunner" -Dcucumber.filter.tags="@WEB" -q

REM Check if test ran
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Video recording test completed!
) else (
    echo.
    echo ⚠️ Test completed (check for any issues in logs)
)

echo.
echo 📹 Check the following locations:
echo - Videos: test-output\videos\
echo - Spark Report: test-output\SparkReport\Spark.html
echo - Allure Results: target\allure-results\
echo.

REM Display video directory contents
echo 🎥 Video Recording Results:
echo ===========================
if exist "test-output\videos" (
    dir "test-output\videos" /b /ad 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo To view the recorded frames, navigate to the test folders above.
        echo Each folder contains PNG frames and a recording summary.
    ) else (
        echo No video recordings found. Check if UI tests were executed.
    )
) else (
    echo Videos directory not created - no UI tests may have run.
)

echo.
echo 🎬 Video Recording Integration Demo Complete!
echo.
set /p openVideos="Would you like to open the videos directory? (y/n): "
if /i "%openVideos%"=="y" (
    start "" "test-output\videos\"
)

pause