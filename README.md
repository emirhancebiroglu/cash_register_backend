# Cash-Register-Backend Project For 32Bit-2024

Cash Register Backend is a collection of microservices built with Spring Boot to manage transactions and inventory for a retail cash register system. It includes services for product management, user management, sales tracking, reporting, and JWT token-based authentication. Service discovery is facilitated through Eureka, and Spring Cloud Gateway serves as the API gateway. The project utilizes Docker for containerization and Google Jib for building optimized container images. Additionally, it integrates various technologies such as Kafka for event streaming, Java Mail Sender for email notifications, Thymeleaf for email templating, Passay for password validation, Apache PDFBox for PDF generation, and Log4j2 for logging.

## Microservices

- Product Service
- User Management Service
- Sales Service
- Reporting Service
- JWT Auth Service

## Service Discovery

- Eureka

## Gateway

- Spring Cloud Gateway

## Containerization

- Docker
- Docker Compose
- Google Jib
  
## Authentication

- Token-based authentication with JWT
  
## Logs

- Log4j2
  
## Testing

- JUnit 5
  
## Other Technologies

- Kafka (for event streaming)
- Java Mail Sender (for email notifications)
- Thymeleaf (for email templating)
- Passay (for password validation)
- Apache PDFBox (for PDF generation)
  
## Getting Started

To get started with the Cash Register Backend, follow these steps:

1. Clone the repository: ```git clone https://github.com/emirhancebiroglu/cash_register_backend.git```

2. Navigate to the project directory: 'cd cash-register-backend'

3. Configure each service's 'application-docker.properties' file according to your environment and requirements.
   
   ![application-docker](https://github.com/emirhancebiroglu/cash_register_backend/assets/152030621/5412d9a2-b012-4b40-8392-2cbdd595c8f6)

5. Build and run the Docker containers using Docker Compose: ```docker-compose up -d```
   
## Usage

1. User Management Service: Register as a new user.

2. JWT Auth Service: Obtain JWT tokens for authentication and access control.

3. Product Service: Manage products and inventory.

4. Sales Service: Record and manage sales transactions.

5. Reporting Service: Generate reports based on sales data.

## API Documentation

API documentation for each service is available via Postman collection. Follow these steps to access and import the collection:

Link for the collection : ```https://api.postman.com/collections/30227502-9d39eaf3-a47b-4ff0-9a25-f6141d490ea9?access_key=PMAT-01HWEES7SHM6J62VCSQDBR5MQF```

To import the Postman collection:

1. Copy the provided collection link.
2. Open Postman and go to the "Import" button in the top-left corner.

   <img width="302" alt="import" src="https://github.com/emirhancebiroglu/cash_register_backend/assets/152030621/06942162-ae9f-485e-a491-58416e692887">
   
4. Paste the link and import it into Postman.
   
   <img width="485" alt="paste" src="https://github.com/emirhancebiroglu/cash_register_backend/assets/152030621/6170d20e-5f5e-4fb0-9c4c-9a349e75925e">
   
6. Once imported, you'll have access to all the endpoints and can start making requests to the respective services.

   <img width="282" alt="access" src="https://github.com/emirhancebiroglu/cash_register_backend/assets/152030621/d0603ea2-91fa-4f1d-a0ad-74d500f7c9bd">

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.

## Contact

For any questions or feedback, please contact Your Name.

## Acknowledgements

- Spring Boot
- Eureka Service Discovery
- Spring Cloud Gateway
- JWT
- Docker
- Google Jib
- Kafka
- Java Mail Sender
- Thymeleaf
- Passay
- Apache PDFBox
- Log4j2
- JUnit 5
