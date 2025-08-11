#!/bin/bash

# Test script for Random Video Call System
# This demonstrates how users can request random video calls and get matched

BASE_URL="http://localhost:8080"
AUTH_TOKEN="your_jwt_token_here"

echo "🎥 Random Video Call System Test"
echo "================================="

# Function to make authenticated requests
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    
    if [ -n "$data" ]; then
        curl -X $method "$BASE_URL$endpoint" \
            -H "Authorization: Bearer $AUTH_TOKEN" \
            -H "Content-Type: application/json" \
            -d "$data"
    else
        curl -X $method "$BASE_URL$endpoint" \
            -H "Authorization: Bearer $AUTH_TOKEN"
    fi
}

echo ""
echo "1. 📞 Requesting Random Video Call (User 1)"
echo "--------------------------------------------"

# User 1 requests a random video call
REQUEST_1=$(make_request POST "/api/random-video-calls/request" '{
    "userId": 1,
    "callType": "video",
    "enableVideo": true,
    "enableAudio": true,
    "videoQuality": "high",
    "audioQuality": "high",
    "preferredGender": "any",
    "preferredAgeRange": "any",
    "preferredLanguage": "en",
    "preferredLocation": "any",
    "isPriority": false,
    "queueType": "random"
}')

echo "Response: $REQUEST_1"

# Extract request ID from response
REQUEST_ID_1=$(echo $REQUEST_1 | grep -o '"requestId":"[^"]*"' | cut -d'"' -f4)
echo "Request ID: $REQUEST_ID_1"

echo ""
echo "2. 📞 Requesting Random Video Call (User 2)"
echo "--------------------------------------------"

# User 2 requests a random video call
REQUEST_2=$(make_request POST "/api/random-video-calls/request" '{
    "userId": 2,
    "callType": "video",
    "enableVideo": true,
    "enableAudio": true,
    "videoQuality": "high",
    "audioQuality": "high",
    "preferredGender": "any",
    "preferredAgeRange": "any",
    "preferredLanguage": "en",
    "preferredLocation": "any",
    "isPriority": false,
    "queueType": "random"
}')

echo "Response: $REQUEST_2"

# Extract request ID from response
REQUEST_ID_2=$(echo $REQUEST_2 | grep -o '"requestId":"[^"]*"' | cut -d'"' -f4)
echo "Request ID: $REQUEST_ID_2"

echo ""
echo "3. 🔍 Checking Queue Status"
echo "---------------------------"

# Check status for both users
echo "User 1 Status:"
make_request GET "/api/random-video-calls/status/$REQUEST_ID_1"

echo ""
echo "User 2 Status:"
make_request GET "/api/random-video-calls/status/$REQUEST_ID_2"

echo ""
echo "4. 📊 Queue Statistics"
echo "----------------------"

make_request GET "/api/random-video-calls/statistics"

echo ""
echo "5. ⏳ Waiting for Match (simulating 10 seconds)"
echo "-----------------------------------------------"

sleep 10

echo ""
echo "6. 🔍 Checking Match Status"
echo "---------------------------"

# Check if users were matched
echo "User 1 Status After Matching:"
make_request GET "/api/random-video-calls/status/$REQUEST_ID_1"

echo ""
echo "User 2 Status After Matching:"
make_request GET "/api/random-video-calls/status/$REQUEST_ID_2"

echo ""
echo "7. ✅ Accepting Matched Call (User 1)"
echo "-------------------------------------"

make_request POST "/api/random-video-calls/accept/$REQUEST_ID_1"

echo ""
echo "8. ✅ Accepting Matched Call (User 2)"
echo "-------------------------------------"

make_request POST "/api/random-video-calls/accept/$REQUEST_ID_2"

echo ""
echo "9. 📞 Starting Video Call"
echo "-------------------------"

# Both users are now connected and can start WebRTC video call
echo "Users are now connected! WebRTC signaling data is available in the responses above."
echo "Frontend should use the roomId, peerId, and signaling data to establish WebRTC connection."

echo ""
echo "10. 🏁 Ending Call"
echo "------------------"

make_request POST "/api/random-video-calls/end/$REQUEST_ID_1"
make_request POST "/api/random-video-calls/end/$REQUEST_ID_2"

echo ""
echo "✅ Random Video Call Test Complete!"
echo "=================================="
echo ""
echo "How it works:"
echo "1. Users request random video calls with preferences"
echo "2. Server adds them to a queue"
echo "3. Background scheduler matches compatible users"
echo "4. Matched users get WebRTC signaling data"
echo "5. Users connect directly via WebRTC (peer-to-peer)"
echo "6. Server acts as signaling server only"
echo ""
echo "Key Features:"
echo "- Queue-based matching system"
echo "- Preference-based matching (gender, age, language)"
echo "- Priority users get matched first"
echo "- Automatic timeout handling"
echo "- WebRTC peer-to-peer video calls"
echo "- Real-time queue statistics" 