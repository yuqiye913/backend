#!/bin/bash

# Integration Test Runner Script
# This script runs all integration tests for the Spring Reddit Clone backend

set -e  # Exit on any error

echo "=========================================="
echo "Running Integration Tests"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Maven wrapper exists
if [ ! -f "./mvnw" ]; then
    print_error "Maven wrapper (mvnw) not found. Please run this script from the backend directory."
    exit 1
fi

# Make mvnw executable if it isn't already
chmod +x ./mvnw

print_status "Starting integration tests..."

# Run integration tests
print_status "Running tests with pattern '*IntegrationTest'"
./mvnw test -Dtest="*IntegrationTest" -q

# Check the exit code
if [ $? -eq 0 ]; then
    print_success "All integration tests passed!"
    echo ""
    print_status "Test Summary:"
    echo "  ✅ Integration tests completed successfully"
    echo "  ✅ No test failures detected"
else
    print_warning "Some integration tests failed or had errors."
    echo ""
    print_status "Test Summary:"
    echo "  ⚠️  Some tests failed - check the output above for details"
    echo "  📊 Check target/surefire-reports/ for detailed test reports"
    echo ""
    print_status "To see detailed test reports, run:"
    echo "  ./mvnw test -Dtest=\"*IntegrationTest\" -X"
fi

echo ""
echo "=========================================="
print_status "Integration test run completed"
echo "==========================================" 