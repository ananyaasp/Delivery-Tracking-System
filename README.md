# Delivery Tracking System

A Spring Boot application for managing delivery shipments with MySQL database integration. Features include shipment creation, status updates, agent assignment, and real-time tracking with agent phone numbers visible in logs.

## Features

- ✅ Create shipments with source and destination addresses
- ✅ Automatic agent assignment when shipment status becomes "PickedUp"
- ✅ Real-time shipment tracking with status history
- ✅ Agent phone numbers visible in tracking logs
- ✅ Separate UI pages for different operations
- ✅ MySQL database persistence
- ✅ RESTful API endpoints

## Prerequisites

- **Java 17 or higher** (tested with Java 25)
- **MySQL 8.0 or higher** (tested with MySQL 8.0.43)
- **Maven 3.6 or higher** (tested with Maven 3.14.1)
- **Web browser** for accessing the UI

## MySQL Setup

### 1. Install MySQL

**Windows:**
- Download MySQL from [mysql.com](https://dev.mysql.com/downloads/mysql/)
- Run the installer and follow the setup wizard
- Choose "Developer Default" or "Server only" configuration
- Set a root password during installation

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

**macOS:**
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

### 2. Create Database and User

Start MySQL command line client:

```bash
mysql -u root -p
```

Run these SQL commands:

```sql
-- Create database
CREATE DATABASE delivery_db;

-- Create user for the application
CREATE USER 'springuser'@'localhost' IDENTIFIED BY 'springpass';

-- Grant permissions
GRANT ALL PRIVILEGES ON delivery_db.* TO 'springuser'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;

-- Exit MySQL
EXIT;
```

### 3. Verify MySQL Connection

Test the connection:

```bash
mysql -u springuser -p -e "SHOW DATABASES;"
```

Enter password: `springpass`

You should see `delivery_db` in the list.

## Project Setup

### 1. Clone or Download the Project

Navigate to your desired directory and ensure the project files are there.

### 2. Configure Database Connection

The database connection is already configured in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/delivery_db?createDatabaseIfNotExist=true
spring.datasource.username=springuser
spring.datasource.password=springpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server Configuration
server.port=8081

# SQL Initialization
spring.sql.init.mode=always
```

### 3. Build the Project

```bash
mvnw clean compile
```

## Running the Application

### Method 1: Using Maven Wrapper (Recommended)

```bash
.\mvnw spring-boot:run
```

### Method 2: Using Maven (if installed globally)

```bash
mvn spring-boot:run
```

### Method 3: Build and Run JAR

```bash
.\mvnw clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Accessing the Application

Once the application starts successfully, you'll see:
```
Started DemoApplication in X.XXX seconds
Tomcat started on port 8081 (http) with context path '/'
```

### Web Interface

Open your browser and go to: **http://localhost:8081**

#### Available Pages:
- **Home Page**: `http://localhost:8081/` or `http://localhost:8081/index.html`
- **Create Shipment**: `http://localhost:8081/create.html`
- **Update Status**: `http://localhost:8081/update.html`
- **Track Shipment**: `http://localhost:8081/track.html`

### API Endpoints

#### Create Shipment
```bash
POST http://localhost:8081/shipments
Content-Type: application/json

{
  "sourceAddress": "Mumbai",
  "destinationAddress": "Delhi"
}
```

#### Update Shipment Status
```bash
PUT http://localhost:8081/shipments/{id}?status={status}
```

Available statuses: `PickedUp`, `InTransit`, `OutForDelivery`, `Delivered`

#### Track Shipment
```bash
GET http://localhost:8081/tracking/{id}
```

#### Get Available Agents
```bash
GET http://localhost:8081/agents/available
```

## Sample Usage

### 1. Create a Shipment
- Go to `http://localhost:8081/create.html`
- Enter source and destination addresses
- Click "Create Shipment"

### 2. Update Shipment Status
- Go to `http://localhost:8081/update.html`
- Enter shipment ID and select status
- Click "Update Status"
- When status becomes "PickedUp", an agent is automatically assigned

### 3. Track Shipment
- Go to `http://localhost:8081/track.html`
- Enter shipment ID
- View current status, assigned agent (with phone number), and status history

## Database Schema

The application automatically creates these tables:

- **delivery_agent**: Stores agent information (id, name, phone, available)
- **shipment**: Stores shipment details (id, source, destination, status, agent_id)
- **status_log**: Stores status change history (id, status, timestamp, shipment_id)

## Troubleshooting

### Port 8081 Already in Use
If you see "Port 8081 was already in use":
```bash
# Find process using port 8081
netstat -ano | findstr :8081

# Kill the process (replace XXXX with actual PID)
taskkill /PID XXXX /F
```

### MySQL Connection Issues
- Ensure MySQL service is running
- Verify username/password in `application.properties`
- Check if database `delivery_db` exists

### Application Won't Start
- Ensure Java 17+ is installed: `java -version`
- Ensure Maven wrapper exists: `.\mvnw --version`
- Check MySQL connection: `mysql -u springuser -p delivery_db`

## Project Structure

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── controller/          # REST controllers
│   │   ├── model/              # JPA entities
│   │   ├── repository/         # JPA repositories
│   │   └── service/            # Business logic
│   └── resources/
│       ├── static/             # HTML/CSS/JS files
│       └── application.properties
└── test/                       # Unit tests
```

## Technologies Used

- **Spring Boot 4.0.4**: Framework
- **Spring Data JPA**: ORM
- **MySQL 8.0.43**: Database
- **HikariCP**: Connection pooling
- **Maven**: Build tool
- **Java 25**: Runtime
- **HTML/CSS/JavaScript**: Frontend

## License

This project is for educational purposes.