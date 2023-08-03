# Crypto Recommendations Service

Welcome to the Crypto Recommendations Service! This Spring Boot application provides cryptocurrency investment
recommendations based on historical data.

## Prerequisites

Before you begin, make sure you have the following installed on your system:

- Java 11 or higher
- Docker (if you plan to use Docker for containerization)

## Getting Started

1. Clone this repository to your local machine:

   ```bash
   git clone https://github.com/yourusername/crypto-recommendations.git

2. Navigate to the project directory:

   ```bash
   cd crypto-recommendations

3. Build the Spring Boot application:

   ```bash
   ./gradlew build
   
(On Windows, use gradlew.bat build)

## Running the Application
   Using Docker (Recommended)
1. Build the Docker image:

   ```bash
   docker build -t crypto-recommendations .
   
2. Run the Docker container:

   ```bash 
   docker run -p 8080:8080 crypto-recommendations
Access the application in your browser at: http://localhost:8080

Without Docker
1. Run the Spring Boot application:

   ```bash 
   ./gradlew bootRun
(On Windows, use gradlew.bat bootRun)

Access the application in your browser at: http://localhost:8080

## API Documentation
You can access the API documentation using Swagger UI:

Swagger UI: http://localhost:8080/swagger-ui/

## Configuration
The application uses the application.properties file for configuration. You can adjust properties such as the CSV data folder path and more.

## Contributing
If you'd like to contribute to this project, feel free to open an issue or submit a pull request!

## Happy crypto investing!
