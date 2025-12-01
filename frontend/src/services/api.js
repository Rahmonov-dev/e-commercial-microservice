import axios from 'axios';
import { tokenService } from './tokenService';

// Create axios instance
const api = axios.create({
  baseURL: 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add access token to all requests
api.interceptors.request.use(
  (config) => {
    const token = tokenService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle token refresh on 401
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // If error is 401 and we haven't retried yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = tokenService.getRefreshToken();
        
        if (!refreshToken) {
          // No refresh token, redirect to login
          tokenService.clearTokens();
          window.location.href = '/login';
          return Promise.reject(error);
        }

        // Try to refresh the token (no auth header needed)
        const response = await axios.post(
          'http://localhost:8081/auth/refresh-token',
          { refreshToken },
          {
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );

        const { token: accessToken, refreshToken: newRefreshToken } = response.data;
        
        // Save new tokens
        tokenService.setTokens(accessToken, newRefreshToken);

        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh failed, clear tokens and redirect to login
        tokenService.clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

// Create inventory API instance
const inventoryAPI = axios.create({
  baseURL: 'http://localhost:8082',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create user service API instance
const userServiceAPI = axios.create({
  baseURL: 'http://localhost:8085',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create warehouse service API instance
const warehouseAPI = axios.create({
  baseURL: 'http://localhost:8086',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create order service API instance
const orderServiceAPI = axios.create({
  baseURL: 'http://localhost:8083',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 seconds timeout
});

// Request interceptor for user service API
userServiceAPI.interceptors.request.use(
  (config) => {
    const token = tokenService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Request interceptor for auth API (for user management)
api.interceptors.request.use(
  (config) => {
    const token = tokenService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Request interceptor for warehouse API
warehouseAPI.interceptors.request.use(
  (config) => {
    const token = tokenService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Request interceptor for order service API
orderServiceAPI.interceptors.request.use(
  (config) => {
    const token = tokenService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Request interceptor for inventory API
inventoryAPI.interceptors.request.use(
  (config) => {
    const token = tokenService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth API endpoints
export const authAPI = {
  // Register new user
  register: async (userData) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },

  // Login user
  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  },

  // Refresh access token
  refreshToken: async (refreshToken) => {
    const response = await axios.post(
      'http://localhost:8081/auth/refresh-token',
      { refreshToken },
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
    return response.data;
  },

  // Logout user
  logout: async (refreshToken) => {
    const response = await api.post('/auth/logout', { refreshToken });
    return response.data;
  },
};

// Product API endpoints
export const productAPI = {
  // Get all products with pagination
  getAllProducts: async (page = 0, size = 20) => {
    const response = await inventoryAPI.get(`/api/products`, {
      params: { page, size },
    });
    return response.data;
  },

  // Get product by ID
  getProductById: async (id) => {
    const response = await inventoryAPI.get(`/api/products/${id}`);
    return response.data;
  },

  // Search products
  searchProducts: async (keyword, page = 0, size = 20) => {
    const response = await inventoryAPI.get(`/api/products/search`, {
      params: { keyword, page, size },
    });
    return response.data;
  },

  // Get products by category
  getProductsByCategory: async (categoryId, page = 0, size = 20) => {
    const response = await inventoryAPI.get(`/api/products/category/${categoryId}`, {
      params: { page, size },
    });
    return response.data;
  },

  // Get my products (current user's products)
  getMyProducts: async (page = 0, size = 20) => {
    const response = await inventoryAPI.get(`/api/products/my-products`, {
      params: { page, size, sort: 'createdAt,desc' },
    });
    return response.data;
  },

  // Get products by seller ID
  getProductsBySellerId: async (sellerId, page = 0, size = 20) => {
    const response = await inventoryAPI.get(`/api/products/seller/${sellerId}`, {
      params: { page, size, sort: 'createdAt,desc' },
    });
    return response.data;
  },

  // Update product
  updateProduct: async (productId, productData) => {
    const formData = new FormData();
    if (productData.name) formData.append('name', productData.name);
    if (productData.description !== undefined) formData.append('description', productData.description || '');
    if (productData.price !== undefined) formData.append('price', productData.price);
    if (productData.sku !== undefined) formData.append('sku', productData.sku || '');
    if (productData.barcode !== undefined) formData.append('barcode', productData.barcode || '');
    if (productData.categoryId) formData.append('categoryId', productData.categoryId);
    
    if (productData.imageUrl && productData.imageUrl instanceof File) {
      formData.append('imageUrl', productData.imageUrl);
    } else if (productData.imageUrl) {
      formData.append('imageUrl', productData.imageUrl);
    }
    
    if (productData.supplierId) {
      formData.append('supplierId', productData.supplierId);
    }

    const response = await inventoryAPI.put(`/api/products/${productId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // Delete product
  deleteProduct: async (productId) => {
    const response = await inventoryAPI.delete(`/api/products/${productId}`);
    return response.data;
  },

  // Create product
  createProduct: async (productData) => {
    const formData = new FormData();
    formData.append('name', productData.name);
    formData.append('description', productData.description || '');
    formData.append('price', productData.price);
    formData.append('sku', productData.sku || '');
    formData.append('barcode', productData.barcode || '');
    formData.append('categoryId', productData.categoryId);
    
    if (productData.imageUrl && productData.imageUrl instanceof File) {
      formData.append('imageUrl', productData.imageUrl);
    } else if (productData.imageUrl) {
      formData.append('imageUrl', productData.imageUrl);
    }
    
    if (productData.thirdPartySellerId) {
      formData.append('thirdPartySellerId', productData.thirdPartySellerId);
    }
    
    if (productData.supplierId) {
      formData.append('supplierId', productData.supplierId);
    }
    
    // Inventory fields (optional - if provided, inventory will be created automatically)
    if (productData.warehouseId) {
      formData.append('warehouseId', productData.warehouseId);
    }
    if (productData.currentStock !== undefined && productData.currentStock !== null) {
      formData.append('currentStock', productData.currentStock);
    }
    if (productData.reorderPoint !== undefined && productData.reorderPoint !== null) {
      formData.append('reorderPoint', productData.reorderPoint);
    }
    if (productData.unitCost !== undefined && productData.unitCost !== null) {
      formData.append('unitCost', productData.unitCost);
    }

    const response = await inventoryAPI.post('/api/products', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },
};

// Product Variant API endpoints
export const productVariantAPI = {
  // Get all variants for a product
  getProductVariants: async (productId) => {
    const response = await inventoryAPI.get(`/api/products/${productId}/variants`);
    return response.data;
  },

  // Get variant by ID
  getVariantById: async (productId, variantId) => {
    const response = await inventoryAPI.get(`/api/products/${productId}/variants/${variantId}`);
    return response.data;
  },

  // Create variant
  createVariant: async (productId, variantData) => {
    const formData = new FormData();
    formData.append('variantName', variantData.variantName);
    formData.append('variantValue', variantData.variantValue);
    if (variantData.sku) formData.append('sku', variantData.sku);
    if (variantData.price) formData.append('price', variantData.price);
    if (variantData.stockQuantity !== undefined) formData.append('stockQuantity', variantData.stockQuantity);
    if (variantData.isDefault !== undefined) formData.append('isDefault', variantData.isDefault);
    if (variantData.imageUrl && variantData.imageUrl instanceof File) {
      formData.append('imageUrl', variantData.imageUrl);
    }
    
    const response = await inventoryAPI.post(`/api/products/${productId}/variants`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // Update variant
  updateVariant: async (productId, variantId, variantData) => {
    const formData = new FormData();
    formData.append('variantName', variantData.variantName);
    formData.append('variantValue', variantData.variantValue);
    if (variantData.sku) formData.append('sku', variantData.sku);
    if (variantData.price) formData.append('price', variantData.price);
    if (variantData.stockQuantity !== undefined) formData.append('stockQuantity', variantData.stockQuantity);
    if (variantData.isDefault !== undefined) formData.append('isDefault', variantData.isDefault);
    if (variantData.imageUrl && variantData.imageUrl instanceof File) {
      formData.append('imageUrl', variantData.imageUrl);
    }
    
    const response = await inventoryAPI.put(`/api/products/${productId}/variants/${variantId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // Delete variant
  deleteVariant: async (productId, variantId) => {
    const response = await inventoryAPI.delete(`/api/products/${productId}/variants/${variantId}`);
    return response.data;
  },
};

// Category API endpoints
export const categoryAPI = {
  // Get all categories with pagination
  getAllCategories: async (page = 0, size = 20) => {
    const response = await inventoryAPI.get(`/categories`, {
      params: { page, size, sort: 'name' },
    });
    return response.data;
  },

  // Get category by ID
  getCategoryById: async (id) => {
    const response = await inventoryAPI.get(`/categories/${id}`);
    return response.data;
  },

  // Search categories
  searchCategories: async (name, page = 0, size = 20) => {
    const response = await inventoryAPI.get(`/categories/search`, {
      params: { name, page, size },
    });
    return response.data;
  },

  // Create category
  createCategory: async (categoryData) => {
    const response = await inventoryAPI.post('/categories', categoryData);
    return response.data;
  },

  // Update category
  updateCategory: async (id, categoryData) => {
    const response = await inventoryAPI.put(`/categories/${id}`, categoryData);
    return response.data;
  },

  // Delete category
  deleteCategory: async (id) => {
    const response = await inventoryAPI.delete(`/categories/${id}`);
    return response.data;
  },
};

// Role API endpoints
export const roleAPI = {
  // Get all roles
  getAllRoles: async () => {
    const response = await api.get('/roles');
    return response.data;
  },

  // Get role by ID
  getRoleById: async (id) => {
    const response = await api.get(`/roles/${id}`);
    return response.data;
  },

  // Create role
  createRole: async (roleData) => {
    const response = await api.post('/roles', roleData);
    return response.data;
  },

  // Update role
  updateRole: async (id, roleData) => {
    const response = await api.put(`/roles/${id}`, roleData);
    return response.data;
  },

  // Delete role
  deleteRole: async (id) => {
    const response = await api.delete(`/roles/${id}`);
    return response.data;
  },
};

// Permission API endpoints
export const permissionAPI = {
  // Get all permissions
  getAllPermissions: async () => {
    const response = await api.get('/permissions');
    return response.data;
  },

  // Get permission by ID
  getPermissionById: async (id) => {
    const response = await api.get(`/permissions/${id}`);
    return response.data;
  },

  // Create permission
  createPermission: async (permissionData) => {
    const response = await api.post('/permissions', permissionData);
    return response.data;
  },

  // Update permission
  updatePermission: async (id, permissionData) => {
    const response = await api.put(`/permissions/${id}`, permissionData);
    return response.data;
  },

  // Delete permission
  deletePermission: async (id) => {
    const response = await api.delete(`/permissions/${id}`);
    return response.data;
  },
};

// Warehouse API endpoints
export const warehouseAPIEndpoints = {
  // Create warehouse
  createWarehouse: async (warehouseData) => {
    const response = await warehouseAPI.post('/api/warehouses', warehouseData);
    return response.data;
  },

  // Get all warehouses
  getAllWarehouses: async (page = 0, size = 20) => {
    const response = await warehouseAPI.get('/api/warehouses', {
      params: { page, size },
    });
    return response.data;
  },

  // Get warehouse by ID
  getWarehouseById: async (id) => {
    const response = await warehouseAPI.get(`/api/warehouses/${id}`);
    return response.data;
  },
};

// Cart API endpoints
export const cartAPI = {
  // Get or create cart for user
  getOrCreateCart: async (userId) => {
    const response = await orderServiceAPI.get(`/api/carts/user/${userId}/get-or-create`);
    return response.data;
  },

  // Get cart by user ID
  getCartByUserId: async (userId) => {
    const response = await orderServiceAPI.get(`/api/carts/user/${userId}`);
    return response.data;
  },

  // Get cart items by user ID
  getCartItemsByUserId: async (userId) => {
    const response = await orderServiceAPI.get(`/api/carts/user/${userId}/items`);
    return response.data;
  },

  // Add item to cart
  addItemToCart: async (cartData) => {
    const response = await orderServiceAPI.post('/api/carts/items', cartData);
    return response.data;
  },

  // Update cart item quantity
  updateCartItemQuantity: async (itemId, quantity) => {
    const response = await orderServiceAPI.put(`/api/carts/items/${itemId}/quantity`, null, {
      params: { quantity },
    });
    return response.data;
  },

  // Remove item from cart
  removeItemFromCart: async (itemId) => {
    const response = await orderServiceAPI.delete(`/api/carts/items/${itemId}`);
    return response.data;
  },

  // Clear cart
  clearCart: async (userId) => {
    const response = await orderServiceAPI.delete(`/api/carts/user/${userId}/clear`);
    return response.data;
  },

  // Clear cart items by user ID
  clearCartItemsByUserId: async (userId) => {
    const response = await orderServiceAPI.delete(`/api/carts/user/${userId}/items/clear`);
    return response.data;
  },
};

// Order API endpoints
export const orderAPI = {
  // Create order
  createOrder: async (orderData) => {
    const response = await orderServiceAPI.post('/api/orders', orderData);
    return response.data;
  },

  // Get order by ID
  getOrderById: async (id) => {
    const response = await orderServiceAPI.get(`/api/orders/${id}`);
    return response.data;
  },

  // Get orders by user ID
  getOrdersByUserId: async (userId, page = 0, size = 20) => {
    const response = await orderServiceAPI.get(`/api/orders/user/${userId}`, {
      params: { page, size, sort: 'createdAt,desc' },
    });
    return response.data;
  },

  // Get order by order number
  getOrderByOrderNumber: async (orderNumber) => {
    const response = await orderServiceAPI.get(`/api/orders/number/${orderNumber}`);
    return response.data;
  },

  // Get orders by status
  getOrdersByStatus: async (status, page = 0, size = 20) => {
    const response = await orderServiceAPI.get(`/api/orders/status/${status}`, {
      params: { page, size, sort: 'createdAt,desc' },
    });
    return response.data;
  },

  // Get all orders (admin only)
  getAllOrders: async (page = 0, size = 20) => {
    const response = await orderServiceAPI.get('/api/orders', {
      params: { page, size, sort: 'createdAt,desc' },
    });
    return response.data;
  },
};

// Third Party Seller API endpoints
export const sellerAPI = {
  // Register as third-party seller
  registerSeller: async (sellerData) => {
    const response = await userServiceAPI.post('/api/third-party-sellers', sellerData);
    return response.data;
  },

  // Get seller by ID
  getSellerById: async (id) => {
    const response = await userServiceAPI.get(`/api/third-party-sellers/${id}`);
    return response.data;
  },

  // Get current user's seller profile
  getMySellerProfile: async () => {
    const response = await userServiceAPI.get('/api/third-party-sellers/me');
    return response.data;
  },

  // Update seller profile
  updateSeller: async (id, sellerData) => {
    const response = await userServiceAPI.put(`/api/third-party-sellers/${id}`, sellerData);
    return response.data;
  },
};

// User API endpoints (from auth-service)
export const userAPI = {
  // Get all active users
  getAllUsers: async () => {
    const response = await api.get('/users');
    return response.data;
  },

  // Get user by ID
  getUserById: async (id) => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  // Get users by role
  getUsersByRole: async (roleName) => {
    const response = await api.get(`/users/role/${roleName}`);
    return response.data;
  },

  // Get users by status
  getUsersByStatus: async (status) => {
    const response = await api.get(`/users/status/${status}`);
    return response.data;
  },

  // Get all sellers (users with SELLER role)
  getAllSellers: async () => {
    const response = await api.get('/users/role/SELLER');
    return response.data;
  },
};

// Wishlist API endpoints
export const wishlistAPI = {
  // Create wishlist
  createWishlist: async (userId) => {
    const response = await orderServiceAPI.post('/api/wishlists', { userId });
    return response.data;
  },

  // Get wishlist by user ID
  getWishlistByUserId: async (userId) => {
    try {
      const response = await orderServiceAPI.get(`/api/wishlists/user/${userId}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404 || error.response?.status === 500) {
        return null; // Wishlist not found
      }
      throw error;
    }
  },

  // Get or create wishlist for user
  getOrCreateWishlist: async (userId) => {
    try {
      // Try to get existing wishlist
      const wishlist = await wishlistAPI.getWishlistByUserId(userId);
      if (wishlist) {
        return wishlist;
      }
      // Create new wishlist if not found
      return await wishlistAPI.createWishlist(userId);
    } catch (error) {
      console.error('Error getting or creating wishlist:', error);
      throw error;
    }
  },

  // Add item to wishlist
  addItemToWishlist: async (wishlistId, product) => {
    const itemRequest = {
      productId: product.id,
      productName: product.name || product.productName,
      productDescription: product.description || '',
      productImageUrl: product.imageUrl || product.image || '',
      productPrice: product.price || product.productPrice || 0
    };
    const response = await orderServiceAPI.post(`/api/wishlists/${wishlistId}/items`, itemRequest);
    return response.data;
  },

  // Remove item from wishlist
  removeItemFromWishlist: async (wishlistId, itemId) => {
    const response = await orderServiceAPI.delete(`/api/wishlists/${wishlistId}/items/${itemId}`);
    return response.data;
  },

  // Check if product is in wishlist
  isProductInWishlist: async (userId, productId) => {
    try {
      const wishlist = await wishlistAPI.getWishlistByUserId(userId);
      if (!wishlist || !wishlist.wishlistItems) {
        return { exists: false, itemId: null };
      }
      const item = wishlist.wishlistItems.find(item => item.productId === productId);
      return {
        exists: !!item,
        itemId: item?.id || null
      };
    } catch (error) {
      return { exists: false, itemId: null };
    }
  }
};

export default api;










