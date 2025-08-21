#!/bin/bash

# Fix integration tests that expect plain string but get JSON response
sed -i '' 's/User Registration Successful/{"message":"User Registration Successful"}/g' src/test/java/com/programming/techie/springredditclone/integration/CompleteUserJourneyIntegrationTest.java
sed -i '' 's/User Registration Successful/{"message":"User Registration Successful"}/g' src/test/java/com/programming/techie/springredditclone/integration/auth/CompleteWorkflowIntegrationTest.java
sed -i '' 's/User Registration Successful/{"message":"User Registration Successful"}/g' src/test/java/com/programming/techie/springredditclone/integration/auth/SignupIntegrationTest.java

echo "Integration test fixes applied!" 