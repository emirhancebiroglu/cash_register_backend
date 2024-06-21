# Cash-Register-Backend Project For 32Bit-2024

The Cash Register Backend is an integrated suite of microservices and essential backend technologies designed to support a retail cash register system. Developed using Spring Boot, this project facilitates the management of transactions and inventory. It enables role-based user management, supporting roles such as admin, cashier, and store manager. Additionally, the project offers comprehensive features for product management, including CDN support and stock tracking. It also provides robust functionalities for handling orders and generating order reports.

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
- Zipkin (for distributed tracing)
- Grafana (for monitoring and visualization)
- Prometheus (for metrics collection and monitoring)
- Actuator (for application monitoring and management)
- Apache Poi (for exporting data to an excel file)
- Resilience4j (for fault tolerance and resilience patterns)
  
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
   
3. User Management Service: Utilize this service to manage user-related operations (CRUD). When registering a user in the system, please use an existing email address, as the login credentials (user code and system-generated password) will be sent to that email. Subsequently, you can log in using this information and change the password using the relevant endpoint.
   
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
