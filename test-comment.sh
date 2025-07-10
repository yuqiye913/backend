#!/bin/bash

# Test script for comment functionality
# This script runs all comment-related tests in the correct order

echo "💬 Running Comment Tests"
echo "==============================="

# Set test profile
export SPRING_PROFILES_ACTIVE=test

# Run unit/service tests first (fastest)
echo ""
echo "📋 Running Service Tests..."
echo "------------------------"
./mvnw test -Dtest="*CommentServiceTest" -q

# Run repository tests
echo ""
echo "🗄️  Running Repository Tests..."
echo "-------------------------------"
./mvnw test -Dtest="*CommentRepositoryTest" -q

# Run controller tests
echo ""
echo "🌐 Running Controller Tests..."
echo "-----------------------------"
./mvnw test -Dtest="*CommentsControllerTest" -q

# Run all comment-related tests together
echo ""
echo "🎯 Running All Comment Tests..."
echo "-----------------------------"
./mvnw test -Dtest="*Comment*Test" -q

echo ""
echo "✅ All comment tests completed!"
echo ""
echo "📊 Test Summary:"
echo "   - Service Tests: CommentService business logic"
echo "   - Repository Tests: CommentRepository database operations"
echo "   - Controller Tests: CommentsController HTTP endpoints"
echo ""
echo "💡 To run specific test categories:"
echo "   ./mvnw test -Dtest=\"*CommentServiceTest\"      # Service tests only"
echo "   ./mvnw test -Dtest=\"*CommentRepositoryTest\"  # Repository tests only"
echo "   ./mvnw test -Dtest=\"*CommentsControllerTest\" # Controller tests only"
echo ""
echo "🔍 Test Coverage:"
echo "   ✓ Create comment (top-level and reply)"
echo "   ✓ Get comments for post/user (paginated and all)"
echo "   ✓ Get replies for comment (threading)"
echo "   ✓ Swear word detection"
echo "   ✓ Error handling and edge cases"
echo "   ✓ HTTP endpoint validation" 