# Multi-Platform Automation Framework - Reporting Integration

This framework now supports dual reporting with both **Spark Reports** and **Allure Reports** for comprehensive test result visualization.

## Available Reports

### 1. Spark Report (ExtentReports)

- **Location**: `test-output/SparkReport/Spark.html`
- **Features**: Real-time dashboards, test categorization, screenshot integration
- **Best for**: Quick test result overview, management reporting

### 2. Allure Report

- **Location**: `target/allure-report/index.html`
- **Features**: Detailed test analytics, trends, historical data, behavior-driven reporting
- **Best for**: Detailed analysis, CI/CD integration, trend analysis

### 3. Additional Reports

- **HTML Report**: `test-output/HtmlReport/ExtentHtml.html`
- **PDF Report**: `test-output/PdfReport/ExtentPdf.pdf`
- **Cucumber Report**: `target/cucumber-reports/cucumber-html-report.html`

## Quick Start

### Running Tests and Generating All Reports

```bash
# Option 1: Use the convenient batch script
.\run-tests-and-reports.bat

# Option 2: Manual commands
mvn clean test
mvn allure:report
```

### Generate Allure Report Only

```bash
# Option 1: Use batch script
.\generate-allure-report.bat

# Option 2: Manual commands
mvn allure:report
mvn allure:serve  # Generate and open in browser
```

## 📋 Report Features

### Spark Report Features

- ✅ Real-time test execution dashboard
- ✅ Screenshot capture on failure
- ✅ Test categorization and filtering
- ✅ Environment information
- ✅ PDF export capability
- ✅ Dark/Light theme support

### Allure Report Features

- ✅ Behavior-driven test organization
- ✅ Step-by-step execution details
- ✅ Screenshot and attachment support
- ✅ Test history and trends
- ✅ Flaky test detection
- ✅ Environment and executor information
- ✅ Integration with CI/CD pipelines

## 🛠️ Configuration Files

### Allure Configuration

- **allure.properties**: Basic Allure settings
- **environment.properties**: Test environment information
- **allure.config.json**: Advanced Allure configuration

### Spark Configuration

- **extent.properties**: ExtentReports configuration
- **spark-config.xml**: Spark theme and styling

## Adding Allure Annotations

### Step Definitions Example

```java
@Step("Login with username {username}")
@Description("User authentication process")
public void loginUser(String username, String password) {
    AllureManager.addStep("Entering credentials");
    // Your test logic
    AllureManager.attachScreenshot(driver, "Login Page");
}
```

### Available Allure Annotations

- `@Step("description")`: Mark method as test step
- `@Description("text")`: Add description to test
- `@Severity(SeverityLevel.CRITICAL)`: Set test severity
- `@Feature("Login")`: Group tests by feature
- `@Story("User Authentication")`: Group by user story
- `@Issue("JIRA-123")`: Link to issue tracker
- `@TmsLink("TEST-456")`: Link to test management system

## 🔧 Maven Commands

```bash
# Run tests
mvn clean test

# Generate Allure report
mvn allure:report

# Serve Allure report (generates and opens in browser)
mvn allure:serve

# Clean Allure results
mvn allure:clean

# Run specific test suite
mvn test -DsuiteXmlFile=testng.xml
```

## 📊 Report Screenshots Integration

Both reporting systems automatically capture screenshots:

### Failure Screenshots

- Automatically captured when tests fail
- Attached to both Spark and Allure reports
- Saved to dedicated folder: `test-output/screenshots/`
- Timestamped filenames for easy identification

### Screenshot Storage Locations

1. **Standalone Screenshots**: `test-output/screenshots/`
   - Individual PNG files with descriptive names
   - Timestamped for uniqueness
   - Easy access for manual review

2. **Allure Attachments**: `target/allure-results/`
   - Integrated with test reports
   - Viewable in Allure report interface

3. **Video Frames**: `test-output/videos/`
   - Screenshot frames from video recordings
   - Organized by test name

### Manual Screenshots

```java
// In step definitions - automatically saves to screenshots folder
AllureManager.attachScreenshot(driver, "Custom Screenshot Name");
```

## 🎯 Best Practices

### For Spark Reports

1. Use categorization with `@Category` annotations
2. Configure appropriate themes in `spark-config.xml`
3. Enable screenshot capture for better debugging

### For Allure Reports

1. Use `@Step` annotations for detailed test flow
2. Add `@Description` for better test documentation
3. Use `@Feature` and `@Story` for organization
4. Attach relevant files and logs using `AllureManager`

### General

1. Run both reports for comprehensive coverage
2. Use Spark for quick analysis, Allure for detailed investigation
3. Configure CI/CD to publish both report types
4. Regular cleanup of old report files

## 🐛 Troubleshooting

### Common Issues

1. **Allure command not found**

   ```bash
   # Install Allure CLI
   npm install -g allure-commandline --save-dev
   ```

2. **Reports not generating**
   - Check Maven dependencies in `pom.xml`
   - Ensure test results exist in `target/allure-results`
   - Verify plugin configuration

3. **Screenshots not appearing**
   - Check WebDriver initialization
   - Verify screenshot directory permissions
   - Ensure `AllureManager` is properly imported

### Report Locations

If reports are not in expected locations, check:

- `extent.properties` for Spark report paths
- `allure.properties` for Allure result directory
- Maven Surefire plugin configuration

## 📈 Integration with CI/CD

### Jenkins Integration

```groovy
pipeline {
    post {
        always {
            // Publish Allure Report
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])
            
            // Archive Spark Reports
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output/SparkReport',
                reportFiles: 'Spark.html',
                reportName: 'Spark Report'
            ])
        }
    }
}
```

## 🔄 Version Information

- **Framework**: Selenium + Cucumber + TestNG
- **Allure Version**: 2.24.0
- **ExtentReports Version**: 5.1.2
- **Java Version**: 11+
- **Maven Version**: 3.6+

---

Happy Testing! 🚀
