package com.programming.techie.springredditclone;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

/**
 * Base test class for integration tests.
 * Provides common configuration and utilities for all test classes.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseTest {

    static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:latest")
            .withDatabaseName("spring-reddit-test-db")
            .withUsername("testuser")
            .withPassword("pass")
            .withReuse(true);

    static {
        mySQLContainer.start();
    }

    // Common test utilities and setup can be added here
    
    /**
     * Helper method to create test data
     */
    protected void setupTestData() {
        // Common test data setup
    }
    
    /**
     * Helper method to clean up test data
     */
    protected void cleanupTestData() {
        // Common test data cleanup
    }
}
