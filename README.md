<p align="center">
  <img width="400" alt="logo" src="https://github.com/user-attachments/assets/31b45c82-ec96-4e2b-9a2d-401284dc4cd6">
</p>

<p align="center">This backend project is an integrated suite of microservices and essential backend technologies designed to support a retail cash register system. </p>

<p align="center" style="font-size:14px">
  <a href="#services">Services</a> |
  <a href="#roles-and-permissions">Roles and Permissions</a> |
  <a href="#technology-stack">Technology Stack</a> |
  <a href="#getting-started">Getting Started</a> |
  <a href="#api-documentation">API Documentation</a> |
  <a href="#license">License</a> |
  <a href="#contact">Contact</a>
</p>

# Services

- **Product Service**
  - Manage products and inventory

  ### Key Features
  - Add new products with detailed information.
  - Update product details such as price, image, and stock.
  - Delete products from the inventory.
  - Search and filter products based on various criteria.
  - Add/Remove Products from Favorites.
  - Export products to excel.

- **User Management Service**
  - Manage user-related operations.

  ### Key Features
  - Register new users to the system.
  - Update user details.
  - Delete users from the system.
  - Email notifications for user-related activities.
  - Search and filter users based on various criteria.
  
> **Note:** Please use a real email address when registering a new user, as user credentials will be sent to that email.

- **Sales Service**
  - This service allows users to make sales with or without campaigns.

  ### Key Features
  - Fetch current sales campaigns and promotions.
  - Add selected products to the shopping bag.
  - Remove products from the shopping bag.
  - Process the return of purchased products.
  - Initiate a new checkout.
  - Finalize the purchase and complete the checkout process.
  - Cancel an ongoing checkout process.
  - Create and add new sales campaigns.
  - Reactivate a previously inactive campaign.
  - Modify details of existing sales campaigns.
  - Deactivate a sales campaign.


- **Reporting Service**
  - Manage sales reports.

  ### Key Features
  - **List Reports**: View a list of available reports and their details.
  - **Generate Receipts**: Create and provide receipts based on sales transactions.

- **JWT Auth Service**
  - Handle authentication for the system.

  ### Key Features
  - Authenticate users and issue JWT tokens.
  - Assist users in retrieving their user code.
  - Help users recover their passwords.
  - Allow users to update their passwords.
  - Provide new tokens for authenticated sessions.
  - End user sessions and invalidate tokens.
  
> **Note:** A default admin user is created to allow access to the User Management Service and add new users to the system. 
  > 
  > **Admin Credentials:**
  > - **User Code:** `admin`
  > - **Password:** `admin`
  >
  > When a new user with an admin role is added, the default admin user will be deleted.

# Roles and Permissions

The Cash Register Backend employs Role-Based Access Control (RBAC) to manage user permissions and access to various services within the system. This ensures that users have appropriate levels of access based on their roles.

<p align="center">
    <img width="680" alt="kubernetes" src="https://github.com/user-attachments/assets/f4d376ff-62c4-4fc5-a5a8-68d841fc11d3">
</p>

# Technology Stack

## 🔍 **Service Discovery**
- **Eureka**: Service registration and discovery.

## 🌐 **Gateway**
- **Spring Cloud Gateway**: Routing and API gateway management.

## 💾 **Storage**
- **PostgreSQL**: Advanced relational database.

## 🛠️ **Containerization**
- **Docker Compose**: Multi-container Docker applications.
- **Google Jib**: Builds Docker images directly from Java applications.
- **Kubernetes**: Container orchestration and management.

<p align="center">
    <img width="680" alt="kubernetes" src="https://github.com/user-attachments/assets/ed613c77-ea58-45e2-8903-3b4dbef0767b">
</p>

## 🚀 **CI/CD**
- **Jenkins**: Continuous integration and delivery automation.

<p align="center">
    <img width="680" alt="kubernetes" src="https://github.com/user-attachments/assets/100535d8-1281-4ca0-b498-55109b39d033">
</p>

## 🔑 **Authentication**
- **Token-based with JWT**: Secure authentication mechanism.

## 📜 **Logs**
- **Log4j2**: Flexible logging framework.

## 🧪 **Testing**
- **JUnit 5**: Modern unit and integration testing framework.

## 🌟 **Additional Technologies**

> This section highlights various tools and libraries used for enhancing different aspects of the backend system:

- **Kafka**: Facilitates real-time event streaming.
- **Java Mail Sender**: Handles email notifications.
- **Thymeleaf**: Used for crafting dynamic email templates.
- **Passay**: Ensures robust password validation.
- **Apache PDFBox**: Allows creation and manipulation of PDF documents.
- **Cloudinary**: Manages and transforms images.

<p align="center">
    <img width="680" alt="kubernetes" src="https://github.com/user-attachments/assets/3150fb17-0df3-409a-870b-736e5652b2e6">
</p>

- **WebClient**: Supports reactive HTTP requests.
- **Jacoco**: Measures and reports code coverage.
- **Zipkin**: Provides distributed tracing for application monitoring.
- **Grafana**: Offers visualization and dashboards for metrics.

<p align="center">
    <img width="680" alt="kubernetes" src="https://github.com/user-attachments/assets/ce6f202f-0dab-474a-bd9d-78c9a9eeb6a9">
</p>

- **Prometheus**: Collects and queries metrics for monitoring.
- **Actuator**: Manages application health and metrics.
- **Apache Poi**: Exports data into Excel files.
- **Resilience4j**: Implements fault tolerance and resilience patterns.

# Getting Started

> Before you begin, ensure that the following tools are installed on your machine:
>
>`DOCKER`
>`JAVA 17`

**To get started with the Cash Register Backend, follow these steps:**

1. Clone the repository: ```git clone https://github.com/emirhancebiroglu/cash_register_backend.git```

2. Navigate to the project directory: ```cd cash-register-backend```

3. Configure each service's 'application-docker.properties' file according to your environment and requirements.

4. Build and run the Docker containers using Docker Compose: ```docker-compose up -d```

> If all services are running correctly, each should display a "Healthy" or "Started" status. If you encounter any issues, please reach out to me, and I'll assist you in resolving them.

<p align="center">
    <img width="680" alt="terminal" src="https://github.com/user-attachments/assets/877ac6f7-6321-42eb-83a5-10bf06d6aec7">
</p>

# API Documentation

**API documentation for each service is available via Postman collection.**

To import the Postman collection:

1. Copy the provided collection link: ```https://api.postman.com/collections/30227502-9d39eaf3-a47b-4ff0-9a25-f6141d490ea9?access_key=PMAT-01J3NMGRC9RP3B13NPH76FT8XA```

<p align="center">
  <img width="680" alt="postman collection" src="https://github.com/user-attachments/assets/37a44925-d932-4257-9920-39d83ba4dd8e">
</p>
   
# License

This project is licensed under the MIT License - see the [LICENCE.txt](LICENCE.txt) file for details.

# Contact

For any questions or feedback, please contact me at: ```emirhancebiroglu21@hotmail.com```.
