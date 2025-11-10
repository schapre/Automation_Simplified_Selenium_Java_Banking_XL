@echo off
echo ================================
echo   Video Recording Test Runner
echo ================================
echo.
echo Starting test execution with video recording...
echo.

REM Create videos directory
if not exist "test-output\videos" (
    mkdir "test-output\videos"
    echo ✅ Created videos directory
)

REM Clean up old videos (optional - keep only last 7 days)
echo 🧹 Cleaning up old videos...

REM Run Maven tests
echo.
echo 🚀 Running tests with video recording...
call mvn clean test -Dmaven.test.failure.ignore=true

REM Check if tests ran successfully
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Tests completed successfully!
) else (
    echo.
    echo ⚠️ Tests completed with some failures - check reports for details
)

REM Generate Allure report
echo.
echo 📊 Generating Allure report...
call mvn allure:report

REM Open reports
echo.
echo 📁 Opening reports...
echo.
echo Available reports:
echo - Spark Report: test-output\SparkReport\Spark.html
echo - Allure Report: target\allure-report\index.html
echo - Videos: test-output\videos\
echo.

REM Ask user if they want to open reports
set /p openReports="Would you like to open the reports now? (y/n): "
if /i "%openReports%"=="y" (
    start "" "test-output\SparkReport\Spark.html"
    start "" "target\allure-report\index.html"
    start "" "test-output\videos\"
)

echo.
echo 🎬 Test execution with video recording completed!
echo Videos are saved in: test-output\videos\
echo.
pause