#!/bin/bash

# Test script for follow/friend functionality
# This script runs all follow-related tests in the correct order

echo "👥 Running Follow/Friend Tests"
echo "==============================="

# Set test profile
export SPRING_PROFILES_ACTIVE=test

# Run unit tests first (fastest)
echo ""
echo "📋 Running Unit Tests..."
echo "------------------------"
./mvnw test -Dtest="*FollowServiceTest" -q

# Run repository tests
echo ""
echo "🗄️  Running Repository Tests..."
echo "-------------------------------"
./mvnw test -Dtest="*FollowRepositoryTest" -q

# Run controller tests
echo ""
echo "🌐 Running Controller Tests..."
echo "-----------------------------"
./mvnw test -Dtest="*FollowControllerTest" -q

# Run integration tests (slowest)
echo ""
echo "🔗 Running Integration Tests..."
echo "-------------------------------"
./mvnw test -Dtest="*FollowIntegrationTest" -q

# Run all follow-related tests together
echo ""
echo "🎯 Running All Follow Tests..."
echo "-----------------------------"
./mvnw test -Dtest="*Follow*Test" -q

echo ""
echo "✅ All follow tests completed!"
echo ""
echo "📊 Test Summary:"
echo "   - Unit Tests: FollowService business logic"
echo "   - Repository Tests: FollowRepository database operations"
echo "   - Controller Tests: FollowController HTTP endpoints"
echo "   - Integration Tests: Complete follow workflow"
echo ""
echo "💡 To run specific test categories:"
echo "   ./mvnw test -Dtest=\"*FollowServiceTest\"      # Unit tests only"
echo "   ./mvnw test -Dtest=\"*FollowRepositoryTest\"  # Repository tests only"
echo "   ./mvnw test -Dtest=\"*FollowControllerTest\"  # Controller tests only"
echo "   ./mvnw test -Dtest=\"*FollowIntegrationTest\" # Integration tests only"
echo ""
echo "🔍 Test Coverage:"
echo "   ✓ Follow user successfully"
echo "   ✓ Prevent duplicate follows"
echo "   ✓ Handle non-existent users"
echo "   ✓ Prevent self-following"
echo "   ✓ Validate request data"
echo "   ✓ Update follow counts"
echo "   ✓ Database persistence"
echo "   ✓ HTTP endpoint validation" 