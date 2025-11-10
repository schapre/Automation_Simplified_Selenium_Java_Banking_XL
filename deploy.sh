#!/bin/bash

# CI/CD Deployment Script for Automation Framework
# This script can be used in any CI/CD pipeline

set -e  # Exit on any error

# Configuration
PROJECT_NAME="automation-framework"
DOCKER_IMAGE="$PROJECT_NAME:latest"
COMPOSE_FILE="docker-compose.yml"
BACKUP_DIR="./backups"
LOG_FILE="deployment.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a $LOG_FILE
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a $LOG_FILE
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a $LOG_FILE
}

info() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a $LOG_FILE
}

# Function to check prerequisites
check_prerequisites() {
    log "Checking prerequisites..."
    
    if ! command -v docker &> /dev/null; then
        error "Docker is not installed"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose is not installed" 
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        error "Maven is not installed"
        exit 1
    fi
    
    log "✅ All prerequisites are satisfied"
}

# Function to backup current deployment
backup_deployment() {
    log "Creating backup of current deployment..."
    
    mkdir -p $BACKUP_DIR
    BACKUP_NAME="backup_$(date +%Y%m%d_%H%M%S)"
    
    if [ -d "test-output" ]; then
        cp -r test-output "$BACKUP_DIR/$BACKUP_NAME-test-output"
        log "✅ Test outputs backed up"
    fi
    
    if [ -d "target" ]; then
        cp -r target "$BACKUP_DIR/$BACKUP_NAME-target"
        log "✅ Build artifacts backed up"
    fi
}

# Function to build the application
build_application() {
    log "Building application..."
    
    # Clean and compile
    mvn clean compile test-compile
    
    if [ $? -eq 0 ]; then
        log "✅ Application build successful"
    else
        error "❌ Application build failed"
        exit 1
    fi
}

# Function to run tests
run_tests() {
    local test_suite=${1:-"all"}
    local browser=${2:-"chrome"}
    local environment=${3:-"dev"}
    
    log "Running tests - Suite: $test_suite, Browser: $browser, Environment: $environment"
    
    # Set environment variables
    export BROWSER=$browser
    export TEST_SUITE=$test_suite
    export ENVIRONMENT=$environment
    export HEADLESS=true
    export VIDEO_RECORDING=true
    
    # Run tests
    if [ "$test_suite" == "all" ]; then
        mvn test -Dbrowser=$browser -Dheadless=true -Dvideo.recording.enabled=true
    else
        mvn test -Dbrowser=$browser -Dheadless=true -Dtags="@$test_suite" -Dvideo.recording.enabled=true
    fi
    
    local test_exit_code=$?
    
    # Generate reports regardless of test results
    log "Generating test reports..."
    mvn allure:report
    
    if [ $test_exit_code -eq 0 ]; then
        log "✅ All tests passed successfully"
        return 0
    else
        warning "⚠️ Some tests failed"
        return 1
    fi
}

# Function to build Docker image
build_docker_image() {
    log "Building Docker image: $DOCKER_IMAGE"
    
    docker build -t $DOCKER_IMAGE .
    
    if [ $? -eq 0 ]; then
        log "✅ Docker image built successfully"
    else
        error "❌ Docker image build failed"
        exit 1
    fi
}

# Function to deploy with Docker Compose
deploy_with_compose() {
    log "Deploying with Docker Compose..."
    
    # Stop existing containers
    docker-compose -f $COMPOSE_FILE down --remove-orphans
    
    # Start new deployment
    docker-compose -f $COMPOSE_FILE up -d
    
    if [ $? -eq 0 ]; then
        log "✅ Docker Compose deployment successful"
        
        # Wait for services to be ready
        log "Waiting for services to start..."
        sleep 30
        
        # Check service health
        check_service_health
    else
        error "❌ Docker Compose deployment failed"
        exit 1
    fi
}

# Function to check service health
check_service_health() {
    log "Checking service health..."
    
    # Check Selenium Hub
    if curl -f http://localhost:4444/wd/hub/status > /dev/null 2>&1; then
        log "✅ Selenium Hub is healthy"
    else
        warning "⚠️ Selenium Hub might not be ready yet"
    fi
    
    # Check Report Server
    if curl -f http://localhost:8080/health > /dev/null 2>&1; then
        log "✅ Report Server is healthy"
    else
        warning "⚠️ Report Server might not be ready yet"
    fi
}

# Function to run parallel tests in Docker
run_parallel_tests() {
    log "Running parallel tests in Docker environment..."
    
    # Set test parameters
    export BROWSER=${BROWSER:-"chrome"}
    export TEST_SUITE=${TEST_SUITE:-"all"}
    export VIDEO_RECORDING=${VIDEO_RECORDING:-"true"}
    
    # Run tests in containers
    docker-compose exec -T automation-tests /app/entrypoint.sh
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        log "✅ Parallel tests completed successfully"
    else
        warning "⚠️ Some parallel tests failed"
    fi
    
    # Copy reports from container
    docker cp automation-framework:/app/target ./
    docker cp automation-framework:/app/test-output ./
    
    return $exit_code
}

