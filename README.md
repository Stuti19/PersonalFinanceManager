# Personal Finance Manager

A comprehensive personal finance management system built with Spring Boot that enables users to track income, expenses, and savings goals through a robust REST API.

## Features

- **User Management**: Registration, login, logout with session-based authentication
- **Transaction Management**: Full CRUD operations for financial transactions
- **Category Management**: Default and custom categories for income/expenses
- **Savings Goals**: Create and track progress towards financial goals
- **Reports**: Monthly and yearly financial reports with detailed analytics
- **Data Security**: Complete data isolation between users

## Technology Stack

- **Java**: 17+
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with session-based authentication
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd personal-finance-manager
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Testing

### Unit Tests

Run unit tests with coverage:
```bash
mvn test
mvn jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

### Integration Tests

Run integration tests:
```bash
mvn test -Dtest=PersonalFinanceManagerIntegrationTest
```

### API Testing Script

Run the comprehensive API test script:
```bash
# Start the application first
mvn spring-boot:run

# In another terminal, run the test script
bash financial_manager_tests.sh
```

The test script validates all 12 core API endpoints and ensures proper authentication and data isolation.

### Test Coverage

The project maintains >80% code coverage with comprehensive unit tests for:
- All service layer methods
- All controller endpoints
- Authentication and authorization
- Data validation and error handling
- User data isolation

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

#### Logout
```http
POST /api/auth/logout
```

### Transaction Endpoints

#### Create Transaction
```http
POST /api/transactions
Content-Type: application/json

{
  "amount": 50000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary"
}
```

#### Get Transactions
```http
GET /api/transactions
GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31&category=Salary
```

#### Update Transaction
```http
PUT /api/transactions/{id}
Content-Type: application/json

{
  "amount": 60000.00,
  "description": "Updated January Salary"
}
```

#### Delete Transaction
```http
DELETE /api/transactions/{id}
```

### Category Endpoints

#### Get All Categories
```http
GET /api/categories
```

#### Create Custom Category
```http
POST /api/categories
Content-Type: application/json

{
  "name": "SideBusinessIncome",
  "type": "INCOME"
}
```

#### Delete Custom Category
```http
DELETE /api/categories/{name}
```

### Savings Goals Endpoints

#### Create Goal
```http
POST /api/goals
Content-Type: application/json

{
  "goalName": "Emergency Fund",
  "targetAmount": 5000.00,
  "targetDate": "2026-01-01",
  "startDate": "2025-01-01"
}
```

#### Get All Goals
```http
GET /api/goals
```

#### Get Goal
```http
GET /api/goals/{id}
```

#### Update Goal
```http
PUT /api/goals/{id}
Content-Type: application/json

{
  "targetAmount": 6000.00,
  "targetDate": "2026-02-01"
}
```

#### Delete Goal
```http
DELETE /api/goals/{id}
```

### Reports Endpoints

#### Monthly Report
```http
GET /api/reports/monthly/{year}/{month}
```

#### Yearly Report
```http
GET /api/reports/yearly/{year}
```

## Default Categories

### Income Categories
- Salary

### Expense Categories
- Food
- Rent
- Transportation
- Entertainment
- Healthcare
- Utilities

## Error Handling

The API returns appropriate HTTP status codes:

- `200 OK` - Success
- `201 Created` - Resource created successfully
- `400 Bad Request` - Validation errors or malformed input
- `401 Unauthorized` - Invalid credentials or expired session
- `403 Forbidden` - Accessing other user's data
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resources

Error responses include descriptive messages:
```json
{
  "message": "Error description"
}
```

## Architecture

The application follows a layered architecture:

```
Controller Layer → Service Layer → Repository Layer → Database
```

- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic and validation
- **Repositories**: Data access layer
- **Entities**: JPA entities representing database tables
- **DTOs**: Data transfer objects for API requests/responses

## Security

- Session-based authentication with secure cookies
- Password encryption using BCrypt
- Data isolation between users
- Input validation and sanitization
- CSRF protection disabled for API usage

## Database Schema

The application uses H2 in-memory database with the following entities:

- **Users**: User account information
- **Categories**: Default and custom transaction categories
- **Transactions**: Financial transactions
- **Savings Goals**: User-defined savings targets

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/syfe/finance/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # Data repositories
│   │   └── service/        # Business logic
│   └── resources/
│       └── application.properties
└── test/                   # Unit tests
```

### Adding New Features

1. Create entity classes in `entity/` package
2. Add repository interfaces in `repository/` package
3. Implement business logic in `service/` package
4. Create DTOs in `dto/` package
5. Add REST endpoints in `controller/` package
6. Write unit tests

## Deployment

### Local Development

1. Ensure Java 17+ is installed
2. Set JAVA_HOME environment variable
3. Run the application:
```bash
mvn spring-boot:run
```

### Production Deployment

The application can be deployed to various platforms:

#### Render Deployment
1. Create a new Web Service on Render
2. Connect your GitHub repository
3. Set build command: `mvn clean package`
4. Set start command: `java -jar target/personal-finance-manager-1.0.0.jar`
5. Set environment variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `SERVER_PORT=8080`

#### Heroku Deployment
1. Create Procfile: `web: java -jar target/personal-finance-manager-1.0.0.jar`
2. Deploy using Heroku CLI or GitHub integration

#### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/personal-finance-manager-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Configuration

For production, configure these properties in `application-prod.properties`:
```properties
server.port=${PORT:8080}
spring.datasource.url=jdbc:h2:mem:financedb
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.com.syfe.finance=INFO
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Ensure 80%+ code coverage
5. Submit a pull request

## License

This project is licensed under the MIT License.