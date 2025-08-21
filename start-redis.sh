#!/bin/bash

echo "Starting Redis for Puppy Project..."

# Check if Redis is already running
if pgrep -x "redis-server" > /dev/null; then
    echo "Redis is already running"
else
    # Start Redis using Docker
    if command -v docker &> /dev/null; then
        echo "Starting Redis with Docker..."
        docker run -d \
            --name puppy-redis \
            -p 6379:6379 \
            -v redis_data:/data \
            redis:7-alpine \
            redis-server --appendonly yes
        echo "Redis started on port 6379"
    else
        echo "Docker not found. Please install Docker or start Redis manually."
        echo "You can install Redis with: brew install redis"
        echo "Then start it with: redis-server"
    fi
fi

echo "Redis is ready!"
