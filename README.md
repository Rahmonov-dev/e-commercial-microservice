# E-commerce Microservices

A microservices-based e-commerce application built with Spring Boot and Docker.

## Project Structure

```
e-comerce-microservice/
├── auth-service/          # Authentication and authorization service
├── inventory-service/     # Product inventory management
├── order-service/         # Order processing and management
├── docker-compose.yml     # Docker orchestration
└── build-and-run.sh      # Build and run script
```

## Services

- **Auth Service** (Port 8081): Handles user authentication and authorization
- **Inventory Service** (Port 8082): Manages product inventory
- **Order Service** (Port 8083): Processes orders
- **PostgreSQL** (Port 5434): Database for all services

## Prerequisites

- Docker and Docker Compose
- Java 21
- Maven

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd e-comerce-microservice
   ```

2. **Build and run with Docker**
   ```bash
   # Make the script executable
   chmod +x build-and-run.sh
   
   # Run the script
   ./build-and-run.sh
   ```

   Or manually:
   ```bash
   # Build all services
   docker compose build
   
   # Start all services
   docker compose up -d
   ```

3. **Check service status**
   ```bash
   docker compose ps
   ```

4. **View logs**
   ```bash
   # All services
   docker compose logs -f
   
   # Specific service
   docker compose logs auth-service -f
   ```

## Service URLs

- Auth Service: http://localhost:8081
- Inventory Service: http://localhost:8082
- Order Service: http://localhost:8083
- PostgreSQL: localhost:5434

## Database Configuration

The services use PostgreSQL with the following databases:
- `authdb` - for authentication service
- `inventorydb` - for inventory service  
- `orderdb` - for order service

## Troubleshooting

### Port Conflicts
If you get port conflicts, you can modify the ports in `docker-compose.yml`.

### Service Not Starting
1. Check logs: `docker compose logs <service-name>`
2. Ensure PostgreSQL is running: `docker compose ps`
3. Rebuild if needed: `docker compose build <service-name>`

### Database Issues
If services can't connect to the database:
1. Check if PostgreSQL is running: `docker compose ps`
2. Create missing databases:
   ```bash
   docker exec -it ecommerce-postgres psql -U postgres -c "CREATE DATABASE inventorydb;"
   docker exec -it ecommerce-postgres psql -U postgres -c "CREATE DATABASE orderdb;"
   ```

## Development

### Building Individual Services
```bash
# Build auth service
cd auth-service
./mvnw clean package -DskipTests
cd ..
docker compose build auth-service
```

### Stopping Services
```bash
# Stop all services
docker compose down

# Stop specific service
docker compose stop auth-service
```

## Current Status

✅ **Auth Service**: Running on port 8081  
⚠️ **Inventory Service**: Needs investigation  
⚠️ **Order Service**: Needs investigation  
✅ **PostgreSQL**: Running on port 5434

## Next Steps

1. Fix inventory and order services startup issues
2. Add health check endpoints
3. Implement service-to-service communication
4. Add API documentation
5. Set up monitoring and logging

