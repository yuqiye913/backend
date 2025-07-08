#!/bin/bash

# Test script for post functionality with multiple subreddits
# This script runs all post-related tests in the correct order

echo "📝 Running Post Tests (Multiple Subreddits)"
echo "==========================================="

# Set test profile
export SPRING_PROFILES_ACTIVE=test

# Run unit tests first (fastest)
echo ""
echo "📋 Running Unit Tests..."
echo "------------------------"
./mvnw test -Dtest="*PostServiceTest" -q

# Run repository tests
echo ""
echo "🗄️  Running Repository Tests..."
echo "-------------------------------"
./mvnw test -Dtest="*PostRepositoryTest" -q

# Run controller tests
echo ""
echo "🌐 Running Controller Tests..."
echo "-----------------------------"
./mvnw test -Dtest="*PostControllerTest" -q

# Run all post-related unit tests together (excluding integration tests)
echo ""
echo "🎯 Running All Post Unit Tests..."
echo "--------------------------------"
./mvnw test -Dtest="*PostServiceTest,*PostRepositoryTest,*PostControllerTest" -q

echo ""
echo "✅ All post unit tests completed!"
echo ""
echo "📊 Unit Test Summary:"
echo "   - Unit Tests: PostService business logic with multiple subreddits"
echo "   - Repository Tests: PostRepository database operations"
echo "   - Controller Tests: PostController HTTP endpoints"
echo "   - Integration Tests: Excluded (run separately when needed)"
echo ""
echo "💡 To run specific test categories:"
echo "   ./mvnw test -Dtest=\"*PostServiceTest\"      # Unit tests only"
echo "   ./mvnw test -Dtest=\"*PostRepositoryTest\"  # Repository tests only"
echo "   ./mvnw test -Dtest=\"*PostControllerTest\"  # Controller tests only"
echo "   ./mvnw test -Dtest=\"*PostIntegrationTest\" # Integration tests (run separately)"
echo ""
echo "🔍 Test Coverage:"
echo "   ✓ Create post with multiple subreddits"
echo "   ✓ Create post with single subreddit"
echo "   ✓ Validate subreddit requirements"
echo "   ✓ Handle subreddit not found errors"
echo "   ✓ Query posts by multiple subreddits"
echo "   ✓ Query posts by single subreddit"
echo "   ✓ Query posts by user"
echo "   ✓ Database persistence with many-to-many relationships"
echo "   ✓ HTTP endpoint validation"
echo "   ✓ Error handling and validation"
echo ""
echo "🚀 New API Features:"
echo "   POST /api/posts - Create post with multiple subreddits"
echo "   GET /api/posts/by-subreddits - Query by multiple subreddits"
echo "   GET /api/posts?subredditId=X - Query by single subreddit"
echo "   GET /api/posts?username=X - Query by username" 