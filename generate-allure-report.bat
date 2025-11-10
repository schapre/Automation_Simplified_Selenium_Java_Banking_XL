@echo off
echo ==========================================
echo   Generating Allure Reports
echo ==========================================

REM Clean previous reports
if exist "target\allure-report" (
    echo Cleaning previous Allure reports...
    rmdir /s /q "target\allure-report"
)

REM Generate Allure report
echo Generating Allure report from results...
call mvn allure:report

REM Check if report was generated successfully
if exist "target\allure-report\index.html" (
    echo.
    echo ==========================================
    echo   Allure Report Generated Successfully!
    echo ==========================================
    echo Report location: target\allure-report\index.html
    echo.
    echo Opening Allure report in browser...
    start "" "target\allure-report\index.html"
) else (
    echo.
    echo ==========================================
    echo   Error: Allure report generation failed!
    echo ==========================================
    echo Please check if:
    echo 1. Tests have been executed (mvn test)
    echo 2. Allure results exist in target\allure-results
    echo 3. Allure is properly configured
)

echo.
echo Available commands:
echo - mvn test : Run tests and generate results
echo - mvn allure:serve : Generate and serve report
echo - mvn allure:report : Generate static report
echo.
pause