# E-Commerce Microservice Architecture

A comprehensive e-commerce microservice system built with Spring Boot backend and React frontend, featuring product variants, inventory management, warehouse management, and event-driven architecture with Kafka.

## 🏗️ Architecture Overview

### Backend Services

| Service | Port | Description |
|---------|------|-------------|
| **Auth Service** | 8081 | User authentication, JWT tokens, roles & permissions |
| **Inventory Service** | 8082 | Product catalog, categories, inventory management, product variants |
| **Order Service** | 8083 | Order processing, cart management, order management |
| **User Service** | 8085 | Third-party sellers and suppliers management |
| **Warehouse Service** | 8086 | Warehouse and inventory location management |

### Frontend

- **React 18** with Vite
- **React Router** for navigation
- **Axios** for API calls
- Responsive design with modern UI/UX
- Multi-language support (Uzbek/English)
- Role-based access control (Customer, Seller, Admin, Super Admin)

### Infrastructure

- **PostgreSQL** - Multiple databases for each service (separate databases per service)
- **Redis** - Caching and session management for auth service
- **Apache Kafka** - Event streaming and inter-service communication
- **Docker & Docker Compose** - Containerization and orchestration

## ✨ Key Features

### Product Management
- **Product Catalog** - Comprehensive product management with images
- **Product Variants** - Support for multiple variants (Size, Color, Material, etc.)
  - Variant-specific pricing
  - Variant-specific stock management
  - Variant images
  - SKU management per variant
- **Categories** - Hierarchical category system
- **Inventory Tracking** - Real-time inventory levels with warehouse integration

### Inventory & Warehouse Management
- **Required Inventory Data** - Warehouse ID, stock quantity, reorder point, and unit cost are mandatory
- **Multi-Warehouse Support** - Products can be stored in multiple warehouses
- **Stock Management** - Automated stock updates and tracking
- **Warehouse Management** - Complete warehouse CRUD operations

### User Roles & Access Control
- **Customer** - Browse products, add to cart, place orders
- **Seller** - Manage products, variants, inventory, view orders
- **Admin** - Manage products, categories, users, orders
- **Super Admin** - Full system access, role & permission management

### Order Management
- **Shopping Cart** - Add/remove items, quantity management
- **Order Processing** - Order creation, status tracking
- **Variant Selection** - Customers can select specific product variants
- **Order History** - View past orders

### Event-Driven Architecture
- **Kafka Event Streaming** - Asynchronous communication between services
- **Event Publishing** - Services publish events for other services to consume
- **Event Consumption** - Services listen to relevant events and react accordingly

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **Node.js 18+** and npm
- **Docker & Docker Compose**
- **PostgreSQL 15** (or use Docker)

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone <repository-url>
cd e-comerce-microservice
```

### 2. Backend Setup

#### Build All Services
```bash
# Build all services
mvn clean package -DskipTests

# Or build individual services
mvn clean package -pl auth-service -DskipTests
mvn clean package -pl inventory-service -DskipTests
mvn clean package -pl order-service -DskipTests
mvn clean package -pl user-service -DskipTests
mvn clean package -pl warehouse-service -DskipTests
```

#### Start with Docker Compose
```bash
# Start all services and infrastructure
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### 4. Access Services

#### Backend Services
| Service | URL | Swagger UI |
|---------|-----|------------|
| Auth Service | http://localhost:8081 | http://localhost:8081/swagger-ui.html |
| Inventory Service | http://localhost:8082 | http://localhost:8082/swagger-ui.html |
| Order Service | http://localhost:8083 | http://localhost:8083/swagger-ui.html |
| User Service | http://localhost:8085 | http://localhost:8085/swagger-ui.html |
| Warehouse Service | http://localhost:8086 | http://localhost:8086/swagger-ui.html |

#### Frontend
- **Development**: http://localhost:5173 (Vite default port)
- **Production**: Build and serve from `frontend/dist`

## 🗄️ Database Configuration

Each service uses its own PostgreSQL database:

| Service | Database | Host Port |
|---------|----------|-----------|
| Auth Service | authdb | 5442 |
| Inventory Service | inventorydb | 5443 |
| Order Service | orderdb | 5444 |
| User Service | userdb | 5446 |
| Warehouse Service | warehousedb | 5447 |

**Default Credentials:**
- Username: `postgres`
- Password: `1234`

## 💻 Development

