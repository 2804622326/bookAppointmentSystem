# Docker Deployment Guide

## 🚀 Quick Start

### Prerequisites
- Docker installed on your machine
- Docker Compose installed

### 1. Build and Start All Services
```bash
# Clone the repository
git clone https://github.com/2804622326/bookAppointmentSystem.git
cd bookAppointmentSystem

# Start all services (MySQL, Backend, Frontend)
docker-compose up --build
```

### 2. Access the Application
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:9192/api/v1
- **MySQL**: localhost:3307

### 3. Stop All Services
```bash
docker-compose down
```

### 4. Clean Up (Remove volumes)
```bash
docker-compose down -v
```

## 📋 Services Overview

| Service  | Port | Description |
|----------|------|-------------|
| Frontend | 3000 | React.js application served by Nginx |
| Backend  | 9192 | Spring Boot REST API |
| MySQL    | 3307 | Database server |

## 🔧 Configuration

### Environment Variables

#### Backend Service
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username  
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `AUTH_TOKEN_JWT_SECRET`: JWT signing secret
- `FRONTEND_BASE_URL`: Frontend application URL

#### Frontend Service  
- `REACT_APP_API_BASE_URL`: Backend API base URL

### Database
- **Database Name**: `pet-care-system`
- **Username**: `root`
- **Password**: `Liminghao2001`

## 🐛 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using the port
   lsof -i :3000  # or :9192, :3307
   
   # Kill the process or change ports in docker-compose.yml
   ```

2. **Database Connection Issues**
   ```bash
   # Check if MySQL is healthy
   docker-compose logs mysql
   
   # Restart services
   docker-compose restart
   ```

3. **Backend Won't Start**
   ```bash
   # Check backend logs
   docker-compose logs backend
   
   # Often due to waiting for database initialization
   ```

### Useful Commands

```bash
# View logs for specific service
docker-compose logs [service-name]

# Follow logs in real-time
docker-compose logs -f [service-name]

# Restart specific service
docker-compose restart [service-name]

# Rebuild specific service
docker-compose up --build [service-name]

# Execute command in running container
docker-compose exec [service-name] /bin/bash
```

## 🔍 Health Checks

All services include health checks:
- **MySQL**: `mysqladmin ping`
- **Backend**: API endpoint `/api/v1/pets/get-types`
- **Frontend**: Nginx status

Check service health:
```bash
docker-compose ps
```

## 📦 Production Deployment

For production deployment:

1. **Update environment variables** in docker-compose.yml
2. **Use external database** instead of containerized MySQL
3. **Add SSL certificates** for HTTPS
4. **Configure reverse proxy** (nginx/traefik)
5. **Set up monitoring** and logging

## 🔐 Security Notes

- Change default passwords before production use
- Use environment files (.env) for sensitive data
- Update JWT secret key
- Configure proper database permissions