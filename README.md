# Financial Pulse

A modern microservices-based social financial platform built with Spring Boot 3, designed to allow users to share financial insights, follow other users, and engage with financial content.

## Architecture Overview

Financial Pulse follows a microservices architecture pattern with three core services:

- **Account Service** - User registration, authentication, and profile management
- **Content Service** - Post creation, management, and interactions (likes, comments)
- **Follow Service** - Social graph management (follow/unfollow relationships)
- **Common Module** - Shared utilities, configurations, and domain models

### Service Communication

- Services communicate via REST APIs using Spring WebClient
- JWT-based authentication across all services
- Shared security configurations through the common module

## Technology Stack

### Core Framework

- **Spring Boot 3.x** - Microservices framework
- **Spring Security** - Authentication and authorization
- **Spring Security OAuth2 Resource Server** - JWT token validation
- **Spring Data JPA** - ORM for relational data
- **Spring WebFlux** (WebClient) - Non-blocking service communication

### Database & Storage

- **PostgreSQL** - Primary database for relational data
- **AWS DynamoDB** - NoSQL store for account data and revoked JWT tokens
- **Database per Service** pattern implementation

### AWS Services Integration

- **EC2** - Application hosting
- **DynamoDB** - Account storage and JWT revocation tracking
- **SES (Simple Email Service)** - Email notifications and verification
- **CloudFormation** - Infrastructure as Code
- **Future integrations**: RDS/Aurora, SQS, SNS, Lambda, API Gateway

### DevOps & Deployment

- **GitHub Actions** - CI/CD pipeline
- **Maven** - Build and dependency management
- **Docker** (planned) - Containerization
- **Environment-based deployment** (dev, test, prod)

## Security Features

### JWT Authentication

- Custom JWT token generation and validation
- Access tokens and refresh tokens with different expiration times
- Token type validation filter for endpoint-specific token requirements
- Revoked token tracking using DynamoDB
- Stateless authentication across microservices

### Spring Security Configuration

- OAuth2 Resource Server for JWT validation
- Custom authentication entry points and access denied handlers
- Public and protected endpoint configuration
- Security auto-configuration in common module

## Key Features

### Account Management

- User registration with email verification
- Login with JWT token generation
- Profile management (update name, password, labels)
- Account verification via email
- Password reset functionality

### Content Management

- Create, read, update, delete posts
- Rich content support with images
- Like and comment on posts
- Content categorization with labels
- View counts tracking

### Social Features

- Follow/unfollow users
- View follower and following lists
- Social feed based on followed users
- User discovery

## Project Structure

```
financial-pulse/
├── fp-account/          # Account service
├── fp-content/          # Content management service
├── fp-follow/           # Follow relationship service
├── fp-common/           # Shared utilities and configurations
├── .github/workflows/   # CI/CD pipelines
└── cloudformation/      # AWS infrastructure templates
```

### Common Module Features

- JWT token service and utilities
- Security auto-configuration
- DynamoDB repository base classes
- Shared DTOs and exceptions
- Common constants and properties

## Configuration

### Environment Variables

```properties
# JWT Configuration
JWT_SECRET=your-secret-key

# AWS Configuration
AWS_REGION=us-west-1
AWS_SES_FROM_EMAIL=noreply@yourdomain.com

# Service URLs
SERVICES_ACCOUNT_URL=http://localhost:8080
SERVICES_CONTENT_URL=http://localhost:8081
SERVICES_FOLLOW_URL=http://localhost:8082

# Spring Profiles
SPRING_PROFILES_ACTIVE=dev
```

### Database Configuration

Each service maintains its own database:

- `fp_account_dev` - Account table -- DynamoDB
- `fp_content_dev` - Content service database -- PostgreSQL
- `fp_follow_dev` - Follow service database -- PostgreSQL
- `revoked_jwt` - revoked jwt table -- DynamoDB

## Getting Started

### Prerequisites

- Java 17
- Maven 3.6+
- PostgreSQL 14+
- AWS Account (for DynamoDB and SES)
- AWS CLI configured

### Local Development Setup

1. Clone the repository

```bash
git clone https://github.com/yourusername/financial-pulse.git
cd financial-pulse
```

2. Install parent POM and common module

```bash
mvn clean install -N
mvn clean install -pl fp-common
```

3. Use cloudformation to set up insfrastructures for three services on AWS.

4. Set up environment variables for each service.

```bash
export JWT_SECRET=your-secret-key
export AWS_REGION=us-west-1
export AWS_SES_FROM_EMAIL=noreply@yourdomain.com
...
```

5. Run each service on EC2.

## API Documentation

Each service provides Swagger UI documentation:

- Account Service: http://localhost:8080/swagger-ui.html
- Content Service: http://localhost:8081/swagger-ui.html
- Follow Service: http://localhost:8082/swagger-ui.html

## Deployment

### AWS Deployment via GitHub Actions

The project uses GitHub Actions for automated deployment:

1. Push to master branch triggers the deployment
2. Builds the application and creates JAR artifacts
3. Deploys to EC2 instances using SSH
4. Manages environment-specific configurations

### CloudFormation Infrastructure

Infrastructure is managed using AWS CloudFormation:

- EC2 instances with proper IAM roles
- DynamoDB tables for account and JWT storage
- Security groups with appropriate access rules
- Auto-scaling configurations (planned)

### Deployment Environments

- **Development** - Continuous deployment from master branch
- **Test** - Manual deployment with approval
- **Production** - Manual deployment with multi-stage approval

## API Endpoints Overview

### Account Service

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout and revoke tokens
- `GET /api/auth/verify` - Email verification

### Content Service


### Follow Service


## Monitoring and Logging

- Structured logging with SLF4J
- Environment-specific log levels
- AWS CloudWatch integration (planned)
- Application metrics with Micrometer (planned)

