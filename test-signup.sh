#!/bin/bash

# Test script for user signup functionality
# This script runs all signup-related tests in the correct order

echo "🧪 Running User Signup Tests"
echo "=============================="

# Set test profile
export SPRING_PROFILES_ACTIVE=test

# Run unit tests first (fastest)
echo ""
echo "📋 Running Unit Tests..."
echo "------------------------"
./mvnw test -Dtest="*AuthServiceImplTest" -q

# Run repository tests
echo ""
echo "🗄️  Running Repository Tests..."
echo "-------------------------------"
./mvnw test -Dtest="*UserRepositoryTest" -q

# Run controller tests
echo ""
echo "🌐 Running Controller Tests..."
echo "-----------------------------"
./mvnw test -Dtest="*AuthControllerSignupTest" -q

# Run integration tests (slowest)
echo ""
echo "🔗 Running Integration Tests..."
echo "-------------------------------"
./mvnw test -Dtest="*SignupIntegrationTest" -q

# Run all auth-related tests together
echo ""
echo "🎯 Running All Auth Tests..."
echo "----------------------------"
./mvnw test -Dtest="*Auth*Test" -q

echo ""
echo "✅ All signup tests completed!"
echo ""
echo "📊 Test Summary:"
echo "   - Unit Tests: AuthServiceImpl"
echo "   - Repository Tests: UserRepository"
echo "   - Controller Tests: AuthController signup endpoint"
echo "   - Integration Tests: Complete signup flow"
echo ""
echo "💡 To run specific test categories:"
echo "   ./mvnw test -Dtest=\"*AuthServiceImplTest\"     # Unit tests only"
echo "   ./mvnw test -Dtest=\"*UserRepositoryTest\"     # Repository tests only"
echo "   ./mvnw test -Dtest=\"*AuthControllerSignupTest\" # Controller tests only"
echo "   ./mvnw test -Dtest=\"*SignupIntegrationTest\"   # Integration tests only" 