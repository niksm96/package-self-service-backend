# Package Self-Service Backend

## Overview

The Package Self-Service Backend is a Spring Boot application that allows users to manage package shipping services. Users can submit packages, view package statuses, and list available receivers. The application is designed to be extensible and maintainable, using best practices in software development.

## Features

- List available receivers
- Submit packages for shipping
- Retrieve package details and statuses
- Easy integration with a shipping service

## Technologies Used

- Spring Boot
- Spring Web
- OpenAPI for documentation
- JUnit 5 for testing

## Getting Started

### Prerequisites

- JDK 21
- Maven
- IDE (e.g., IntelliJ IDEA, Eclipse)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/niksm96/package-self-service-backend.git
   cd package-self-service-backend
   
2. Run the main class PackageSelfServiceBackendApplication.java in order to run the Spring Boot Application
3. Test the application using the url http://localhost:8080

### Major Improvements for Production among many others. 
1. Proper database implementation. 
2. Spring Security Implementation. 
3. Thorough integration testing. 
