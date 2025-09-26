# Pet Care Appointment System

A full-stack web application for pet owners and veterinarians to manage appointments and care services.

## Architecture

- **Frontend**: React + Vite application served with Nginx
- **Backend**: Spring Boot REST API  
- **Database**: MySQL 8.0

## Quick Start with Docker

### Prerequisites
- Docker
- Docker Compose (or Docker Desktop)

### Run the Application

1. Clone the repository:
```bash
git clone <repository-url>
cd bookAppointmentSystem
```

2. Build the backend JAR file first:
```bash
cd backend
./mvnw clean package
cd ..
```

3. Build the frontend distribution:
```bash
cd frontend
npm install
npm run build
cd ..
```

4. Start all services:
```bash
docker compose up -d
```

5. Access the application:
- Frontend: http://localhost:3000
- Backend API: http://localhost:9192
- Database: localhost:3306

### Stop the Application

```bash
docker compose down
```

To remove all data:
```bash
docker compose down -v
```

## Development

### Frontend Development
```bash
cd frontend
npm install
npm run dev
```
Development server runs on http://localhost:5174

### Backend Development
```bash
cd backend
./mvnw spring-boot:run
```
API server runs on http://localhost:9192

### Development Database Only
If you only need a MySQL database for development:
```bash
docker compose -f docker-compose.dev.yml up -d
```
Database will be available on port 3307 to avoid conflicts.

## Services

### Frontend (React)
- Port: 3000 (Docker) / 5174 (Development)
- Built with Vite and served with Nginx in production
- Supports environment-based API configuration

### Backend (Spring Boot)
- Port: 9192
- RESTful API with JWT authentication
- Profiles: `default` (development), `docker` (containerized)
- Health check endpoint: http://localhost:9192/actuator/health

### Database (MySQL)
- Port: 3306 (production) / 3307 (development)
- Database: `pet-care-system`
- Credentials configured in docker-compose.yml

## Environment Variables

The application supports environment-based configuration:

### Backend
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `FRONTEND_BASE_URL`: Frontend base URL for CORS
- `SPRING_PROFILES_ACTIVE`: Active Spring profile

### Frontend
- `REACT_APP_API_BASE_URL`: Backend API base URL

## Docker Images

The application uses optimized Docker builds:

- **Frontend**: Pre-built dist files → Nginx alpine (lightweight)
- **Backend**: Pre-built JAR file → Eclipse Temurin JRE 17 (secure)
- **Database**: MySQL 8.0 official image

## Troubleshooting

### Build Issues
- Ensure backend JAR is built: `cd backend && ./mvnw clean package`
- Ensure frontend dist is built: `cd frontend && npm run build`
- Check Docker daemon is running: `docker version`

### Network Issues
- Verify ports are not in use: `netstat -an | grep :3000`
- Check container logs: `docker compose logs <service-name>`

### Database Issues
- Wait for MySQL to be ready (health check may take 30-60 seconds)
- Reset database: `docker compose down -v && docker compose up -d`