### Running Individual Services Locally

#### Auth Service
```bash
cd auth-service
mvn spring-boot:run
```

#### Inventory Service
```bash
cd inventory-service
mvn spring-boot:run
```

#### Order Service
```bash
cd order-service
mvn spring-boot:run
```

#### User Service
```bash
cd user-service
mvn spring-boot:run
```

#### Warehouse Service
```bash
cd warehouse-service
mvn spring-boot:run
```

### Frontend Development
```bash
cd frontend
npm run dev
```

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh-token` - Refresh JWT token

### Products & Inventory
- `GET /api/products` - List all products
- `POST /api/products` - Create product (requires inventory data)
- `GET /api/products/{id}` - Get product details
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `GET /api/products/{id}/variants` - Get product variants
- `POST /api/products/{id}/variants` - Create product variant
- `PUT /api/products/{productId}/variants/{variantId}` - Update variant
- `DELETE /api/products/{productId}/variants/{variantId}` - Delete variant

### Categories
- `GET /api/categories` - List all categories
- `POST /api/categories` - Create category
- `GET /api/categories/{id}` - Get category details

### Orders & Cart
- `GET /api/carts/user/{userId}` - Get user cart
- `POST /api/carts/items` - Add item to cart
- `PUT /api/carts/items/{id}/quantity` - Update cart item quantity
- `DELETE /api/carts/items/{id}` - Remove item from cart
- `POST /api/orders` - Create order
- `GET /api/orders/{id}` - Get order details

### Warehouses
- `GET /api/warehouses` - List all warehouses
- `POST /api/warehouses` - Create warehouse
- `GET /api/warehouses/{id}` - Get warehouse details

## 🎯 Product Variants Feature

### Creating Products with Variants

1. **Create Product** - First create the main product with required inventory data:
   - Warehouse ID (required)
   - Current Stock (required)
   - Reorder Point (required)
   - Unit Cost (required)

2. **Add Variants** - After product creation, add variants:
   - Variant Name (e.g., "Size", "Color")
   - Variant Value (e.g., "Large", "Red")
   - Optional: SKU, Price override, Stock quantity, Image

### Variant Selection in Frontend

- Customers can select variants on product detail page
- Selected variant affects price and stock display
- Variant images are shown in image carousel
- Selected variant is included when adding to cart

## 🔄 Event Flow

### Order Processing Flow
1. **Order Created** → Order Service publishes `ORDER_CREATED` event
2. **Inventory Check** → Inventory Service consumes event, checks stock
3. **Stock Reserved** → Inventory Service reserves stock
4. **Order Confirmed** → Order Service updates order status

### Inventory Updates
1. **Stock Change** → Inventory Service publishes `INVENTORY_UPDATED` event
2. **Warehouse Sync** → Warehouse Service consumes event if needed
3. **Order Notification** → Order Service notified of stock changes

## 🔐 Security

- **JWT Authentication** - Secure token-based authentication
- **Role-Based Access Control (RBAC)** - Granular permission system
- **OAuth2 Resource Server** - Secure API access
- **CORS Configuration** - Cross-origin request handling
- **Password Encryption** - BCrypt password hashing

## 📁 Project Structure

```
e-comerce-microservice/
├── auth-service/          # Authentication & Authorization
├── inventory-service/     # Products, Categories, Variants, Inventory
├── order-service/         # Orders, Cart Management
├── user-service/          # User Management, Sellers, Suppliers
├── warehouse-service/     # Warehouse Management
├── frontend/              # React Frontend Application
│   ├── src/
│   │   ├── components/   # Reusable components
│   │   ├── pages/        # Page components
│   │   ├── services/     # API services
│   │   └── context/      # React contexts
├── docker-compose.yml     # Docker orchestration
└── README.md             # This file
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run tests for specific service
cd auth-service
mvn test

# Run frontend tests (if configured)
cd frontend
npm test
```

## 📝 Notes

- **Inventory Data is Required**: When creating products, warehouse ID, current stock, reorder point, and unit cost must be provided
- **Product Variants are Optional**: Variants can be added after product creation, but are not mandatory
- **Payment Service Removed**: Payment processing has been removed from the current version
- **Click Service**: Click service exists but is not included in docker-compose

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- **Rakhmonov** - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the amazing frontend library
- Apache Kafka for event streaming capabilities
- All contributors and open-source libraries used in this project
