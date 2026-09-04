# Demo project — Library REST API

Spring Boot REST API project for managing books and borrowers. The application uses an H2 in-memory database and provides functionality to add books, borrow/return books, create borrowers, and retrieve borrower information and their borrowed books.

The project also demonstrates automated testing, code coverage, API documentation, and basic observability through OpenTelemetry and Micrometer.

## Prerequisites
- Java 21+ 
- Maven 3.9+

## Technologies & Tools:
- Application: Java 21, Maven, Spring Boot, Spring Web / REST, Spring Data JPA, H2 Database, Lombok
- API Documentation: OpenAPI, Swagger UI
- Testing: JUnit 5, Mockito, MockMvc, AssertJ, Hamcrest, JaCoCo, Surefire & Failsafe plugins
- Observability: OpenTelemetry, Micrometer, Spring Boot Actuator

## Running the Application

Start the application without running the build lifecycle:
````
mvn spring-boot:run
````
Build the application and execute unit and integration tests:
````
mvn clean install
````
Build the application without running integration tests:
````
mvn clean install -Dskip.integration.tests=true
````

## API

The API is divided into three main book operations:

- ```GET /books``` — returns currently available books.
- ```POST /books``` — adds a new book to the library.
- ```PATCH /books/{id}/borrow?borrower_id={id}``` — borrows an available book or returns a book when the same borrower calls the endpoint again.

Borrower-related operations are also available:

- ```POST /borrowers``` — creates a new borrower.
- ```GET /borrowers/{id}``` — retrieve a borrower.
- ```GET /borrowers/{id}/books``` — retrieve books currently borrowed by a borrower.

## Swagger UI 

When the application is running, the API can be explored through Swagger UI:
http://localhost:8080/swagger-ui.html

## Code Coverage

JaCoCo is configured for code coverage.
After running:
````
mvn clean install
````
the generated JaCoCo report is available at:
target/site/jacoco/index.html

## Database

The application uses an H2 in-memory database. The database schema and initial test/development data are initialized when the application starts.

## OpenTelemetry & Observability

The application contains focused instrumentation around the main business operation:

- Metric: books.borrow.total
  - Counts successful book borrow operations.
  - Available through Spring Boot Actuator at: http://localhost:8080/actuator/metrics/books.borrow.total

- Span: books.borrow
  - Implemented using Micrometer's @Observed instrumentation around the book borrowing operation.
  - When the borrow endpoint is called, the generated span is available through the application logs.
  - Spring Boot also automatically traces HTTP requests.
- OpenTelemetry tracing uses the logging exporter for local verification.
- No OpenTelemetry receiver/backend is configured; therefore, traces are not exported to an external tracing system.

The instrumentation intentionally focuses on the book-borrowing operation because it represents the main business transaction of the application.

