# Cash-Register-Backend Project For 32Bit-2024

Cash Register Backend is a collection of microservices built with Spring Boot to manage transactions and inventory for a retail cash register system.

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

## Storage

- PostgreSQL

## Containerization

- Docker Compose
- Google Jib
  
## Authentication

- Token-based with JWT
  
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
- Cloudinary (for image management)
- WebClient (for reactive programming)
- Jacoco (for test coverage)
  
## Getting Started

To get started with the Cash Register Backend, follow these steps:

1. Clone the repository: ```git clone https://github.com/emirhancebiroglu/cash_register_backend.git```

2. Navigate to the project directory: 'cd cash-register-backend'

3. Configure each service's 'application-docker.properties' file according to your environment and requirements.
   
   <img width="241" alt="prop" src="https://github.com/emirhancebiroglu/cash_register_backend/assets/152030621/d03101e2-6163-4027-b0ea-f8464a78fcd0">

5. Build and run the Docker containers using Docker Compose: ```docker-compose up -d```
   
## Usage

1. JWT Auth Service: Perform login and obtain JWT token to access to services. Note that there is an admin user craeted by system defaulty so that you can access to User Management Service's endpoints and add your own admin(s). use "admin" as both user code and password to login and obtain the jwt.

   <img width="635" alt="admin" src="https://github.com/emirhancebiroglu/cash_register_backend/assets/152030621/dca9b271-4a1e-4a45-922f-8f97a0128963">
   
3. User Management Service: Use this service to handle with user related operations (CRUD).
   
4. Product Service: Manage products and inventory.

5. Sales Service: Record and manage sales transactions.

6. Reporting Service: Generate reports based on sales data.

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

This project is licensed under the MIT License - see the [LICENCE.txt](LICENCE.txt) file for details.

## Contact

For any questions or feedback, please contact me at: ```emirhancebiroglu21@hotmail.com```.
