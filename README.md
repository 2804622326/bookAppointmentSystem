# Pet Care Appointment System

A full-stack web application for pet owners and veterinarians to manage appointments and care services.

## Architecture

- **Frontend**: React + Vite application served with Nginx
- **Backend**: Spring Boot REST API  
- **Database**: MySQL 8.0

## Quick Start with Docker

### Prerequisites
- Docker
- Docker Compose

### Run the Application

1. Clone the repository:
```bash
git clone <repository-url>
cd bookAppointmentSystem
```

2. Start all services:
```bash
docker-compose up -d
```

3. Access the application:
- Frontend: http://localhost:3000
- Backend API: http://localhost:9192
- Database: localhost:3306

### Stop the Application

```bash
docker-compose down
```

To remove all data:
```bash
docker-compose down -v
```

## Development

### Frontend Development
```bash
cd frontend
npm install
npm run dev
```

### Backend Development
```bash
cd backend
./mvnw spring-boot:run
```

## Services

### Frontend (React)
- Port: 3000 (Docker) / 5174 (Development)
- Built with Vite and served with Nginx in production

### Backend (Spring Boot)
- Port: 9192
- RESTful API with JWT authentication
- Profiles: `default` (development), `docker` (containerized)

### Database (MySQL)
- Port: 3306
- Database: `pet-care-system`
- Credentials configured in docker-compose.yml

## Environment Variables

The application supports environment-based configuration:

### Backend
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `FRONTEND_BASE_URL`: Frontend base URL for CORS

### Frontend
- `REACT_APP_API_BASE_URL`: Backend API base URL

## Docker Images

The application uses multi-stage builds for optimized production images:

- Frontend: Node.js build → Nginx alpine
- Backend: Maven build → OpenJDK 17 slim
- Database: MySQL 8.0 official image