# RadMan Test Suite

This directory contains comprehensive unit, integration, and functional tests for the RadMan application.

## Test Structure

```
src/test/
├── java/
│   └── software/netcore/radman/
│       ├── RadmanApplicationTest.java          # Application context and smoke tests
│       ├── TestConfiguration.java              # Test configuration beans
│       ├── buisness/
│       │   └── service/
│       │       ├── accounting/
│       │       │   └── AccountingServiceTest.java
│       │       ├── nas/
│       │       │   ├── NasServiceTest.java
│       │       │   └── converter/
│       │       │       ├── DtoToNasConverterTest.java
│       │       │       └── NasToDtoConverterTest.java
│       │       ├── security/
│       │       │   └── SecurityServiceTest.java
│       │       └── user/
│       │           └── radius/
│       │               └── RadiusUserServiceTest.java
│       ├── data/
│       │   ├── internal/
│       │   │   └── repo/
│       │   │       └── RadiusUserRepoTest.java
│       │   └── radius/
│       │       └── repo/
│       │           └── NasRepoTest.java
│       └── integration/
│           ├── NasIntegrationTest.java
│           └── RadiusUserIntegrationTest.java
└── resources/
    └── application-test.properties             # Test configuration

```

## Test Categories

### 1. Unit Tests
Unit tests verify individual components in isolation using mocks and stubs.

**Service Layer Tests:**
- `RadiusUserServiceTest` - Tests for RADIUS user management operations
- `NasServiceTest` - Tests for Network Access Server (NAS) management
- `SecurityServiceTest` - Tests for security and authentication logic
- `AccountingServiceTest` - Tests for accounting record management

**Converter Tests:**
- `NasToDtoConverterTest` - Tests entity to DTO conversion
- `DtoToNasConverterTest` - Tests DTO to entity conversion

**Repository Tests:**
- `RadiusUserRepoTest` - Tests for RadiusUser repository with in-memory database
- `NasRepoTest` - Tests for NAS repository with in-memory database

### 2. Integration Tests
Integration tests verify multiple components working together with real database connections.

- `NasIntegrationTest` - End-to-end NAS management flow
- `RadiusUserIntegrationTest` - End-to-end user management flow including RADIUS data

### 3. Application Tests
- `RadmanApplicationTest` - Smoke tests ensuring application context loads and beans are properly configured

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=NasServiceTest
```

### Run Integration Tests Only
```bash
mvn test -Dtest=*IntegrationTest
```

### Run Unit Tests Only
```bash
mvn test -Dtest=*Test -Dtest=!*IntegrationTest
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

## Test Configuration

Tests use in-memory HSQLDB databases for both RadMan and Radius data sources. Configuration is in `application-test.properties`.

### Key Configuration:
- **Database**: HSQLDB in-memory
- **Auto-login**: Disabled
- **Liquibase**: Enabled for schema management
- **JPA**: DDL auto set to create-drop

## Writing New Tests

### Unit Test Template
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    private MyRepository repository;
    
    @InjectMocks
    private MyService service;
    
    @Test
    void myTest_ShouldDoSomething() {
        // Arrange
        // Act
        // Assert
    }
}
```

### Integration Test Template
```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class MyIntegrationTest {
    @Autowired
    private MyService service;
    
    @Test
    void myIntegrationTest() {
        // Arrange
        // Act
        // Assert
    }
}
```

## Test Dependencies

The following dependencies are included via `spring-boot-starter-test`:
- JUnit 5 (Jupiter)
- Mockito
- AssertJ
- Spring Test
- Spring Boot Test

## Best Practices

1. **Arrange-Act-Assert**: Structure tests with clear setup, execution, and verification phases
2. **Test Isolation**: Each test should be independent and not rely on other tests
3. **Descriptive Names**: Use descriptive test method names that explain what is being tested
4. **Mock External Dependencies**: Use mocks for external services and repositories in unit tests
5. **Use Real Database in Integration Tests**: Integration tests use in-memory databases
6. **Clean Up**: Use `@Transactional` or explicit cleanup in `@BeforeEach`

## Coverage Goals

- **Service Layer**: 80%+ code coverage
- **Repository Layer**: 70%+ code coverage
- **Converter Layer**: 90%+ code coverage
- **Overall**: 75%+ code coverage

## Continuous Integration

Tests are automatically run in CI/CD pipelines. All tests must pass before merging to main branch.

## Troubleshooting

### Test Fails with Database Connection Error
- Check that `application-test.properties` is properly configured
- Ensure HSQLDB dependency is in pom.xml

### Mocking Issues
- Verify all dependencies are properly annotated with `@Mock`
- Ensure the class under test is annotated with `@InjectMocks`

### Integration Test Fails
- Check that Liquibase changesets are compatible with HSQLDB
- Verify test data setup in `@BeforeEach` methods
