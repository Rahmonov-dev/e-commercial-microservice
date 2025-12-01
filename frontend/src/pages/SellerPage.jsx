import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { useNavigate } from 'react-router-dom';
import { productAPI, categoryAPI, warehouseAPIEndpoints, orderAPI, productVariantAPI } from '../services/api';
import { tokenService } from '../services/tokenService';
import './SellerPage.css';

const SellerPage = () => {
  const { user, userRole } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [showAddProduct, setShowAddProduct] = useState(false);
  const [categories, setCategories] = useState([]);
  const [productForm, setProductForm] = useState({
    name: '',
    description: '',
    price: '',
    sku: '',
    barcode: '',
    categoryId: '',
    imageUrl: null,
    warehouseId: '',
    currentStock: '',
    reorderPoint: '',
    unitCost: '',
  });
  const [warehouses, setWarehouses] = useState([]);
  const [productLoading, setProductLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [products, setProducts] = useState([]);
  const [productsLoading, setProductsLoading] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [showManageProducts, setShowManageProducts] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [activeTab, setActiveTab] = useState('dashboard');
  const [orders, setOrders] = useState([]);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [sellerProductIds, setSellerProductIds] = useState([]);
  const [createdProductId, setCreatedProductId] = useState(null);
  const [variants, setVariants] = useState([]);
  const [showVariants, setShowVariants] = useState(false);
  const [variantForm, setVariantForm] = useState({
    variantName: '',
    variantValue: '',
    sku: '',
    price: '',
    stockQuantity: '0',
    isDefault: false,
    imageUrl: null,
  });
  
  // Calculate pageSize based on screen size
  const calculatePageSize = () => {
    const width = window.innerWidth;
    if (width >= 1400) return 12; // Large desktop: 4 columns * 3 rows
    if (width >= 1200) return 9;  // Desktop: 3 columns * 3 rows
    if (width >= 968) return 6;  // Tablet: 2 columns * 3 rows
    if (width >= 768) return 4;  // Small tablet: 2 columns * 2 rows
    return 2; // Mobile: 1 column * 2 rows
  };
  
  const [pageSize, setPageSize] = useState(calculatePageSize());
  
  // Update pageSize on window resize
  useEffect(() => {
    const handleResize = () => {
      const newPageSize = calculatePageSize();
      setPageSize(newPageSize);
      // Reset to first page if current page would be out of bounds
      const newTotalPages = Math.ceil(products.length / newPageSize);
      if (currentPage >= newTotalPages && newTotalPages > 0) {
        setCurrentPage(0);
      }
    };
    
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [products.length, currentPage]);

  // Redirect if not seller
  useEffect(() => {
    if (userRole !== 'ROLE_SELLER' && userRole !== 'SELLER') {
      navigate('/');
    }
  }, [userRole, navigate]);

  // Flatten categories hierarchy to show all categories (parent + children)
  const flattenCategories = (categories) => {
    const result = [];
    const flatten = (cats, level = 0) => {
      cats.forEach(cat => {
        result.push({ ...cat, level, displayName: '  '.repeat(level) + cat.name });
        if (cat.subCategories && cat.subCategories.length > 0) {
          flatten(cat.subCategories, level + 1);
        }
      });
    };
    flatten(categories);
    return result;
  };
  
  const [allCategoriesFlat, setAllCategoriesFlat] = useState([]);

  // Load categories
  useEffect(() => {
    const loadCategories = async () => {
      try {
        const response = await categoryAPI.getAllCategories(0, 100);
        const categoriesList = response.content || [];
        setCategories(categoriesList);
        // Flatten categories for dropdowns
        setAllCategoriesFlat(flattenCategories(categoriesList));
      } catch (error) {
        console.error('Error loading categories:', error);
      }
    };
    loadCategories();
  }, []);

  // Load warehouses
  useEffect(() => {
    const loadWarehouses = async () => {
      try {
        const response = await warehouseAPIEndpoints.getAllWarehouses(0, 100);
        const warehousesList = response.content || [];
        setWarehouses(warehousesList);
      } catch (error) {
        console.error('Error loading warehouses:', error);
      }
    };
    loadWarehouses();
  }, []);

  // Load seller products and extract IDs
  useEffect(() => {
    const loadSellerProducts = async () => {
      try {
        const response = await productAPI.getMyProducts(0, 1000);
        const productsList = response.content || response || [];
        const productIds = productsList.map(p => p.id);
        setSellerProductIds(productIds);
      } catch (error) {
        console.error('Error loading seller products:', error);
        setSellerProductIds([]);
      }
    };
    loadSellerProducts();
  }, []);

  // Load products
  useEffect(() => {
    const loadProducts = async () => {
      if (!showManageProducts) return;
      
      setProductsLoading(true);
      try {
        const response = await productAPI.getMyProducts(0, 100);
        const productsList = response.content || response || [];
        setProducts(productsList);
      } catch (error) {
        console.error('Error loading products:', error);
        setProducts([]);
      } finally {
        setProductsLoading(false);
      }
    };
    loadProducts();
  }, [showManageProducts]);

  // Load orders for seller
  useEffect(() => {
    const loadSellerOrders = async () => {
      if (activeTab !== 'orders') {
        setOrders([]);
        return;
      }

      if (sellerProductIds.length === 0) {
        setOrders([]);
        setOrdersLoading(false);
        return;
      }

      setOrdersLoading(true);
      setErrorMessage('');
      
      try {
        let allOrders = [];
        let ordersLoaded = false;
        
        // Strategy 1: Try to get orders by status (CONFIRMED and PENDING)
        // We need both statuses: PENDING for cash payments, CONFIRMED for confirmed orders
        try {
          // Get CONFIRMED orders
          const confirmedResponse = await orderAPI.getOrdersByStatus('CONFIRMED', 0, 1000);
          const confirmedOrders = confirmedResponse.content || confirmedResponse || [];
          
          // Get PENDING orders (cash payments)
          let pendingOrders = [];
          try {
            const pendingResponse = await orderAPI.getOrdersByStatus('PENDING', 0, 1000);
            pendingOrders = pendingResponse.content || pendingResponse || [];
          } catch (pendingError) {
            console.log('⚠️ Could not get PENDING orders:', pendingError.response?.status || pendingError.message);
          }
          
          // Combine both
          allOrders = [...confirmedOrders, ...pendingOrders];
          ordersLoaded = true;
          console.log(`✅ Loaded ${confirmedOrders.length} CONFIRMED + ${pendingOrders.length} PENDING = ${allOrders.length} total orders`);
        } catch (statusError) {
          console.log('⚠️ Could not get orders by status:', statusError.response?.status || statusError.message);
          
          // Strategy 2: Try to get all orders (if admin or endpoint allows)
          try {
            const allResponse = await orderAPI.getAllOrders(0, 1000);
            allOrders = allResponse.content || allResponse || [];
            ordersLoaded = true;
            console.log(`✅ Loaded ${allOrders.length} all orders (fallback)`);
          } catch (allError) {
            console.log('⚠️ Could not get all orders:', allError.response?.status || allError.message);
            
            // Strategy 3: If user is logged in, try to get their orders and check if any contain seller products
            // This is a workaround - ideally backend should have a seller-specific endpoint
            if (user?.id) {
              try {
                const userOrdersResponse = await orderAPI.getOrdersByUserId(user.id, 0, 1000);
                const userOrders = userOrdersResponse.content || userOrdersResponse || [];
                // Note: This only gets orders where user is the buyer, not seller
                // This won't work for seller's orders, but we try anyway
                allOrders = userOrders;
                ordersLoaded = true;
                console.log(`⚠️ Loaded ${allOrders.length} user orders (limited - may not show all seller orders)`);
              } catch (userError) {
                console.error('❌ Could not get user orders:', userError);
              }
            }
          }
        }
        
        if (!ordersLoaded || allOrders.length === 0) {
          console.warn('⚠️ No orders could be loaded. Backend endpoint may require authentication or seller-specific endpoint needed.');
          setOrders([]);
          setOrdersLoading(false);
          return;
        }
        
        // Filter orders that contain seller's products
        // Logic:
        // 1. Customer creates order with orderItems (each has productId)
        // 2. We check if any orderItem.productId matches seller's product IDs
        // 3. Show orders with status: CONFIRMED, ORDERED, or PENDING (for cash payments)
        //    - PENDING: Customer just created order (cash payment - waiting for confirmation)
        //    - CONFIRMED: Order confirmed by admin
        //    - ORDERED: Alternative status name for confirmed orders
        const sellerOrders = allOrders.filter(order => {
          // Step 1: Check status - show CONFIRMED, ORDERED, and PENDING orders
          // PENDING is shown because cash payments start as PENDING and need seller attention
          const validStatuses = ['CONFIRMED', 'ORDERED', 'PENDING'];
          if (!validStatuses.includes(order.status)) {
            return false;
          }
          
          // Step 2: Check if order has items
          if (!order.orderItems || order.orderItems.length === 0) {
            return false;
          }
          
          // Step 3: Check if any order item has a product ID that belongs to this seller
          // This is the key logic: if customer ordered seller's product, show it to seller
          const hasSellerProduct = order.orderItems.some(item => 
            sellerProductIds.includes(item.productId)
          );
          
          return hasSellerProduct;
        });

        console.log(`✅ Filtered ${sellerOrders.length} orders containing seller's products from ${allOrders.length} total orders`);
        setOrders(sellerOrders);
        
      } catch (error) {
        console.error('❌ Error loading seller orders:', error);
        setErrorMessage('Buyurtmalarni yuklashda xatolik yuz berdi. Iltimos, qayta urinib ko\'ring.');
        setOrders([]);
      } finally {
        setOrdersLoading(false);
      }
    };
    loadSellerOrders();
  }, [activeTab, sellerProductIds, user?.id]);

  // Pagination calculations
  const totalPages = Math.ceil(products.length / pageSize);
  const startIndex = currentPage * pageSize;
  const endIndex = startIndex + pageSize;
  const currentProducts = products.slice(startIndex, endIndex);

  const handlePageChange = (newPage) => {
    if (newPage < 0 || newPage >= Math.ceil(products.length / pageSize)) return;
    setCurrentPage(newPage);
    // Scroll to top of product list wrapper
    setTimeout(() => {
      const productListWrapper = document.querySelector('.product-list-wrapper');
      if (productListWrapper) {
        productListWrapper.scrollTop = 0;
      }
    }, 0);
  };

  const handleProductFormChange = (e) => {
    const { name, value, files } = e.target;
    if (name === 'imageUrl' && files && files[0]) {
      setProductForm({ ...productForm, imageUrl: files[0] });
    } else {
      setProductForm({ ...productForm, [name]: value });
    }
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    setProductLoading(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const productData = {
        name: productForm.name,
        description: productForm.description,
        price: parseFloat(productForm.price),
        sku: productForm.sku,
        barcode: productForm.barcode,
        categoryId: parseInt(productForm.categoryId),
        imageUrl: productForm.imageUrl,
        // Inventory fields (required)
        warehouseId: parseInt(productForm.warehouseId),
        currentStock: parseInt(productForm.currentStock),
        reorderPoint: parseInt(productForm.reorderPoint),
        unitCost: parseFloat(productForm.unitCost),
      };

      if (editingProduct) {
        // Update existing product
        await productAPI.updateProduct(editingProduct.id, productData);
        setSuccessMessage(t('productUpdated') || 'Product updated successfully');
        setEditingProduct(null);
        setShowAddProduct(false);
      } else {
        // Create new product
        const createdProduct = await productAPI.createProduct(productData);
        setCreatedProductId(createdProduct.id);
        setSuccessMessage(t('productAdded'));
        setShowVariants(true);
      }
      
      if (!editingProduct) {
        // Don't reset form if creating new product (for variants)
      } else {
        setProductForm({
          name: '',
          description: '',
          price: '',
          sku: '',
          barcode: '',
          categoryId: '',
          imageUrl: null,
          warehouseId: '',
          currentStock: '',
          reorderPoint: '',
          unitCost: '',
        });
      }
      
      // Reload products if manage products is open
      if (showManageProducts) {
        const response = await productAPI.getMyProducts(0, 100);
        const productsList = response.content || response || [];
        setProducts(productsList);
        setCurrentPage(0); // Reset to first page after add/update
      }
      
      setTimeout(() => {
        setSuccessMessage('');
      }, 3000);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || t('productAddError'));
    } finally {
      setProductLoading(false);
    }
  };

  const handleVariantFormChange = (e) => {
    const { name, value, type, checked, files } = e.target;
    if (name === 'imageUrl' && files && files[0]) {
      setVariantForm({ ...variantForm, imageUrl: files[0] });
    } else {
      setVariantForm({
        ...variantForm,
        [name]: type === 'checkbox' ? checked : value,
      });
    }
  };

  const handleAddVariant = async (e) => {
    e.preventDefault();
    if (!createdProductId) return;

    try {
      const variantData = {
        variantName: variantForm.variantName,
        variantValue: variantForm.variantValue,
        sku: variantForm.sku || null,
        price: variantForm.price ? parseFloat(variantForm.price) : null,
        stockQuantity: parseInt(variantForm.stockQuantity) || 0,
        isDefault: variantForm.isDefault,
        imageUrl: variantForm.imageUrl,
      };

      const createdVariant = await productVariantAPI.createVariant(createdProductId, variantData);
      setVariants([...variants, createdVariant]);
      setVariantForm({
        variantName: '',
        variantValue: '',
        sku: '',
        price: '',
        stockQuantity: '0',
        isDefault: false,
        imageUrl: null,
      });
      setSuccessMessage(t('variantAdded') || 'Variant added successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || t('variantAddError') || 'Failed to add variant');
    }
  };

  const handleFinishProductCreation = () => {
    setShowAddProduct(false);
    setShowVariants(false);
    setCreatedProductId(null);
    setVariants([]);
    setProductForm({
      name: '',
      description: '',
      price: '',
      sku: '',
      barcode: '',
      categoryId: '',
      imageUrl: null,
      warehouseId: '',
      currentStock: '',
      reorderPoint: '',
      unitCost: '',
    });
    setVariantForm({
      variantName: '',
      variantValue: '',
      sku: '',
      price: '',
      stockQuantity: '0',
      isDefault: false,
      imageUrl: null,
    });
    setSuccessMessage('');
    setErrorMessage('');
  };

  if (userRole !== 'ROLE_SELLER' && userRole !== 'SELLER') {
    return null;
  }

  return (
    <div className="seller-page">
      <Navbar showUserInfo={true} />
      
      <div className="seller-main-container">
        {/* Sidebar Navigation */}
        <aside className="seller-sidebar">
          <div className="sidebar-header">
            <div className="seller-avatar">
              <span>{user?.firstName?.charAt(0) || 'S'}</span>
            </div>
            <h3>{user?.firstName || 'Seller'}</h3>
            <p className="seller-role">Sotuvchi</p>
          </div>
          
          <nav className="sidebar-nav">
            <button 
              className={`nav-item ${activeTab === 'dashboard' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('dashboard');
                setShowAddProduct(false);
                setShowManageProducts(false);
              }}
            >
              <span className="nav-icon">📊</span>
              <span className="nav-text">Dashboard</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'add-product' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('add-product');
                setShowAddProduct(true);
                setShowManageProducts(false);
              }}
            >
              <span className="nav-icon">➕</span>
              <span className="nav-text">{t('addNewProduct')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'manage' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('manage');
                setShowManageProducts(true);
                setShowAddProduct(false);
              }}
            >
              <span className="nav-icon">📦</span>
              <span className="nav-text">{t('manageProducts')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'orders' ? 'active' : ''}`}
              onClick={() => setActiveTab('orders')}
            >
              <span className="nav-icon">🛒</span>
              <span className="nav-text">{t('viewOrders')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'analytics' ? 'active' : ''}`}
              onClick={() => setActiveTab('analytics')}
            >
              <span className="nav-icon">📈</span>
              <span className="nav-text">Analytics</span>
            </button>
          </nav>
        </aside>

        {/* Main Content */}
        <div className="seller-container">
          {activeTab === 'dashboard' && (
            <>
              <div className="seller-header">
                <div>
                  <h1 className="seller-title">{t('welcome')}, {user?.firstName || 'Seller'}! 👋</h1>
                  <p className="seller-subtitle">Bu sizning sotuvchi panelingiz</p>
                </div>
              </div>

              <div className="seller-stats">
                <div className="stat-card stat-card-1">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>📦</span>
                    </div>
                    <div className="stat-info">
                      <h3>My Products</h3>
                      <p className="stat-number">0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
                
                <div className="stat-card stat-card-2">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>🛒</span>
                    </div>
                    <div className="stat-info">
                      <h3>Total Orders</h3>
                      <p className="stat-number">0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
                
                <div className="stat-card stat-card-3">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>💰</span>
                    </div>
                    <div className="stat-info">
                      <h3>Total Revenue</h3>
                      <p className="stat-number">$0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
                
                <div className="stat-card stat-card-4">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>⭐</span>
                    </div>
                    <div className="stat-info">
                      <h3>Rating</h3>
                      <p className="stat-number">0.0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
              </div>

              <div className="quick-actions-grid">
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('add-product');
                  setShowAddProduct(true);
                  setShowManageProducts(false);
                }}>
                  <div className="action-icon">➕</div>
                  <h3>Mahsulot qo'shish</h3>
                  <p>Yangi mahsulot yarating</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('manage');
                  setShowManageProducts(true);
                  setShowAddProduct(false);
                }}>
                  <div className="action-icon">✏️</div>
                  <h3>Boshqarish</h3>
                  <p>Mahsulotlarni tahrirlang</p>
                </div>
                
                <div className="quick-action-card" onClick={() => setActiveTab('orders')}>
                  <div className="action-icon">📋</div>
                  <h3>Buyurtmalar</h3>
                  <p>Buyurtmalarni ko'ring</p>
                </div>
                
                <div className="quick-action-card" onClick={() => setActiveTab('analytics')}>
                  <div className="action-icon">📊</div>
                  <h3>Statistika</h3>
                  <p>Savdo hisobotlari</p>
                </div>
              </div>
            </>
          )}

          {activeTab === 'add-product' && showAddProduct && (
              <div className="seller-section">
                <h2>{editingProduct ? (t('editProduct') || 'Edit Product') : t('addProduct')}</h2>
                {successMessage && (
                  <div className="success-message">{successMessage}</div>
                )}
                {errorMessage && (
                  <div className="error-message">{errorMessage}</div>
                )}
                <form onSubmit={handleAddProduct} className="product-form">
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="productName">{t('productName')} *</label>
                      <input
                        type="text"
                        id="productName"
                        name="name"
                        value={productForm.name}
                        onChange={handleProductFormChange}
                        required
                        placeholder={t('productName')}
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="productPrice">{t('productPrice')} *</label>
                      <input
                        type="number"
                        id="productPrice"
                        name="price"
                        value={productForm.price}
                        onChange={handleProductFormChange}
                        required
                        step="0.01"
                        min="0"
                        placeholder="0.00"
                      />
                    </div>
                  </div>
                  <div className="form-group">
                    <label htmlFor="productDescription">{t('productDescription')}</label>
                    <textarea
                      id="productDescription"
                      name="description"
                      value={productForm.description}
                      onChange={handleProductFormChange}
                      rows="3"
                      placeholder={t('productDescription')}
                    />
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="productSku">{t('productSku')}</label>
                      <input
                        type="text"
                        id="productSku"
                        name="sku"
                        value={productForm.sku}
                        onChange={handleProductFormChange}
                        placeholder={t('productSku')}
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="productBarcode">{t('productBarcode')}</label>
                      <input
                        type="text"
                        id="productBarcode"
                        name="barcode"
                        value={productForm.barcode}
                        onChange={handleProductFormChange}
                        placeholder={t('productBarcode')}
                      />
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="productCategory">{t('productCategory')} *</label>
                      <select
                        id="productCategory"
                        name="categoryId"
                        value={productForm.categoryId}
                        onChange={handleProductFormChange}
                        required
                      >
                        <option value="">{t('selectCategory')}</option>
                        {allCategoriesFlat.map((cat) => (
                          <option key={cat.id} value={cat.id}>
                            {cat.displayName}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="form-group">
                      <label htmlFor="productImage">{t('productImage')}</label>
                      <input
                        type="file"
                        id="productImage"
                        name="imageUrl"
                        onChange={handleProductFormChange}
                        accept="image/*"
                      />
                    </div>
                  </div>
                  
                  {/* Inventory Fields (Required) */}
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="warehouseId">Warehouse *</label>
                      <select
                        id="warehouseId"
                        name="warehouseId"
                        value={productForm.warehouseId}
                        onChange={handleProductFormChange}
                        required
                      >
                        <option value="">Select Warehouse</option>
                        {warehouses.map((warehouse) => (
                          <option key={warehouse.id} value={warehouse.id}>
                            {warehouse.name} - {warehouse.city}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="form-group">
                      <label htmlFor="currentStock">Current Stock *</label>
                      <input
                        type="number"
                        id="currentStock"
                        name="currentStock"
                        value={productForm.currentStock}
                        onChange={handleProductFormChange}
                        required
                        min="0"
                        placeholder="0"
                      />
                    </div>
                  </div>
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="reorderPoint">Reorder Point *</label>
                      <input
                        type="number"
                        id="reorderPoint"
                        name="reorderPoint"
                        value={productForm.reorderPoint}
                        onChange={handleProductFormChange}
                        required
                        min="0"
                        placeholder="10"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="unitCost">Unit Cost *</label>
                      <input
                        type="number"
                        id="unitCost"
                        name="unitCost"
                        value={productForm.unitCost}
                        onChange={handleProductFormChange}
                        required
                        step="0.01"
                        min="0"
                        placeholder="0.00"
                      />
                    </div>
                  </div>
                  
                  <div className="form-actions">
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => {
                        setShowAddProduct(false);
                        setEditingProduct(null);
                        setShowVariants(false);
                        setCreatedProductId(null);
                        setVariants([]);
                        setProductForm({
                          name: '',
                          description: '',
                          price: '',
                          sku: '',
                          barcode: '',
                          categoryId: '',
                          imageUrl: null,
                          warehouseId: '',
                          currentStock: '',
                          reorderPoint: '',
                          unitCost: '',
                        });
                        setVariantForm({
                          variantName: '',
                          variantValue: '',
                          sku: '',
                          price: '',
                          stockQuantity: '0',
                          isDefault: false,
                          imageUrl: null,
                        });
                        setErrorMessage('');
                        setSuccessMessage('');
                      }}
                    >
                      {t('cancel')}
                    </button>
                    <button
                      type="submit"
                      className="btn btn-primary"
                      disabled={productLoading}
                    >
                      {productLoading ? t('creating') : t('submit')}
                    </button>
                  </div>
                </form>

                {/* Variants Section */}
                {showVariants && createdProductId && (
                  <div className="variants-section">
                    <div className="section-header">
                      <h3>{t('addVariants') || 'Add Product Variants'}</h3>
                      <p className="section-subtitle">{t('variantsHint') || 'Add different sizes, colors, or other variations of this product (optional)'}</p>
                    </div>
                    <form onSubmit={handleAddVariant} className="variant-form">
                      <div className="form-row">
                        <div className="form-group">
                          <label htmlFor="variantName">{t('variantName') || 'Variant Name'} *</label>
                          <input
                            type="text"
                            id="variantName"
                            name="variantName"
                            value={variantForm.variantName}
                            onChange={handleVariantFormChange}
                            required
                            placeholder={t('variantNamePlaceholder') || 'e.g., Size, Color, Material'}
                          />
                        </div>
                        <div className="form-group">
                          <label htmlFor="variantValue">{t('variantValue') || 'Variant Value'} *</label>
                          <input
                            type="text"
                            id="variantValue"
                            name="variantValue"
                            value={variantForm.variantValue}
                            onChange={handleVariantFormChange}
                            required
                            placeholder={t('variantValuePlaceholder') || 'e.g., Large, Red, Cotton'}
                          />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label htmlFor="variantSku">{t('variantSku') || 'Variant SKU'}</label>
                          <input
                            type="text"
                            id="variantSku"
                            name="sku"
                            value={variantForm.sku}
                            onChange={handleVariantFormChange}
                            placeholder={t('variantSkuPlaceholder') || 'Optional unique SKU'}
                          />
                        </div>
                        <div className="form-group">
                          <label htmlFor="variantPrice">{t('variantPrice') || 'Variant Price'}</label>
                          <input
                            type="number"
                            id="variantPrice"
                            name="price"
                            value={variantForm.price}
                            onChange={handleVariantFormChange}
                            step="0.01"
                            min="0"
                            placeholder={t('variantPricePlaceholder') || 'Override product price (optional)'}
                          />
                        </div>
                        <div className="form-group">
                          <label htmlFor="variantStock">{t('variantStock') || 'Stock Quantity'}</label>
                          <input
                            type="number"
                            id="variantStock"
                            name="stockQuantity"
                            value={variantForm.stockQuantity}
                            onChange={handleVariantFormChange}
                            min="0"
                            placeholder="0"
                          />
                        </div>
                      </div>
                      <div className="form-group">
                        <label htmlFor="variantImage">{t('variantImage') || 'Variant Image'}</label>
                        <input
                          type="file"
                          id="variantImage"
                          name="imageUrl"
                          onChange={handleVariantFormChange}
                          accept="image/*"
                        />
                      </div>
                      <div className="form-group">
                        <label className="checkbox-label">
                          <input
                            type="checkbox"
                            name="isDefault"
                            checked={variantForm.isDefault}
                            onChange={handleVariantFormChange}
                          />
                          {t('setAsDefault') || 'Set as default variant'}
                        </label>
                      </div>
                      <button type="submit" className="btn btn-secondary">
                        {t('addVariant') || 'Add Variant'}
                      </button>
                    </form>

                    {/* Variants List */}
                    {variants.length > 0 && (
                      <div className="variants-list">
                        <h4>{t('addedVariants') || 'Added Variants'}</h4>
                        <ul>
                          {variants.map((variant, index) => (
                            <li key={index}>
                              <strong>{variant.variantName}:</strong> {variant.variantValue}
                              {variant.price && ` - $${variant.price.toFixed(2)}`}
                              {variant.stockQuantity !== undefined && ` (Stock: ${variant.stockQuantity})`}
                              {variant.imageUrl && (
                                <img 
                                  src={variant.imageUrl.startsWith('http') ? variant.imageUrl : `http://localhost:8082${variant.imageUrl}`}
                                  alt={`${variant.variantName} ${variant.variantValue}`}
                                  style={{ width: '40px', height: '40px', objectFit: 'cover', marginLeft: '10px', borderRadius: '4px' }}
                                />
                              )}
                            </li>
                          ))}
                        </ul>
                      </div>
                    )}

                    <div className="form-actions">
                      <button
                        type="button"
                        className="btn btn-primary"
                        onClick={handleFinishProductCreation}
                      >
                        {t('finishProductCreation') || 'Finish Product Creation'}
                      </button>
                    </div>
                  </div>
                )}
              </div>
            )}

          {activeTab === 'manage' && showManageProducts && (
              <div className="seller-section product-management-section">
                <h2>{t('manageProducts') || 'Manage Products'}</h2>
                <div className="product-list-wrapper">
                  {productsLoading ? (
                    <div className="loading-state">
                      <p>{t('loading') || 'Loading...'}</p>
                    </div>
                  ) : products.length === 0 ? (
                    <div className="empty-state">
                      <p>{t('noProducts') || 'No products yet. Add your first product!'}</p>
                    </div>
                  ) : (
                    <>
                    <div className="product-list">
                    {currentProducts.map((product) => (
                      <div key={product.id} className="product-card">
                        <div className="product-image-wrapper">
                          <div className="product-image">
                            {product.imageUrl ? (
                              <img 
                                src={product.imageUrl.startsWith('http') ? product.imageUrl : `http://localhost:8082${product.imageUrl}`}
                                alt={product.name}
                              />
                            ) : (
                              <div className="no-image">📦 No Image</div>
                            )}
                          </div>
                          {product.inventory && product.inventory.currentStock > 0 && (
                            <div className="product-badge">
                              {product.inventory.currentStock <= 10 ? 'Low Stock' : 'In Stock'}
                            </div>
                          )}
                        </div>
                        <div className="product-info">
                          <div className="product-info-main">
                            <h3>{product.name}</h3>
                            <p className="product-description">{product.description || 'No description available'}</p>
                          </div>
                          <div className="product-info-footer">
                            <p className="product-price">${parseFloat(product.price || 0).toFixed(2)}</p>
                            <div className="product-meta">
                              {product.inventory && (
                                <p className={`product-stock ${
                                  product.inventory.currentStock === 0 
                                    ? 'out-of-stock' 
                                    : product.inventory.currentStock <= 10 
                                    ? 'low-stock' 
                                    : ''
                                }`}>
                                  {product.inventory.currentStock === 0 
                                    ? 'Out of Stock' 
                                    : `Stock: ${product.inventory.currentStock}`}
                                </p>
                              )}
                              <p className="product-date">
                                {new Date(product.createdAt).toLocaleDateString()}
                              </p>
                            </div>
                          </div>
                        </div>
                        <div className="product-actions">
                          <button
                            className="btn btn-secondary"
                            onClick={() => {
                              setEditingProduct(product);
                              setProductForm({
                                name: product.name,
                                description: product.description || '',
                                price: product.price?.toString() || '',
                                sku: product.sku || '',
                                barcode: product.barcode || '',
                                categoryId: product.category?.id?.toString() || '',
                                imageUrl: null,
                                warehouseId: product.inventory?.warehouseId?.toString() || '',
                                currentStock: product.inventory?.currentStock?.toString() || '',
                                reorderPoint: product.inventory?.reorderPoint?.toString() || '',
                                unitCost: product.inventory?.unitCost?.toString() || '',
                              });
                              setShowAddProduct(true);
                            }}
                          >
                            {t('edit') || 'Edit'}
                          </button>
                          <button
                            className="btn btn-danger"
                            onClick={async () => {
                              if (window.confirm(t('confirmDelete') || 'Are you sure you want to delete this product?')) {
                                try {
                                  await productAPI.deleteProduct(product.id);
                                  setSuccessMessage(t('productDeleted') || 'Product deleted successfully');
                                  const response = await productAPI.getMyProducts(0, 100);
                                  const productsList = response.content || response || [];
                                  setProducts(productsList);
                                  setCurrentPage(0); // Reset to first page after delete
                                  setTimeout(() => setSuccessMessage(''), 3000);
                                } catch (error) {
                                  setErrorMessage(error.response?.data?.message || t('deleteError') || 'Failed to delete product');
                                  setTimeout(() => setErrorMessage(''), 3000);
                                }
                              }
                            }}
                          >
                            {t('delete') || 'Delete'}
                          </button>
                        </div>
                      </div>
                    ))}
                    </div>
                    {totalPages > 1 && (
                      <div className="pagination">
                        <button
                          className="pagination-btn"
                          onClick={() => handlePageChange(currentPage - 1)}
                          disabled={currentPage === 0}
                        >
                          ← {t('previous') || 'Previous'}
                        </button>
                        <span className="pagination-info">
                          {t('page') || 'Page'} {currentPage + 1} {t('of') || 'of'} {totalPages} ({products.length} {t('total') || 'total'})
                        </span>
                        <button
                          className="pagination-btn"
                          onClick={() => handlePageChange(currentPage + 1)}
                          disabled={currentPage >= totalPages - 1}
                        >
                          {t('next') || 'Next'} →
                        </button>
                      </div>
                    )}
                    </>
                  )}
                </div>
              </div>
            )}
            
          {activeTab === 'orders' && (
            <div className="seller-section orders-section">
              <div className="orders-header">
                <h2>Buyurtmalar</h2>
                <p className="orders-subtitle">Sizning mahsulotlaringizdan bo'lgan buyurtmalar (PENDING, CONFIRMED)</p>
              </div>
              
              {errorMessage && (
                <div className="error-message" style={{ marginBottom: '20px' }}>
                  {errorMessage}
                </div>
              )}
              
              {ordersLoading ? (
                <div className="loading-state">
                  <p>{t('loading') || 'Yuklanmoqda...'}</p>
                </div>
              ) : orders.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">📦</div>
                  <p>Hozircha buyurtmalar yo'q</p>
                  <p className="empty-subtitle">
                    Sizning mahsulotlaringizdan buyurtma qilinganida, bu yerda ko'rinadi
                  </p>
                  <div className="info-box" style={{ marginTop: '20px', padding: '16px', background: '#f0f7ff', borderRadius: '8px', fontSize: '14px', color: '#666' }}>
                    <strong>Qanday ishlaydi:</strong>
                    <ul style={{ marginTop: '8px', paddingLeft: '20px' }}>
                      <li>1. Customer mahsulot tanlaydi va buyurtma yaratadi</li>
                      <li>2. Cash to'lovda: Buyurtma PENDING holatida ko'rinadi (admin tasdiqlashini kutadi)</li>
                      <li>3. Admin tasdiqlaganda: Buyurtma CONFIRMED holatiga o'tadi</li>
                      <li>4. Agar buyurtmada sizning mahsulotlaringiz bo'lsa, bu yerda ko'rinadi</li>
                    </ul>
                  </div>
                </div>
              ) : (
                <div className="orders-list">
                  {orders.map((order) => {
                    // Filter only seller's products from order items
                    const sellerOrderItems = order.orderItems?.filter(item => 
                      sellerProductIds.includes(item.productId)
                    ) || [];
                    
                    const sellerOrderTotal = sellerOrderItems.reduce((sum, item) => 
                      sum + (parseFloat(item.totalAmount) || 0), 0
                    );
                    
                    return (
                      <div key={order.id} className="order-card">
                        <div className="order-header">
                          <div className="order-number-section">
                            <span className="order-label">Buyurtma raqami:</span>
                            <span className="order-number">#{order.orderNumber}</span>
                          </div>
                          <div className={`order-status-badge ${order.status.toLowerCase()}`}>
                            {order.status === 'CONFIRMED' ? 'Tasdiqlangan' : 
                             order.status === 'PENDING' ? 'Kutilmoqda' : 
                             order.status === 'ORDERED' ? 'Buyurtma qilingan' :
                             order.status}
                          </div>
                        </div>
                        
                        <div className="order-info">
                          <div className="order-info-item">
                            <span className="info-label">Sana:</span>
                            <span className="info-value">
                              {new Date(order.createdAt).toLocaleDateString('uz-UZ', {
                                year: 'numeric',
                                month: 'long',
                                day: 'numeric',
                                hour: '2-digit',
                                minute: '2-digit'
                              })}
                            </span>
                          </div>
                          <div className="order-info-item">
                            <span className="info-label">To'lov holati:</span>
                            <span className={`payment-status ${order.paymentStatus?.toLowerCase()}`}>
                              {order.paymentStatus === 'PAID' ? 'To\'langan' : 
                               order.paymentStatus === 'PENDING' ? 'Kutilmoqda' : 
                               order.paymentStatus || 'Noma\'lum'}
                            </span>
                          </div>
                          <div className="order-info-item">
                            <span className="info-label">Yetkazib berish manzili:</span>
                            <span className="info-value">{order.shippingAddress || 'Kiritilmagan'}</span>
                          </div>
                        </div>
                        
                        <div className="order-items">
                          <h4 className="order-items-title">Sizning mahsulotlaringiz:</h4>
                          <div className="items-list">
                            {sellerOrderItems.map((item, index) => (
                              <div key={item.id || index} className="order-item">
                                <div className="item-info">
                                  <span className="item-name">{item.productName}</span>
                                  <span className="item-quantity">Miqdor: {item.quantity}</span>
                                </div>
                                <div className="item-price">
                                  ${parseFloat(item.totalAmount || 0).toFixed(2)}
                                </div>
                              </div>
                            ))}
                          </div>
                        </div>
                        
                        <div className="order-footer">
                          <div className="order-total">
                            <span className="total-label">Jami (sizning mahsulotlaringiz):</span>
                            <span className="total-amount">${sellerOrderTotal.toFixed(2)}</span>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {activeTab === 'analytics' && (
            <div className="seller-section">
              <h2>Analytics</h2>
              <div className="empty-state">
                <p>Analytics funksiyasi tez orada qo'shiladi</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SellerPage;



