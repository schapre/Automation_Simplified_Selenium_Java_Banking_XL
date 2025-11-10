@echo off
echo Running API Tests...
echo ==================

REM Run API tests using Maven 
mvn clean test

echo.
echo API Test execution completed.
echo Check target/allure-results for test results.
echo Check target/surefire-reports for TestNG results.
echo Run 'allure serve target/allure-results' to view the report.

pause