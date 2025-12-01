#!/bin/bash

# E-Commerce Microservice Test Runner
# This script runs all JUnit tests across all services

echo "🚀 Starting E-Commerce Microservice Test Suite"
echo "=============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to run tests for a specific service
run_service_tests() {
    local service_name=$1
    local service_path=$2
    
    echo -e "\n${BLUE}Testing $service_name...${NC}"
    echo "----------------------------------------"
    
    cd "$service_path" || exit 1
    
    # Run tests with Maven
    if mvn test -Dspring.profiles.active=test; then
        echo -e "${GREEN}✅ $service_name tests PASSED${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name tests FAILED${NC}"
        return 1
    fi
}

# Function to run integration tests
run_integration_tests() {
    echo -e "\n${BLUE}Running Integration Tests...${NC}"
    echo "----------------------------------------"
    
    # Start Docker containers for integration tests
    echo "Starting Docker containers for integration tests..."
    docker-compose -f docker-compose.test.yml up -d
    
    # Wait for services to be ready
    echo "Waiting for services to be ready..."
    sleep 30
    
    # Run integration tests
    echo "Running integration tests..."
    
    # Stop Docker containers
    docker-compose -f docker-compose.test.yml down
    
    echo -e "${GREEN}✅ Integration tests completed${NC}"
}

# Main test execution
main() {
    local start_time=$(date +%s)
    local failed_services=()
    
    echo -e "${YELLOW}Running JUnit tests for all microservices...${NC}"
    
    # Test Auth Service
    if ! run_service_tests "Auth Service" "./auth-service"; then
        failed_services+=("Auth Service")
    fi
    
    # Test Inventory Service
    if ! run_service_tests "Inventory Service" "./inventory-service"; then
        failed_services+=("Inventory Service")
    fi
    
    # Test Order Service
    if ! run_service_tests "Order Service" "./order-service"; then
        failed_services+=("Order Service")
    fi
    
    # Test Payment Service
    if ! run_service_tests "Payment Service" "./payment-service"; then
        failed_services+=("Payment Service")
    fi
    
    # Test User Service
    if ! run_service_tests "User Service" "./user-service"; then
        failed_services+=("User Service")
    fi
    
    # Test Warehouse Service
    if ! run_service_tests "Warehouse Service" "./warehouse-service"; then
        failed_services+=("Warehouse Service")
    fi
    
    # Calculate execution time
    local end_time=$(date +%s)
    local execution_time=$((end_time - start_time))
    
    # Print summary
    echo -e "\n${BLUE}Test Execution Summary${NC}"
    echo "========================"
    echo -e "Total execution time: ${execution_time}s"
    
    if [ ${#failed_services[@]} -eq 0 ]; then
        echo -e "${GREEN}🎉 All tests PASSED!${NC}"
        echo -e "${GREEN}✅ All microservices are working correctly${NC}"
        exit 0
    else
        echo -e "${RED}❌ Some tests FAILED:${NC}"
        for service in "${failed_services[@]}"; do
            echo -e "${RED}  - $service${NC}"
        done
        exit 1
    fi
}

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed. Please install Maven first.${NC}"
    exit 1
fi

# Check if Docker is installed (for integration tests)
if ! command -v docker &> /dev/null; then
    echo -e "${YELLOW}⚠️  Docker is not installed. Integration tests will be skipped.${NC}"
fi

# Parse command line arguments
case "${1:-all}" in
    "auth")
        run_service_tests "Auth Service" "./auth-service"
        ;;
    "inventory")
        run_service_tests "Inventory Service" "./inventory-service"
        ;;
    "order")
        run_service_tests "Order Service" "./order-service"
        ;;
    "payment")
        run_service_tests "Payment Service" "./payment-service"
        ;;
    "user")
        run_service_tests "User Service" "./user-service"
        ;;
    "warehouse")
        run_service_tests "Warehouse Service" "./warehouse-service"
        ;;
    "integration")
        run_integration_tests
        ;;
    "all"|*)
        main
        ;;
esac





















