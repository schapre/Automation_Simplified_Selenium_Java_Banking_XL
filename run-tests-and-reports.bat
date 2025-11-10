@echo off
echo ==========================================
echo   Multi-Report Generator
echo   Spark + Allure Reports + Video Recording
echo ==========================================

REM Create videos directory for video recording
if not exist "test-output\videos" (
    mkdir "test-output\videos"
    echo ✅ Video recording directory created
)

echo Step 1: Running tests with video recording...
call mvn clean test

echo.
echo Step 2: Generating reports...

REM Generate Allure report
echo Generating Allure report...
call mvn allure:report

echo.
echo ==========================================
echo   Report Generation Complete!
echo ==========================================

REM Check and display report locations
if exist "test-output\SparkReport\Spark.html" (
    echo ✓ Spark Report: test-output\SparkReport\Spark.html
)

if exist "test-output\HtmlReport\ExtentHtml.html" (
    echo ✓ HTML Report: test-output\HtmlReport\ExtentHtml.html
)

if exist "test-output\PdfReport\ExtentPdf.pdf" (
    echo ✓ PDF Report: test-output\PdfReport\ExtentPdf.pdf
)

if exist "target\allure-report\index.html" (
    echo ✓ Allure Report: target\allure-report\index.html
)

if exist "target\cucumber-reports\cucumber-html-report.html" (
    echo ✓ Cucumber Report: target\cucumber-reports\cucumber-html-report.html
)

if exist "test-output\videos" (
    echo ✓ Video Recordings: test-output\videos\ 
    for /d %%d in (test-output\videos\*) do (
        echo   - %%~nxd
    )
)

echo.
echo Choose which report to open:
echo 1. Spark Report
echo 2. Allure Report  
echo 3. All Reports
echo 4. Videos Directory
echo 5. Exit

set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" (
    start "" "test-output\SparkReport\Spark.html"
) else if "%choice%"=="2" (
    start "" "target\allure-report\index.html"
) else if "%choice%"=="3" (
    start "" "test-output\SparkReport\Spark.html"
    timeout /t 2 >nul
    start "" "target\allure-report\index.html"
) else if "%choice%"=="4" (
    start "" "test-output\videos\"
) else (
    echo Exiting...
)

echo.
pause