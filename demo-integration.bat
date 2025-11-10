@echo off
echo ==========================================
echo   Spark + Allure Integration Demo
echo ==========================================
echo.
echo This demo will:
echo 1. Run a sample test
echo 2. Generate both Spark and Allure reports
echo 3. Show both reports side by side
echo.
pause

echo Running tests...
call mvn clean test -Dtest=TestRunner

echo.
echo Generating Allure reports...
call mvn allure:report

echo.
echo ==========================================
echo   Integration Complete! 
echo ==========================================
echo.
echo Reports generated:
echo ✓ Spark Report: test-output\SparkReport\Spark.html
echo ✓ Allure Report: target\allure-report\index.html
echo ✓ HTML Report: test-output\HtmlReport\ExtentHtml.html
echo ✓ PDF Report: test-output\PdfReport\ExtentPdf.pdf
echo.
echo Opening both reports...

if exist "test-output\SparkReport\Spark.html" (
    start "" "test-output\SparkReport\Spark.html"
    echo Spark Report opened
)

timeout /t 3 >nul

if exist "target\allure-report\index.html" (
    start "" "target\allure-report\index.html"
    echo Allure Report opened
)

echo.
echo ==========================================
echo   Demo Complete!
echo ==========================================
echo.
echo Key Features Integrated:
echo • Dual Reporting (Spark + Allure)
echo • Screenshot capture on failures
echo • Step-by-step execution details
echo • Environment information
echo • TestNG listener integration
echo • Cucumber integration with both reports
echo.
pause