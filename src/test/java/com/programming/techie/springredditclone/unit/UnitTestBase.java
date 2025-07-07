package com.programming.techie.springredditclone.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base class for unit tests.
 * Provides common setup for unit tests that don't require Spring context.
 */
@ExtendWith(MockitoExtension.class)
public abstract class UnitTestBase {
    
    @BeforeEach
    void setUp() {
        // Common setup for unit tests
        setupTestData();
    }
    
    /**
     * Setup test data for unit tests
     */
    protected void setupTestData() {
        // Override in subclasses if needed
    }
    
    /**
     * Helper method to create mock objects
     */
    protected <T> T createMock(Class<T> clazz) {
        // Mock creation utility
        return null; // Implement as needed
    }
} 