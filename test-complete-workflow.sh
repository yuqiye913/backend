#!/bin/bash

# Complete Workflow Integration Test Runner
# This script demonstrates the difference between stubbed and non-stubbed integration tests

set -e  # Exit on any error

echo "=========================================="
echo "Complete Workflow Integration Tests"
echo "Testing Real JWT, Real Security, Real Auth"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
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

print_highlight() {
    echo -e "${PURPLE}[HIGHLIGHT]${NC} $1"
}

# Check if Maven wrapper exists
if [ ! -f "./mvnw" ]; then
    print_error "Maven wrapper (mvnw) not found. Please run this script from the backend directory."
    exit 1
fi

# Make mvnw executable if it isn't already
chmod +x ./mvnw

print_highlight "🚀 Starting Complete Workflow Integration Tests"
echo ""

print_status "Testing complete user journey:"
echo "  📝 Register → 📧 Email Verification → 🔐 Login → 🛡️ JWT Auth → 📄 Access Protected Resource"
echo ""

print_status "Key differences from stubbed tests:"
echo "  ✅ Real JWT token generation and validation"
echo "  ✅ Real password encoding and verification"
echo "  ✅ Real security context and authentication"
echo "  ✅ Real database transactions and persistence"
echo "  ✅ Real email capture (without external SMTP)"
echo "  ✅ Real user state management"
echo ""

# Run the complete workflow tests
print_status "Running CompleteWorkflowIntegrationTest..."
./mvnw test -Dtest="CompleteWorkflowIntegrationTest" -q

# Check the exit code
if [ $? -eq 0 ]; then
    print_success "Complete workflow integration tests passed!"
    echo ""
    print_highlight "🎉 What this proves:"
    echo "  ✅ Complete user registration flow works end-to-end"
    echo "  ✅ JWT authentication is properly implemented"
    echo "  ✅ Security is correctly configured"
    echo "  ✅ Database operations are working correctly"
    echo "  ✅ Email service integration is functional"
    echo "  ✅ Protected resources are properly secured"
    echo ""
    print_status "This gives you confidence that your production system will work!"
else
    print_warning "Some complete workflow tests failed."
    echo ""
    print_status "This indicates real integration issues that need to be fixed."
    echo "Check the test output above for specific failure details."
fi

echo ""
echo "=========================================="
print_highlight "Complete Workflow Test Summary"
echo "=========================================="
echo ""
print_status "Benefits of complete workflow testing:"
echo "  🔍 Tests real system behavior, not just mocked components"
echo "  🛡️ Validates security implementation end-to-end"
echo "  🗄️ Verifies database integration and transactions"
echo "  📧 Confirms email service integration (without external deps)"
echo "  🎯 Catches integration bugs that unit tests miss"
echo "  🚀 Provides confidence for production deployment"
echo ""
print_status "Next steps:"
echo "  📋 Run: ./mvnw test -Dtest=\"*IntegrationTest\" # All integration tests"
echo "  🔧 Run: ./mvnw test -Dtest=\"*Test\" # All tests"
echo "  📊 Check: target/surefire-reports/ for detailed reports" 