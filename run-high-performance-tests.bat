@echo off
echo ==========================================
echo   High-Performance Parallel Execution
echo   Using testng-parallel.xml (6 threads)
echo ==========================================

REM Create directories for parallel execution reports
if not exist "test-output\parallel" (
    mkdir "test-output\parallel"
    echo ✅ Parallel test output directory created
)

if not exist "test-output\videos" (
    mkdir "test-output\videos"
    echo ✅ Video recording directory created
)

echo Step 1: Running tests with maximum parallelization...
echo ⚡ Using testng-parallel.xml configuration (6 threads)
call mvn clean test -DsuiteXmlFile=testng-parallel.xml

echo.
echo Step 2: Generating reports...

REM Generate Allure report
echo Generating Allure report...
call mvn allure:report

echo.
echo ==========================================
echo   High-Performance Execution Complete!
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

if exist "test-output\videos" (
    echo ✓ Video Recordings: test-output\videos\
)

echo.
echo Performance Stats:
echo - Thread Count: 6
echo - Data Provider Threads: 3
echo - Maven Fork Count: 2
echo - Parallel Level: Methods
echo.

echo Choose an option:
echo 1. Open Spark Report
echo 2. Open Allure Report
echo 3. Open Both Reports
echo 4. Open Video Folder
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