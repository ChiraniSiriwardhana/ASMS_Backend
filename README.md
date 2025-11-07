# 🚗 Automobile Management System (ASMS) - Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-ff69b4.svg)](https://jwt.io/)

A comprehensive REST API backend for managing an automobile service system with role-based authentication using JWT tokens.

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [User Roles & Flow](#-user-roles--flow)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Security](#-security)
- [License](#-license)

## ✨ Features

- **JWT Authentication** - Secure token-based authentication
- **Role-Based Access Control** - Three user roles (Admin, Customer, Employee)
- **🔔 Real-Time Notifications** - WebSocket-based live notifications (NEW!)
- **Email Notifications** - Automated email for employee activation
- **Password Management** - Reset, change, and secure password storage with BCrypt
- **RESTful API** - Clean and well-documented endpoints
- **Exception Handling** - Global error handling with meaningful responses
- **Input Validation** - Bean validation on all inputs
- **PostgreSQL Database** - Reliable data persistence

### 🆕 Live Notification System
- **WebSocket Support** - Real-time notifications without page refresh
- **User-Specific Notifications** - Personalized notification delivery
- **Appointment Updates** - Instant alerts for appointment status changes
- **Admin Broadcasts** - System-wide notifications for administrators
- **Notification History** - REST API for fetching historical notifications
- **Read/Unread Tracking** - Mark notifications as read, get unread count

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Programming Language |
| Spring Boot | 3.5.6 | Application Framework |
| Spring Security | 3.x | Security & Authentication |
| Spring Data JPA | 3.x | Data Access Layer |
| **Spring WebSocket** | **3.x** | **Real-Time Notifications (NEW!)** |
| PostgreSQL | Latest | Database |
| JWT (jjwt) | 0.11.5 | Token Generation |
| Lombok | Latest | Boilerplate Reduction |
| Maven | 3.6+ | Build Tool |
| JavaMailSender | Latest | Email Service |

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **JDK 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **PostgreSQL** - [Download](https://www.postgresql.org/download/)
- **Maven** (Optional - wrapper included) - [Download](https://maven.apache.org/download.cgi)
- **Git** - [Download](https://git-scm.com/downloads)

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/ASMS_Backend.git
cd ASMS_Backend
```

### 2. Create PostgreSQL Database

```sql
-- Open PostgreSQL terminal
psql -U postgres

-- Create database
CREATE DATABASE asms_db;

-- Verify
\l

-- Exit
\q
```

### 3. Configure Application

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/asms_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

# Email Configuration (Gmail Example)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-gmail-app-password
```

> **Note:** For Gmail, you need to generate an [App Password](https://support.google.com/accounts/answer/185833):
> 1. Enable 2-Step Verification
> 2. Go to Security → App passwords
> 3. Generate password for "Mail"
> 4. Use generated password in configuration

### 4. Build the Project

```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# Or using installed Maven
mvn clean install
```

## ⚙️ Configuration

### Application Properties

```properties
# Server Configuration
server.port=8080

# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400000  # 24 hours in milliseconds

# Admin Credentials (Hardcoded)
admin.username=admin
admin.password=admin123
admin.email=admin@asms.com

# Application URL
app.url=http://localhost:8080
```

### Environment Variables (Optional)

For production, use environment variables:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export MAIL_USERNAME=youremail@gmail.com
export MAIL_PASSWORD=yourapppassword
```

## 🏃 Running the Application

### Development Mode

```bash
./mvnw spring-boot:run
```

### Production Mode

```bash
# Build JAR
./mvnw clean package

# Run JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Verify Application is Running

```bash
curl http://localhost:8080/api/auth/login
```

Expected: JSON response (not 404)

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication Endpoints (Public)

#### 1. Login (All Roles)
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "email": "admin@asms.com",
  "role": "ADMIN",
  "message": "Login successful"
}
```

#### 2. Customer Signup
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "1234567890",
  "address": "123 Main St"
}
```

#### 3. Verify Token (GET - From Email Link)
```http
GET /api/auth/verify-token?token={uuid-from-email}
```

**Response:**
```json
{
  "success": true,
  "message": "Token is valid. Please set your password.",
  "data": {
    "username": "employee1",
    "email": "employee1@example.com",
    "firstName": "Jane",
    "lastName": "Smith"
  }
}
```

#### 4. Set Password (POST)
```http
POST /api/auth/set-password
Content-Type: application/json

{
  "token": "uuid-from-email",
  "newPassword": "newPassword123"
}
```

#### 5. Change Password (Authenticated)
```http
POST /api/auth/change-password
Authorization: Bearer {jwt-token}
Content-Type: application/json

{
  "oldPassword": "currentPassword",
  "newPassword": "newPassword123"
}
```

### Admin Endpoints

> **Note:** All admin endpoints require `Authorization: Bearer {admin-token}` header

#### 1. Add Employee
```http
POST /api/admin/employees
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "username": "employee1",
  "email": "employee1@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "phoneNumber": "9876543210"
}
```

#### 2. Get All Employees
```http
GET /api/admin/employees
Authorization: Bearer {admin-token}
```

#### 3. Get All Customers
```http
GET /api/admin/customers
Authorization: Bearer {admin-token}
```

#### 4. Get User by ID
```http
GET /api/admin/users/{id}
Authorization: Bearer {admin-token}
```

#### 5. Activate/Deactivate User
```http
PUT /api/admin/users/{id}/activate
PUT /api/admin/users/{id}/deactivate
Authorization: Bearer {admin-token}
```

#### 6. Resend Employee Activation Email
```http
POST /api/admin/employees/{id}/resend-activation
Authorization: Bearer {admin-token}
```

### Employee Endpoints

> **Note:** Requires `Authorization: Bearer {employee-token}` header

```http
GET /api/employee/profile
GET /api/employee/dashboard
Authorization: Bearer {employee-token}
```

### Customer Endpoints

> **Note:** Requires `Authorization: Bearer {customer-token}` header

```http
GET /api/customer/profile
GET /api/customer/dashboard
Authorization: Bearer {customer-token}
```

### 🔔 Notification Endpoints (NEW!)

> **Note:** All notification endpoints require `Authorization: Bearer {token}` header

#### 1. Get All Notifications
```http
GET /api/notifications
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Notifications retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Appointment Created",
      "message": "Your appointment for Toyota Camry has been created",
      "type": "APPOINTMENT_CREATED",
      "recipientId": 5,
      "appointmentId": 10,
      "isRead": false,
      "createdAt": "2025-11-07T14:30:00",
      "readAt": null
    }
  ]
}
```

#### 2. Get Unread Notifications
```http
GET /api/notifications/unread
Authorization: Bearer {token}
```

#### 3. Get Unread Count
```http
GET /api/notifications/unread/count
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "message": "Unread count retrieved successfully",
  "data": 5
}
```

#### 4. Mark Notification as Read
```http
PUT /api/notifications/{notificationId}/read
Authorization: Bearer {token}
```

#### 5. Mark All Notifications as Read
```http
PUT /api/notifications/read-all
Authorization: Bearer {token}
```

#### 6. Delete Notification
```http
DELETE /api/notifications/{notificationId}
Authorization: Bearer {token}
```

### 📡 WebSocket Connection (Real-Time Notifications)

#### Connect to WebSocket
```javascript
// WebSocket URL
ws://localhost:8080/ws/notifications?token={jwt-token}

// Subscribe to user-specific topic
/topic/notifications/user.{userId}

// Admins also subscribe to
/topic/notifications/admin
```

#### Frontend Integration
See detailed integration guides:
- **📘 Complete Guide:** [WEBSOCKET_NOTIFICATION_GUIDE.md](WEBSOCKET_NOTIFICATION_GUIDE.md)
- **⚡ Quick Start:** [QUICK_START_NOTIFICATIONS.md](QUICK_START_NOTIFICATIONS.md)
- **🧪 Test Tool:** Open `websocket-test.html` in browser

#### Notification Types
- `APPOINTMENT_CREATED` - Customer creates appointment
- `APPOINTMENT_CONFIRMED` - Admin confirms appointment
- `APPOINTMENT_CANCELLED` - Appointment cancelled
- `EMPLOYEE_ASSIGNED` - Employee assigned to appointment
- `STATUS_CHANGED_IN_SERVICE` - Status changed to IN_SERVICE
- `STATUS_CHANGED_READY` - Status changed to READY
- `STATUS_CHANGED_COMPLETED` - Status changed to COMPLETED
- `GENERAL` - General notification

#### WebSocket Message Format
```json
{
  "notificationId": 123,
  "title": "Appointment Confirmed",
  "message": "Your appointment for Toyota Camry has been confirmed",
  "type": "APPOINTMENT_CONFIRMED",
  "appointmentId": 45,
  "recipientId": 5,
  "timestamp": "2025-11-07T14:30:00"
}
```

#### Quick Integration Example
```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const token = localStorage.getItem('authToken');
const userId = localStorage.getItem('userId');

const client = new Client({
  webSocketFactory: () => 
    new SockJS(`http://localhost:8080/ws/notifications?token=${token}`),
  
  onConnect: () => {
    client.subscribe(`/topic/notifications/user.${userId}`, (message) => {
      const notification = JSON.parse(message.body);
      console.log('📬 New notification:', notification);
      // Update your UI here
    });
  }
});

client.activate();
```

## 👥 User Roles & Flow

### 🔐 Admin
- **Access:** Hardcoded credentials (`admin` / `admin123`)
- **Capabilities:**
  - Login to system
  - Create employee accounts
  - Manage users (activate/deactivate)
  - View all customers and employees
  - Resend activation emails

**Flow:**
```
Login → Add Employee → System sends activation email → Manage users
```

### 👤 Customer
- **Access:** Public registration via `/api/auth/signup`
- **Capabilities:**
  - Self-registration
  - Login to system
  - View profile
  - Access customer dashboard
  - Change password

**Flow:**
```
Signup → Login → Get JWT Token → Access Customer Features
```

### 👨‍💻 Employee
- **Access:** Created by Admin only
- **Capabilities:**
  - Receive activation email
  - Set password via token
  - Login to system
  - View profile
  - Access employee dashboard
  - Change password

**Flow:**
```
Admin creates account → Receive email → Click link/Use token → Set password → Login → Access Employee Features
```

## 🧪 Testing

### Using cURL

```bash
# 1. Admin Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Customer Signup
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "email":"test@example.com",
    "password":"password123",
    "firstName":"Test",
    "lastName":"User",
    "phoneNumber":"1234567890",
    "address":"Test Address"
  }'

# 3. Add Employee (use admin token)
curl -X POST http://localhost:8080/api/admin/employees \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -d '{
    "username":"employee1",
    "email":"employee1@example.com",
    "firstName":"Jane",
    "lastName":"Smith",
    "phoneNumber":"9876543210"
  }'
```

### Using Postman

1. Import the provided Postman collection: `ASMS_API_Collection.postman_collection.json`
2. Set variables after login:
   - `admin_token`
   - `customer_token`
   - `employee_token`
3. Test all endpoints

## 📁 Project Structure

```
ASMS_Backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/              # Security & app configuration
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/          # REST API endpoints
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CustomerController.java
│   │   │   │   └── EmployeeController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   └── ...
│   │   │   ├── exception/           # Exception handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── BadRequestException.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/               # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   └── Role.java
│   │   │   ├── repository/          # Database access
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/            # JWT & Authentication
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── AdminService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   └── UserService.java
│   │   │   └── AsmsBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── .gitignore
├── README.md
└── ASMS_API_Collection.postman_collection.json
```

## 🔒 Security

### Features

- **JWT Tokens:** Stateless authentication with 24-hour expiration
- **BCrypt Hashing:** Password encryption with salt
- **Role-Based Access:** Endpoint protection by user role
- **Token Validation:** Automatic token verification on each request
- **CSRF Protection:** Disabled for stateless API (using JWT)
- **Input Validation:** Bean validation on all request DTOs

### Best Practices

1. **Change Default Admin Password** in production
2. **Use Environment Variables** for sensitive configuration
3. **Generate Strong JWT Secret** (at least 256 bits)
4. **Enable HTTPS** in production
5. **Implement Rate Limiting** for authentication endpoints
6. **Regular Security Audits** of dependencies

## 🔑 Default Credentials

```
Username: admin
Password: admin123
Email: admin@asms.com
```

> ⚠️ **Important:** Change these credentials before deploying to production!

## 🐛 Troubleshooting

### Issue: Database Connection Failed
```bash
# Check if PostgreSQL is running
sudo service postgresql status

# Verify database exists
psql -U postgres -l | grep asms_db
```

### Issue: Port 8080 Already in Use
Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: Email Not Sending
- Verify Gmail App Password (not regular password)
- Check spam folder
- Ensure 2-Step Verification is enabled
- Check application logs for errors

### Issue: JWT Token Expired
- Token validity: 24 hours
- Login again to get new token
- Or increase `jwt.expiration` value

### Issue: WebSocket Connection Failed
```bash
# Verify backend is running
curl http://localhost:8080/api/notifications

# Check if token is valid (not expired)
# Token must include userId claim

# Ensure correct WebSocket URL format:
ws://localhost:8080/ws/notifications?token=YOUR_JWT_TOKEN
```

### Issue: Not Receiving Real-Time Notifications
- ✅ Check if subscribed to correct topic: `/topic/notifications/user.{YOUR_USER_ID}`
- ✅ Verify userId in JWT token matches your user ID
- ✅ Ensure WebSocket connection is active (check connection status)
- ✅ Test using `websocket-test.html` tool
- ✅ Check browser console for errors

### Issue: 500 Error on Notification Endpoints
- Verify user is authenticated (valid JWT token)
- Check database connection
- Review application logs for stack traces

## 📝 API Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### HTTP Status Codes
- `200 OK` - Successful request
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Invalid/missing token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

## 🙏 Acknowledgments

- Spring Boot Team for the amazing framework
- JWT.io for authentication standard
- PostgreSQL community
- All contributors

---

⭐ **Star this repository if you find it helpful!**

📧 **Questions?** Open an issue or contact the maintainer.

🚀 **Happy Coding!**

