# Test Structure and Guidelines

## Overview

This project follows a comprehensive testing strategy with different types of tests organized in a clear directory structure. The test structure supports unit tests, integration tests, and end-to-end tests.

## Directory Structure

```
src/test/
├── java/com/programming/techie/springredditclone/
│   ├── BaseTest.java                           # Base class for all tests
│   ├── unit/                                   # Unit tests (no Spring context)
│   │   └── UnitTestBase.java                   # Base class for unit tests
│   ├── integration/                            # Integration tests (full Spring context)
│   │   └── IntegrationTestBase.java            # Base class for integration tests
│   ├── controller/                             # Controller tests
│   │   ├── auth/                               # Authentication controller tests
│   │   ├── user/                               # User controller tests
│   │   ├── matching/                           # Matching controller tests
│   │   ├── communication/                      # Communication controller tests
│   │   └── content/                            # Content controller tests
│   ├── service/                                # Service tests
│   │   ├── auth/                               # Authentication service tests
│   │   ├── user/                               # User service tests
│   │   ├── matching/                           # Matching service tests
│   │   ├── communication/                      # Communication service tests
│   │   └── content/                            # Content service tests
│   ├── repository/                             # Repository tests
│   │   ├── auth/                               # Authentication repository tests
│   │   ├── user/                               # User repository tests
│   │   ├── matching/                           # Matching repository tests
│   │   ├── communication/                      # Communication repository tests
│   │   └── content/                            # Content repository tests
│   └── utils/                                  # Test utilities and helpers
└── resources/
    ├── application-test.properties             # Test-specific configuration
    ├── test-data/                              # Test data files
    │   ├── sample-users.sql                    # Sample user data
    │   ├── sample-posts.sql                    # Sample post data
    │   └── sample-subreddits.sql               # Sample subreddit data
    ├── test-config/                            # Test configuration files
    └── test-properties/                        # Additional test properties
```

## Test Types

### 1. Unit Tests (`unit/`)
- **Purpose**: Test individual components in isolation
- **Scope**: Single class or method
- **Dependencies**: Mocked using Mockito
- **Speed**: Fast execution
- **Base Class**: `UnitTestBase`

**Example**:
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest extends UnitTestBase {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private AuthServiceImpl authService;
    
    @Test
    void testSignup_Success() {
        // Test implementation
    }
}
```

### 2. Integration Tests (`integration/`)
- **Purpose**: Test component interactions
- **Scope**: Multiple components working together
- **Dependencies**: Real Spring context, in-memory database
- **Speed**: Medium execution
- **Base Class**: `IntegrationTestBase`

**Example**:
```java
@SpringBootTest
@AutoConfigureWebMvc
class AuthIntegrationTest extends IntegrationTestBase {
    @Test
    void testSignupFlow() {
        // Test full signup flow
    }
}
```

### 3. Controller Tests (`controller/`)
- **Purpose**: Test REST endpoints
- **Scope**: HTTP requests and responses
- **Dependencies**: MockMvc, mocked services
- **Speed**: Fast execution
- **Annotations**: `@WebMvcTest`

**Example**:
```java
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @Test
    void testSignupEndpoint() {
        // Test HTTP endpoint
    }
}
```

### 4. Repository Tests (`repository/`)
- **Purpose**: Test data access layer
- **Scope**: Database operations
- **Dependencies**: Real database (H2 for tests)
- **Speed**: Medium execution
- **Annotations**: `@DataJpaTest`

**Example**:
```java
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByUsername() {
        // Test repository method
    }
}
```

## Test Organization by Domain

### Authentication Domain (`auth/`)
- User registration and login
- JWT token management
- Password validation
- Account verification

### User Domain (`user/`)
- User profile management
- User settings
- User demographics and personality
- User introductions

### Matching Domain (`matching/`)
- User matching algorithms
- Match scoring
- Friend requests and responses
- Match preferences

### Communication Domain (`communication/`)
- Direct messages
- Phone calls
- Friend requests
- Notifications

### Content Domain (`content/`)
- Posts and comments
- Subreddits
- Voting system
- Content moderation

## Test Configuration

### Test Properties (`application-test.properties`)
```properties
# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# JWT
jwt.secret=testSecretKey
jwt.expiration=900000

# Logging
logging.level.com.programming.techie.springredditclone=DEBUG
```

### Test Data (`test-data/`)
- SQL files with sample data
- JSON files for API testing
- CSV files for bulk data testing

## Best Practices

### 1. Naming Conventions
- Test classes: `{ClassName}Test`
- Test methods: `test{MethodName}_{Scenario}`
- Test data: `{EntityName}TestData`

### 2. Test Structure (AAA Pattern)
```java
@Test
void testMethodName_Scenario() {
    // Arrange (Given)
    // Setup test data and mocks
    
    // Act (When)
    // Execute the method under test
    
    // Assert (Then)
    // Verify the results
}
```

### 3. Test Data Management
- Use `@BeforeEach` for setup
- Use `@AfterEach` for cleanup
- Use test data builders for complex objects
- Use SQL scripts for database setup

### 4. Mocking Guidelines
- Mock external dependencies
- Mock database repositories in service tests
- Mock services in controller tests
- Use real repositories in repository tests

### 5. Assertions
- Use descriptive assertion messages
- Test both positive and negative scenarios
- Verify all side effects
- Test edge cases and error conditions

## Running Tests

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Category
```bash
# Unit tests only
./mvnw test -Dtest="*UnitTest"

# Integration tests only
./mvnw test -Dtest="*IntegrationTest"

# Controller tests only
./mvnw test -Dtest="*ControllerTest"
```

### Run Tests by Domain
```bash
# Auth tests only
./mvnw test -Dtest="*auth*"

# User tests only
./mvnw test -Dtest="*user*"
```

### Run with Coverage
```bash
./mvnw test jacoco:report
```

## Test Utilities

### BaseTest
- Common configuration for all tests
- Shared test utilities
- Database setup and cleanup

### UnitTestBase
- Mockito configuration
- Common mock setup
- Unit test utilities

### IntegrationTestBase
- Spring context configuration
- MockMvc setup
- JSON serialization utilities

### Test Data Builders
- Builder pattern for test objects
- Consistent test data creation
- Easy to maintain and modify

## Continuous Integration

### Test Execution
- All tests run on every commit
- Separate test environments for different stages
- Coverage reporting and thresholds

### Quality Gates
- Minimum 80% code coverage
- All tests must pass
- No critical security vulnerabilities
- Performance benchmarks met

## Troubleshooting

### Common Issues
1. **Test Database Issues**: Check H2 configuration
2. **Mock Problems**: Verify Mockito setup
3. **Context Loading**: Check component scanning
4. **Transaction Issues**: Verify @Transactional usage

### Debugging Tips
- Use `@DirtiesContext` for stateful tests
- Enable SQL logging for database issues
- Use `@TestPropertySource` for custom properties
- Use `@ActiveProfiles` for profile-specific tests 