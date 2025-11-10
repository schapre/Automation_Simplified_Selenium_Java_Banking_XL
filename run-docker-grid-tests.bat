@echo off
echo ==========================================
echo   Docker Grid Parallel Execution
echo   Using Selenium Grid with Multiple Nodes
echo ==========================================

echo Step 1: Starting Selenium Grid infrastructure...
docker-compose up -d selenium-hub chrome-node firefox-node

echo Waiting for Grid to be ready...
timeout /t 10 >nul

echo Step 2: Running tests against Selenium Grid...
set SELENIUM_GRID_URL=http://localhost:4444/wd/hub
call mvn clean test -Dselenium.grid.url=%SELENIUM_GRID_URL% -Dparallel.execution=true

echo.
echo Step 3: Generating reports...
call mvn allure:report

echo.
echo Step 4: Cleaning up Docker containers...
docker-compose down

echo.
echo ==========================================
echo   Docker Grid Execution Complete!
echo ==========================================

REM Display results
if exist "test-output\SparkReport\Spark.html" (
    echo ✓ Spark Report: test-output\SparkReport\Spark.html
)

if exist "target\allure-report\index.html" (
    echo ✓ Allure Report: target\allure-report\index.html
)

echo.
echo Grid Configuration Used:
echo - Hub: 4444
echo - Chrome Nodes: 2 (4 sessions each)
echo - Firefox Nodes: 2 (4 sessions each)
echo - Max Concurrent Sessions: 16
echo.

echo Choose an option:
echo 1. Open Spark Report
echo 2. Open Allure Report
echo 3. Open Both Reports
echo 4. Check Docker Logs
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
    docker-compose logs
) else (
    echo Exiting...
)

echo.
pause