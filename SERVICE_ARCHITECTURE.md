# Service Layer Architecture

## Overview

This project follows a clean architecture pattern where the service layer is separated into interfaces and implementations. This separation provides several benefits:

- **Framework Independence**: Interfaces are framework-agnostic and contain only business logic contracts
- **Testability**: Easy to mock interfaces for unit testing
- **Maintainability**: Clear separation of concerns
- **Flexibility**: Multiple implementations can be created for different scenarios

## Directory Structure

```
service/
├── AuthService.java                    # Interface
├── MailService.java                    # Interface
├── PostService.java                    # Interface
├── RefreshTokenService.java            # Interface
├── CommentService.java                 # Interface
├── SubredditService.java               # Interface
├── VoteService.java                    # Interface
└── impl/                              # Implementation directory
    ├── AuthServiceImpl.java           # Implementation with Spring dependencies
    ├── MailServiceImpl.java           # Implementation with Spring dependencies
    ├── PostServiceImpl.java           # Implementation with Spring dependencies
    ├── RefreshTokenServiceImpl.java   # Implementation with Spring dependencies
    ├── CommentServiceImpl.java        # Implementation with Spring dependencies
    ├── SubredditServiceImpl.java      # Implementation with Spring dependencies
    ├── VoteServiceImpl.java           # Implementation with Spring dependencies
    ├── MailContentBuilder.java        # Utility class with Spring dependencies
    └── UserDetailsServiceImpl.java    # Spring UserDetailsService implementation
```

## Interface Design Principles

### 1. Framework Independence
- Interfaces contain only business logic contracts
- No Spring annotations or framework-specific imports
- Pure Java interfaces with clear method signatures

### 2. Documentation
- All public methods are documented with JavaDoc
- Clear parameter and return value descriptions
- Business purpose explanation

### 3. Naming Convention
- Interface names: `ServiceName` (e.g., `AuthService`)
- Implementation names: `ServiceNameImpl` (e.g., `AuthServiceImpl`)

## Implementation Design Principles

### 1. Framework Integration
- All Spring annotations (`@Service`, `@Transactional`, etc.)
- Framework-specific dependencies and imports
- Configuration and dependency injection

### 2. Business Logic
- Implementation of interface contracts
- Data access through repositories
- Business rules and validation

### 3. Error Handling
- Framework-specific exception handling
- Logging and monitoring
- Transaction management

## Example: AuthService

### Interface (`AuthService.java`)
```java
public interface AuthService {
    void signup(RegisterRequest registerRequest);
    User getCurrentUser();
    void verifyAccount(String token);
    AuthenticationResponse login(LoginRequest loginRequest);
    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    boolean isLoggedIn();
}
```

### Implementation (`AuthServiceImpl.java`)
```java
@Service
@AllArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    // ... other dependencies
    
    @Override
    public void signup(RegisterRequest registerRequest) {
        // Implementation with Spring framework dependencies
    }
    
    // ... other method implementations
}
```

## Benefits of This Architecture

### 1. Testing
- Easy to create mock implementations for unit tests
- Interface-based testing without framework dependencies
- Clear separation between business logic and framework code

### 2. Maintenance
- Changes to framework code don't affect interface contracts
- Business logic is isolated from technical implementation
- Clear responsibility boundaries

### 3. Flexibility
- Multiple implementations can exist (e.g., different authentication providers)
- Easy to swap implementations without changing consuming code
- Framework migration is easier

### 4. Code Organization
- Clear separation of concerns
- Framework code is isolated in impl directory
- Business contracts are clearly defined

## Migration Guide

When converting existing services to this pattern:

1. **Extract Interface**: Move method signatures to interface
2. **Add Documentation**: Document all public methods
3. **Create Implementation**: Move existing code to impl class
4. **Add Framework Annotations**: Add `@Service`, `@Transactional`, etc.
5. **Update Dependencies**: Ensure proper imports and dependencies
6. **Test**: Verify functionality remains the same

## Best Practices

1. **Keep Interfaces Simple**: Only include business logic contracts
2. **Document Everything**: Use JavaDoc for all public methods
3. **Follow Naming Conventions**: Consistent naming across the project
4. **Separate Concerns**: Framework code stays in implementations
5. **Test Both Layers**: Unit test interfaces and integration test implementations 