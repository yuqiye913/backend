#!/bin/bash

set -e

echo "Running AuthController signup unit tests..."
./mvnw test -Dtest=com.programming.techie.springredditclone.controller.auth.AuthControllerSignupTest


echo "Running AuthServiceImpl signup unit tests..."
./mvnw test -Dtest=com.programming.techie.springredditclone.service.auth.AuthServiceImplTest


echo "Running Auth signup integration tests..."
./mvnw test -Dtest=com.programming.techie.springredditclone.integration.auth.SignupIntegrationTest

echo "All signup-related auth tests passed!" 