@echo off
echo ==========================================
echo   Database Testing Suite
echo   Multi-Platform Database Testing
echo ==========================================

REM Set default platform if not provided
if "%1"=="" (
    set PLATFORM=H2
    echo Default platform set to H2 for testing
) else (
    set PLATFORM=%1
    echo Platform set to %PLATFORM%
)

echo.
echo ==========================================
echo   Database Platform: %PLATFORM%
echo ==========================================

REM Validate platform parameter
if /I "%PLATFORM%"=="POSTGRESQL" goto :valid_platform
if /I "%PLATFORM%"=="MYSQL" goto :valid_platform
if /I "%PLATFORM%"=="H2" goto :valid_platform

echo ❌ Invalid platform: %PLATFORM%
echo Available platforms: POSTGRESQL, MYSQL, H2
pause
exit /b 1

:valid_platform

REM Set Maven properties based on platform
if /I "%PLATFORM%"=="POSTGRESQL" (
    set MAVEN_OPTS=-Ddb.platform=POSTGRESQL -Ddb.url=jdbc:postgresql://localhost:5432/testdb -Ddb.username=testuser -Ddb.password=testpass
)
if /I "%PLATFORM%"=="MYSQL" (
    set MAVEN_OPTS=-Ddb.platform=MYSQL -Ddb.url=jdbc:mysql://localhost:3306/testdb -Ddb.username=testuser -Ddb.password=testpass
)
if /I "%PLATFORM%"=="H2" (
    set MAVEN_OPTS=-Ddb.platform=H2 -Ddb.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE -Ddb.username=sa -Ddb.password=""
)

echo Maven Options: %MAVEN_OPTS%

echo.
echo Step 1: Compiling project...
call mvn clean compile test-compile

if %ERRORLEVEL% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ✅ Compilation successful!

echo.
echo Step 2: Running database tests...
echo Running tests with tags: @database

REM Run database tests with platform-specific properties
call mvn test -Dcucumber.filter.tags="@database" %MAVEN_OPTS% -Dtest.platform=DATABASE

if %ERRORLEVEL% neq 0 (
    echo ❌ Database tests failed!
    echo Check test reports for details
) else (
    echo ✅ Database tests completed successfully!
)

echo.
echo Step 3: Generating reports...

REM Generate Allure report
echo Generating Allure report...
call mvn allure:report

echo.
echo ==========================================
echo   Database Test Execution Complete!
echo ==========================================

REM Display test results summary
echo.
echo 📊 Test Results Summary:
if exist "target\surefire-reports\TEST-*.xml" (
    echo ✓ Surefire Reports: target\surefire-reports\
)

if exist "test-output\SparkReport\Spark.html" (
    echo ✓ Spark Report: test-output\SparkReport\Spark.html
)

if exist "target\allure-report\index.html" (
    echo ✓ Allure Report: target\allure-report\index.html
)

if exist "target\cucumber-reports" (
    echo ✓ Cucumber Reports: target\cucumber-reports\
)

echo.
echo 🗄️ Database Platform Used: %PLATFORM%
echo.

REM Check for test failures
if exist "target\surefire-reports\*FAILED*.txt" (
    echo ⚠️ Some tests failed. Check the reports for details.
    echo Failed test files:
    dir /b "target\surefire-reports\*FAILED*.txt"
)

echo.
echo Choose an action:
echo 1. Open Spark Report
echo 2. Open Allure Report
echo 3. Open All Reports
echo 4. View Surefire Reports
echo 5. Run API + Database Integration Tests
echo 6. Exit

set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" (
    if exist "test-output\SparkReport\Spark.html" (
        start "" "test-output\SparkReport\Spark.html"
    ) else (
        echo ❌ Spark report not found
    )
) else if "%choice%"=="2" (
    if exist "target\allure-report\index.html" (
        start "" "target\allure-report\index.html"
    ) else (
        echo ❌ Allure report not found
    )
) else if "%choice%"=="3" (
    if exist "test-output\SparkReport\Spark.html" (
        start "" "test-output\SparkReport\Spark.html"
    )
    timeout /t 2 >nul
    if exist "target\allure-report\index.html" (
        start "" "target\allure-report\index.html"
    )
) else if "%choice%"=="4" (
    start "" "target\surefire-reports\"
) else if "%choice%"=="5" (
    echo.
    echo Running API + Database Integration Tests...
    call mvn test -Dcucumber.filter.tags="@database or @api-integration" %MAVEN_OPTS% -Dtest.platform=DATABASE
    echo Integration tests completed!
) else (
    echo Exiting...
)

echo.
echo ==========================================
echo   Thank you for using Database Test Suite
echo ==========================================
pause