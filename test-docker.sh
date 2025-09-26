#!/bin/bash

# Test script for Docker configuration
echo "=== Testing Docker Configuration ==="

# Check if Docker and Docker Compose are available
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed"
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "ERROR: Docker Compose is not available"
    exit 1
fi

echo "✓ Docker and Docker Compose are available"

# Ensure backend JAR is built
echo ""
echo "=== Building Backend JAR ==="
cd backend
if ./mvnw clean package -DskipTests=true; then
    echo "✓ Backend JAR build successful"
else
    echo "❌ Backend JAR build failed"
    exit 1
fi
cd ..

# Ensure frontend dist is built
echo ""
echo "=== Building Frontend Distribution ==="
cd frontend
if [ ! -d "node_modules" ]; then
    echo "Installing npm dependencies..."
    npm install
fi

if npm run build; then
    echo "✓ Frontend build successful"
else
    echo "❌ Frontend build failed"
    exit 1
fi
cd ..

# Test building individual services
echo ""
echo "=== Testing Backend Docker Build ==="
cd backend
if docker build -t pet-care-backend .; then
    echo "✓ Backend Docker build successful"
else
    echo "❌ Backend Docker build failed"
    exit 1
fi
cd ..

echo ""
echo "=== Testing Frontend Docker Build ==="
cd frontend
if docker build -t pet-care-frontend .; then
    echo "✓ Frontend Docker build successful"
else
    echo "❌ Frontend Docker build failed"
    exit 1
fi
cd ..

echo ""
echo "=== Testing Docker Compose Configuration ==="
if docker compose config > /dev/null; then
    echo "✓ Docker Compose configuration is valid"
else
    echo "❌ Docker Compose configuration has errors"
    docker compose config
    exit 1
fi

echo ""
echo "=== All Docker tests passed! ==="
echo ""
echo "Usage Instructions:"
echo "1. Start the application:"
echo "   docker compose up -d"
echo ""
echo "2. Check service status:"
echo "   docker compose ps"
echo ""
echo "3. View logs:"
echo "   docker compose logs -f [service-name]"
echo ""
echo "4. Stop the application:"
echo "   docker compose down"
echo ""
echo "5. Remove all data:"
echo "   docker compose down -v"
echo ""
echo "Access URLs:"
echo "- Frontend: http://localhost:3000"
echo "- Backend: http://localhost:9192"
echo "- Database: localhost:3306"