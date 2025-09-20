#!/bin/bash

echo "🚀 Building and running E-commerce Microservices with Docker..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Build all services
echo "📦 Building all services..."
docker-compose build

# Run all services
echo "🏃‍♂️ Starting all services..."
docker-compose up -d

# Wait for services to start
echo "⏳ Waiting for services to start..."
sleep 30

# Check service status
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "✅ Services are running!"
echo "📱 Auth Service: http://localhost:8081"
echo "📦 Inventory Service: http://localhost:8082"
echo "🛒 Order Service: http://localhost:8083"
echo "🗄️  PostgreSQL: localhost:5432"
echo ""
echo "To view logs: docker-compose logs -f"
echo "To stop services: docker-compose down"

