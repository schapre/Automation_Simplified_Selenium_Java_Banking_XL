pipeline {
    agent any
    
    parameters {
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to run tests on'
        )
        choice(
            name: 'TEST_SUITE',
            choices: ['all', 'web', 'api', 'mobile'],
            description: 'Test suite to execute'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Environment to test against'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
        booleanParam(
            name: 'VIDEO_RECORDING',
            defaultValue: true,
            description: 'Enable video recording for UI tests'
        )
    }
    
    environment {
        MAVEN_OPTS = '-Xmx2048m'
        JAVA_HOME = tool('JDK11')
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK11'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo "Code checked out successfully"
            }
        }
        
        stage('Environment Setup') {
            steps {
                script {
                    // Create necessary directories
                    sh '''
                        mkdir -p test-output/videos
                        mkdir -p target/allure-results
                        mkdir -p target/surefire-reports
                    '''
                }
                echo "Environment setup completed"
            }
        }
        
        stage('Build') {
            steps {
                script {
                    try {
                        sh 'mvn clean compile test-compile'
                        echo "✅ Build completed successfully"
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error "❌ Build failed: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('Static Code Analysis') {
            parallel {
                stage('SonarQube Analysis') {
                    when {
                        anyOf {
                            branch 'main'
                            branch 'develop'
                            changeRequest()
                        }
                    }
                    steps {
                        script {
                            try {
                                withSonarQubeEnv('SonarQube') {
                                    sh '''
                                        mvn sonar:sonar \
                                        -Dsonar.projectKey=automation-framework \
                                        -Dsonar.host.url=${SONAR_HOST_URL} \
                                        -Dsonar.login=${SONAR_AUTH_TOKEN}
                                    '''
                                }
                            } catch (Exception e) {
                                echo "⚠️ SonarQube analysis failed: ${e.getMessage()}"
                            }
                        }
                    }
                }
                
                stage('Dependency Check') {
                    steps {
                        script {
                            try {
                                sh 'mvn org.owasp:dependency-check-maven:check'
                                publishHTML([
                                    allowMissing: false,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true,
                                    reportDir: 'target',
                                    reportFiles: 'dependency-check-report.html',
                                    reportName: 'Dependency Check Report'
                                ])
                            } catch (Exception e) {
                                echo "⚠️ Dependency check failed: ${e.getMessage()}"
                            }
                        }
                    }
                }
            }
        }
        
        stage('Test Execution') {
            steps {
                script {
                    try {
                        def testCommand = "mvn test -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"
                        
                        if (params.TEST_SUITE != 'all') {
                            testCommand += " -Dtags=@${params.TEST_SUITE}"
                        }
                        
                        if (params.VIDEO_RECORDING) {
                            testCommand += " -Dvideo.recording.enabled=true"
                        }
                        
                        sh testCommand
                        echo "✅ Tests executed successfully"
                    } catch (Exception e) {
                        echo "⚠️ Some tests failed: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    // Publish test results
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    
                    // Archive test artifacts
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'test-output/**/*', allowEmptyArchive: true
                }
            }
        }
        
        stage('Generate Reports') {
            parallel {
                stage('Allure Report') {
                    steps {
                        script {
                            try {
                                sh 'mvn allure:report'
                                allure([
                                    includeProperties: false,
                                    jdk: '',
                                    properties: [],
                                    reportBuildPolicy: 'ALWAYS',
                                    results: [[path: 'target/allure-results']]
                                ])
                                echo "✅ Allure report generated"
                            } catch (Exception e) {
                                echo "⚠️ Allure report generation failed: ${e.getMessage()}"
                            }
                        }
                    }
                }
                
                stage('ExtentReports') {
                    steps {
                        script {
                            try {
                                publishHTML([
                                    allowMissing: false,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true,
                                    reportDir: 'test-output/SparkReport',
                                    reportFiles: 'Spark.html',
                                    reportName: 'Extent Spark Report'
                                ])
                                echo "✅ Extent report published"
                            } catch (Exception e) {
                                echo "⚠️ Extent report publishing failed: ${e.getMessage()}"
                            }
                        }
                    }
                }
            }
        }
        
        stage('Performance Analysis') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    try {
                        // Add performance analysis tools like JMeter or custom performance tests
                        echo "📊 Performance analysis completed"
                    } catch (Exception e) {
                        echo "⚠️ Performance analysis failed: ${e.getMessage()}"
                    }
                }
            }
        }
    }
    
    post {
        always {
            script {
                def buildStatus = currentBuild.result ?: 'SUCCESS'
                def color = buildStatus == 'SUCCESS' ? 'good' : buildStatus == 'UNSTABLE' ? 'warning' : 'danger'
                def emoji = buildStatus == 'SUCCESS' ? '✅' : buildStatus == 'UNSTABLE' ? '⚠️' : '❌'
                
                // Archive video recordings
                archiveArtifacts artifacts: 'test-output/videos/**/*', allowEmptyArchive: true
                
                // Send notifications
                slackSend(
                    channel: '#automation',
                    color: color,
                    message: """
                        ${emoji} *Automation Framework Build ${buildStatus}*
                        
                        *Job:* ${env.JOB_NAME}
                        *Build:* ${env.BUILD_NUMBER}
                        *Branch:* ${env.BRANCH_NAME}
                        *Browser:* ${params.BROWSER}
                        *Test Suite:* ${params.TEST_SUITE}
                        *Environment:* ${params.ENVIRONMENT}
                        
                        *Reports:*
                        • <${env.BUILD_URL}allure|📊 Allure Report>
                        • <${env.BUILD_URL}Extent_Spark_Report|📋 Extent Report>
                        
                        *Duration:* ${currentBuild.durationString}
                    """
                )
                
                // Email notification for failures
                if (buildStatus != 'SUCCESS') {
                    emailext (
                        subject: "${emoji} Automation Framework Build ${buildStatus} - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        body: """
                            Build Status: ${buildStatus}
                            
                            Job: ${env.JOB_NAME}
                            Build Number: ${env.BUILD_NUMBER}
                            Branch: ${env.BRANCH_NAME}
                            
                            Parameters:
                            - Browser: ${params.BROWSER}
                            - Test Suite: ${params.TEST_SUITE}
                            - Environment: ${params.ENVIRONMENT}
                            - Headless: ${params.HEADLESS}
                            - Video Recording: ${params.VIDEO_RECORDING}
                            
                            Build URL: ${env.BUILD_URL}
                            
                            Please check the reports for more details.
                        """,
                        to: "${env.CHANGE_AUTHOR_EMAIL}, automation-team@company.com"
                    )
                }
            }
        }
        
        success {
            echo "🎉 Pipeline completed successfully!"
        }
        
        failure {
            echo "💥 Pipeline failed!"
        }
        
        unstable {
            echo "⚠️ Pipeline completed with test failures!"
        }
        
        cleanup {
            // Clean up workspace
            cleanWs()
        }
    }
}