# Function to publish reports
publish_reports() {
    log "Publishing test reports..."
    
    # Check if reports exist
    if [ -d "target/allure-report" ]; then
        log "📊 Allure report available at: http://localhost:8080/allure/"
    fi
    
    if [ -f "test-output/SparkReport/Spark.html" ]; then
        log "⚡ Spark report available at: http://localhost:8080/spark/"
    fi
    
    if [ -f "test-output/HtmlReport/ExtentHtml.html" ]; then
        log "📋 Extent report available at: http://localhost:8080/extent/"
    fi
    
    log "🌐 Reports dashboard available at: http://localhost:8080/"
}

# Function to send notifications
send_notifications() {
    local status=$1
    local message="$PROJECT_NAME deployment $status"
    
    # Slack notification (if webhook is configured)
    if [ ! -z "$SLACK_WEBHOOK_URL" ]; then
        curl -X POST -H 'Content-type: application/json' \
            --data "{\"text\":\"$message\"}" \
            "$SLACK_WEBHOOK_URL"
        log "📱 Slack notification sent"
    fi
    
    # Email notification (if configured)
    if [ ! -z "$NOTIFICATION_EMAIL" ]; then
        echo "$message" | mail -s "$PROJECT_NAME Deployment Status" "$NOTIFICATION_EMAIL"
        log "📧 Email notification sent"
    fi
}

# Function to cleanup
cleanup() {
    log "Cleaning up temporary files..."
    
    # Remove old backups (keep last 5)
    if [ -d "$BACKUP_DIR" ]; then
        ls -t "$BACKUP_DIR" | tail -n +6 | xargs -I {} rm -rf "$BACKUP_DIR/{}"
    fi
    
    # Clean up old Docker images
    docker image prune -f
    
    log "✅ Cleanup completed"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  build          - Build the application"
    echo "  test           - Run tests locally"
    echo "  docker-build   - Build Docker image"
    echo "  deploy         - Deploy with Docker Compose"
    echo "  parallel-test  - Run parallel tests in Docker"
    echo "  full-pipeline  - Run complete CI/CD pipeline"
    echo "  cleanup        - Clean up resources"
    echo ""
    echo "Options:"
    echo "  --browser      - Browser for tests (chrome|firefox|edge)"
    echo "  --suite        - Test suite (all|web|api|mobile)"
    echo "  --env          - Environment (dev|staging|prod)"
    echo "  --skip-tests   - Skip test execution"
    echo "  --no-backup    - Skip backup creation"
    echo ""
    echo "Examples:"
    echo "  $0 test --browser chrome --suite web"
    echo "  $0 deploy --env staging"
    echo "  $0 full-pipeline --browser firefox --suite all --env prod"
}

# Main execution logic
main() {
    local command=${1:-"help"}
    shift || true
    
    # Parse options
    BROWSER="chrome"
    TEST_SUITE="all"
    ENVIRONMENT="dev"
    SKIP_TESTS=false
    NO_BACKUP=false
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --browser)
                BROWSER="$2"
                shift 2
                ;;
            --suite)
                TEST_SUITE="$2"
                shift 2
                ;;
            --env)
                ENVIRONMENT="$2"
                shift 2
                ;;
            --skip-tests)
                SKIP_TESTS=true
                shift
                ;;
            --no-backup)
                NO_BACKUP=true
                shift
                ;;
            *)
                error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    # Execute command
    case $command in
        build)
            log "🚀 Starting build process..."
            check_prerequisites
            build_application
            log "✅ Build completed successfully"
            ;;
        test)
            log "🧪 Starting test execution..."
            check_prerequisites
            if [ "$NO_BACKUP" != true ]; then
                backup_deployment
            fi
            run_tests "$TEST_SUITE" "$BROWSER" "$ENVIRONMENT"
            log "✅ Test execution completed"
            ;;
        docker-build)
            log "🐋 Starting Docker build..."
            check_prerequisites
            build_docker_image
            log "✅ Docker build completed"
            ;;
        deploy)
            log "🚀 Starting deployment..."
            check_prerequisites
            build_docker_image
            deploy_with_compose
            publish_reports
            send_notifications "successful"
            log "✅ Deployment completed successfully"
            ;;
        parallel-test)
            log "🔄 Starting parallel test execution..."
            check_prerequisites
            run_parallel_tests
            publish_reports
            log "✅ Parallel test execution completed"
            ;;
        full-pipeline)
            log "🎯 Starting full CI/CD pipeline..."
            check_prerequisites
            
            if [ "$NO_BACKUP" != true ]; then
                backup_deployment
            fi
            
            build_application
            
            if [ "$SKIP_TESTS" != true ]; then
                run_tests "$TEST_SUITE" "$BROWSER" "$ENVIRONMENT"
            fi
            
            build_docker_image
            deploy_with_compose
            
            if [ "$SKIP_TESTS" != true ]; then
                run_parallel_tests
            fi
            
            publish_reports
            cleanup
            send_notifications "successful"
            log "🎉 Full pipeline completed successfully!"
            ;;
        cleanup)
            log "🧹 Starting cleanup..."
            cleanup
            log "✅ Cleanup completed"
            ;;
        help|--help|-h)
            show_usage
            ;;
        *)
            error "Unknown command: $command"
            show_usage
            exit 1
            ;;
    esac
}

# Trap signals for cleanup
trap 'error "Script interrupted"; cleanup; exit 1' INT TERM

# Run main function
main "$@"