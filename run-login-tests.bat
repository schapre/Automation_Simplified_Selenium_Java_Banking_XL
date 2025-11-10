@echo off
echo Running Login Functionality Tests...
echo =====================================

mvn clean test "-Dcucumber.filter.tags=@LOGIN"

echo.
echo Test execution completed!
echo Check the reports in:
echo - target/cucumber-reports/
echo - test-output/
echo - target/allure-results/ (run 'allure serve target/allure-results' to view)

pause