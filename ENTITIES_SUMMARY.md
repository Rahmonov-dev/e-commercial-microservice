# E-commerce Microservices - Simplified Entity Models (Learning Project)

## 📋 **Overview**
This document summarizes the **simplified** entity models created for the e-commerce microservices learning project.

---

## 🔐 **Auth Service Entities**

### 1. **User** (`auth-service/src/main/java/org/rakhmonov/authservice/entity/User.java`)
**Purpose**: Core user entity with basic authentication

**Key Features**:
- ✅ Spring Security `UserDetails` implementation
- ✅ Basic user profile (name, email, phone)
- ✅ User roles (ADMIN, CUSTOMER, SELLER)
- ✅ User status (ACTIVE, INACTIVE)

**Fields**: 10 essential fields

### 2. **RefreshToken** (`auth-service/src/main/java/org/rakhmonov/authservice/entity/RefreshToken.java`)
**Purpose**: JWT refresh token management

**Key Features**:
- ✅ Token storage and expiration
- ✅ User association
- ✅ Token revocation support

---

## 📦 **Inventory Service Entities**

### 1. **Product** (`inventory-service/src/main/java/org/rakhmonov/inventoryservice/entity/Product.java`)
**Purpose**: Core product entity with essential features

**Key Features**:
- ✅ Product information (name, description, price)
- ✅ Basic inventory management (stock quantity)
- ✅ Product status (ACTIVE, INACTIVE, OUT_OF_STOCK)
- ✅ Category and supplier relationships
- ✅ Single image URL

**Fields**: 10 essential fields

### 2. **Category** (`inventory-service/src/main/java/org/rakhmonov/inventoryservice/entity/Category.java`)
**Purpose**: Simple product categorization

**Key Features**:
- ✅ Category name and description
- ✅ Active/inactive status

### 3. **Supplier** (`inventory-service/src/main/java/org/rakhmonov/inventoryservice/entity/Supplier.java`)
**Purpose**: Basic supplier/vendor management

**Key Features**:
- ✅ Supplier name and description
- ✅ Contact information (email, phone)
- ✅ Active/inactive status

---

## 🛒 **Order Service Entities**

### 1. **Order** (`order-service/src/main/java/org/rakhmonov/orderservice/entity/Order.java`)
**Purpose**: Basic order management

**Key Features**:
- ✅ Order numbering and tracking
- ✅ Total amount calculation
- ✅ Simple shipping address
- ✅ Payment method and status
- ✅ Order status lifecycle (PENDING → CONFIRMED → SHIPPED → DELIVERED → CANCELLED)

**Fields**: 10 essential fields

### 2. **OrderItem** (`order-service/src/main/java/org/rakhmonov/orderservice/entity/OrderItem.java`)
**Purpose**: Individual items within an order

**Key Features**:
- ✅ Product information snapshot
- ✅ Quantity and pricing
- ✅ Total amount calculation

### 3. **Cart** (`order-service/src/main/java/org/rakhmonov/orderservice/entity/Cart.java`)
**Purpose**: Simple shopping cart management

**Key Features**:
- ✅ User association
- ✅ Total amount calculation
- ✅ Cart items relationship

### 4. **CartItem** (`order-service/src/main/java/org/rakhmonov/orderservice/entity/CartItem.java`)
**Purpose**: Individual items in shopping cart

**Key Features**:
- ✅ Product information
- ✅ Quantity and pricing
- ✅ Cart association

---

## 🏗️ **Entity Relationships**

### **Auth Service**
```
User (1) ←→ (N) RefreshToken
```

### **Inventory Service**
```
Category (1) ←→ (N) Product
Supplier (1) ←→ (N) Product
```

### **Order Service**
```
Order (1) ←→ (N) OrderItem
Cart (1) ←→ (N) CartItem
```

---

## 🎯 **Simplified Design Benefits**

1. **Easy to Learn**: Fewer fields = easier to understand
2. **Faster Development**: Less complexity = quicker implementation
3. **Clear Relationships**: Simple one-to-many relationships
4. **Essential Features Only**: Core e-commerce functionality
5. **Easy to Extend**: Can add fields later as needed

---

## 📊 **Database Schema Summary**

| Service | Tables | Primary Entities |
|---------|--------|------------------|
| **Auth** | 2 | User, RefreshToken |
| **Inventory** | 3 | Product, Category, Supplier |
| **Order** | 4 | Order, OrderItem, Cart, CartItem |

**Total**: 9 tables across all services

---

## 🚀 **Learning Path**

### **Phase 1: Basic CRUD Operations**
1. Create repositories for each entity
2. Implement basic CRUD services
3. Create simple REST controllers
4. Test basic operations

### **Phase 2: Business Logic**
1. User authentication and authorization
2. Product catalog and search
3. Shopping cart functionality
4. Order processing

### **Phase 3: Advanced Features**
1. Add more fields as needed
2. Implement complex business rules
3. Add validation and error handling
4. Performance optimization

---

## 🎯 **What You Can Build Now**

With these simplified entities, you can create:

1. **User Management**: Registration, login, profile management
2. **Product Catalog**: Browse products by category
3. **Shopping Cart**: Add/remove items, calculate totals
4. **Order Processing**: Create orders, track status
5. **Multi-Seller Support**: Different suppliers can add products

The foundation is **perfect for learning** and **easy to extend**! 🎉
