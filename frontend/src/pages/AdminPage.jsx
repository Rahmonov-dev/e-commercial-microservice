import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { useNavigate } from 'react-router-dom';
import { productAPI, categoryAPI, productVariantAPI, warehouseAPIEndpoints } from '../services/api';
import { tokenService } from '../services/tokenService';
import './AdminPage.css';

const AdminPage = () => {
  const { user, userRole } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [bannerText, setBannerText] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [showAddProduct, setShowAddProduct] = useState(false);
  const [showBannerManagement, setShowBannerManagement] = useState(false);
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
  const [products, setProducts] = useState([]);
  const [productsLoading, setProductsLoading] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [showManageProducts, setShowManageProducts] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(2); // 2 ta card per page
  const [activeTab, setActiveTab] = useState('dashboard');

  // Redirect if not admin
  useEffect(() => {
    if (userRole !== 'ROLE_ADMIN' && userRole !== 'ADMIN') {
      navigate('/');
    }
  }, [userRole, navigate]);

  // Load current banner text
  useEffect(() => {
    const currentBanner = localStorage.getItem('bannerText') || 'Summer Sale For All Swim Suits And Free Express Delivery - OFF 50% Shop Now';
    setBannerText(currentBanner);
  }, []);

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

  // Load products
  useEffect(() => {
    const loadProducts = async () => {
      if (!showManageProducts) return;
      
      setProductsLoading(true);
      try {
        const tokenPayload = tokenService.getTokenPayload();
        const userId = tokenPayload?.userId;
        
        if (userId) {
          const response = await productAPI.getProductsBySellerId(userId, 0, 100);
          const productsList = response.content || response || [];
          setProducts(productsList);
        } else {
          setProducts([]);
        }
      } catch (error) {
        console.error('Error loading products:', error);
        setProducts([]);
      } finally {
        setProductsLoading(false);
      }
    };
    loadProducts();
  }, [showManageProducts]);

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

  const handleBannerUpdate = (e) => {
    e.preventDefault();
    if (!bannerText.trim()) {
      setErrorMessage('Banner text cannot be empty');
      setSuccessMessage('');
      return;
    }

    // Save to localStorage
    localStorage.setItem('bannerText', bannerText.trim());
    
    // Dispatch custom event to notify Navbar
    window.dispatchEvent(new Event('bannerTextUpdated'));
    
    setSuccessMessage('Banner text updated successfully!');
    setErrorMessage('');
    
    // Clear success message after 3 seconds
    setTimeout(() => {
      setSuccessMessage('');
    }, 3000);
  };

  const handleReset = () => {
    const defaultText = 'Summer Sale For All Swim Suits And Free Express Delivery - OFF 50% Shop Now';
    setBannerText(defaultText);
    localStorage.setItem('bannerText', defaultText);
    window.dispatchEvent(new Event('bannerTextUpdated'));
    setSuccessMessage('Banner text reset to default!');
    setErrorMessage('');
    setTimeout(() => {
      setSuccessMessage('');
    }, 3000);
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
        // Reload products if manage products is open
        if (showManageProducts) {
          const tokenPayload = tokenService.getTokenPayload();
          const userId = tokenPayload?.userId;
          if (userId) {
            const response = await productAPI.getProductsBySellerId(userId, 0, 100);
            const productsList = response.content || response || [];
            setProducts(productsList);
            setCurrentPage(0); // Reset to first page after update
          }
        }
      } else {
        // Create new product
        const createdProduct = await productAPI.createProduct(productData);
        setCreatedProductId(createdProduct.id);
        setSuccessMessage(t('productAdded'));
        setShowVariants(true);
        // Reload products if manage products is open
        if (showManageProducts) {
          const tokenPayload = tokenService.getTokenPayload();
          const userId = tokenPayload?.userId;
          if (userId) {
            const response = await productAPI.getProductsBySellerId(userId, 0, 100);
            const productsList = response.content || response || [];
            setProducts(productsList);
            setCurrentPage(0); // Reset to first page after add
          }
        }
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

  if (userRole !== 'ROLE_ADMIN' && userRole !== 'ADMIN') {
    return null;
  }

  return (
    <div className="admin-page">
      <Navbar showUserInfo={true} />
      
      <div className="admin-main-container">
        {/* Sidebar Navigation */}
        <aside className="admin-sidebar">
          <div className="sidebar-header">
            <div className="admin-avatar">
              <span>{user?.firstName?.charAt(0) || 'A'}</span>
            </div>
            <h3>{user?.firstName || 'Admin'}</h3>
            <p className="admin-role">Administrator</p>
          </div>
          
          <nav className="sidebar-nav">
            <button 
              className={`nav-item ${activeTab === 'dashboard' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('dashboard');
                setShowAddProduct(false);
                setShowManageProducts(false);
                setShowBannerManagement(false);
              }}
            >
              <span className="nav-icon">📊</span>
              <span className="nav-text">Dashboard</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'banner' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('banner');
                setShowBannerManagement(true);
                setShowAddProduct(false);
                setShowManageProducts(false);
              }}
            >
              <span className="nav-icon">🎨</span>
              <span className="nav-text">{t('bannerManagement')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'add-product' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('add-product');
                setShowAddProduct(true);
                setShowManageProducts(false);
                setShowBannerManagement(false);
                setEditingProduct(null);
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
                setShowBannerManagement(false);
              }}
            >
              <span className="nav-icon">📦</span>
              <span className="nav-text">{t('manageProducts')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'users' ? 'active' : ''}`}
              onClick={() => setActiveTab('users')}
            >
              <span className="nav-icon">👥</span>
              <span className="nav-text">{t('manageUsers')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'orders' ? 'active' : ''}`}
              onClick={() => setActiveTab('orders')}
            >
              <span className="nav-icon">🛒</span>
              <span className="nav-text">{t('viewOrders')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'categories' ? 'active' : ''}`}
              onClick={() => setActiveTab('categories')}
            >
              <span className="nav-icon">📁</span>
              <span className="nav-text">{t('manageCategories')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'sellers' ? 'active' : ''}`}
              onClick={() => setActiveTab('sellers')}
            >
              <span className="nav-icon">🏪</span>
              <span className="nav-text">{t('manageSellers')}</span>
            </button>
          </nav>
        </aside>

        {/* Main Content */}
        <div className="admin-container">
          {activeTab === 'dashboard' && (
            <>
              <div className="admin-header">
                <div>
                  <h1 className="admin-title">{t('welcome')}, {user?.firstName || 'Admin'}! 👋</h1>
                  <p className="admin-subtitle">Bu sizning admin panelingiz</p>
                </div>
              </div>

              <div className="admin-stats">
                <div className="stat-card stat-card-1">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>📦</span>
                    </div>
                    <div className="stat-info">
                      <h3>{t('totalProducts')}</h3>
                      <p className="stat-number">0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
                
                <div className="stat-card stat-card-2">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>👥</span>
                    </div>
                    <div className="stat-info">
                      <h3>{t('totalUsers')}</h3>
                      <p className="stat-number">0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
                
                <div className="stat-card stat-card-3">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>🛒</span>
                    </div>
                    <div className="stat-info">
                      <h3>{t('totalOrders')}</h3>
                      <p className="stat-number">0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
                
                <div className="stat-card stat-card-4">
                  <div className="stat-content">
                    <div className="stat-icon-modern">
                      <span>💰</span>
                    </div>
                    <div className="stat-info">
                      <h3>{t('revenue')}</h3>
                      <p className="stat-number">$0</p>
                    </div>
                  </div>
                  <div className="stat-wave"></div>
                </div>
              </div>

              <div className="quick-actions-grid">
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('banner');
                  setShowBannerManagement(true);
                  setShowAddProduct(false);
                  setShowManageProducts(false);
                }}>
                  <div className="action-icon">🎨</div>
                  <h3>Banner boshqarish</h3>
                  <p>Banner matnini o'zgartirish</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('add-product');
                  setShowAddProduct(true);
                  setShowManageProducts(false);
                  setShowBannerManagement(false);
                }}>
                  <div className="action-icon">➕</div>
                  <h3>Mahsulot qo'shish</h3>
                  <p>Yangi mahsulot yarating</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('manage');
                  setShowManageProducts(true);
                  setShowAddProduct(false);
                  setShowBannerManagement(false);
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
              </div>
            </>
          )}

          {activeTab === 'banner' && showBannerManagement && (
              <div className="admin-section">
                <div className="section-header">
                  <h2>{t('bannerManagement')}</h2>
                  <button
                    type="button"
                    className="close-btn"
                    onClick={() => setShowBannerManagement(false)}
                    aria-label="Close"
                  >
                    ×
                  </button>
                </div>
                <form onSubmit={handleBannerUpdate} className="banner-form">
                  {successMessage && (
                    <div className="success-message">{successMessage}</div>
                  )}
                  {errorMessage && (
                    <div className="error-message">{errorMessage}</div>
                  )}
                  <div className="form-group">
                    <label htmlFor="bannerText">{t('topBannerText')}</label>
                    <input
                      type="text"
                      id="bannerText"
                      value={bannerText}
                      onChange={(e) => {
                        setBannerText(e.target.value);
                        setErrorMessage('');
                        setSuccessMessage('');
                      }}
                      placeholder={t('topBannerText')}
                      className="banner-input"
                    />
                    <small className="form-hint">{t('bannerHint')}</small>
                  </div>
                  <div className="form-actions">
                    <button type="button" onClick={handleReset} className="btn btn-secondary">
                      {t('resetToDefault')}
                    </button>
                    <button type="submit" className="btn btn-primary">
                      {t('updateBanner')}
                    </button>
                  </div>
                </form>
              </div>
            )}

          {activeTab === 'add-product' && showAddProduct && (
              <div className="admin-section">
                <div className="section-header">
                  <h2>{editingProduct ? (t('editProduct') || 'Edit Product') : t('addProduct')}</h2>
                  <button
                    type="button"
                    className="close-btn"
                    onClick={() => {
                      setShowAddProduct(false);
                      setEditingProduct(null);
                      setProductForm({
                        name: '',
                        description: '',
                        price: '',
                        sku: '',
                        barcode: '',
                        categoryId: '',
                        imageUrl: null,
                      });
                      setErrorMessage('');
                      setSuccessMessage('');
                    }}
                    aria-label="Close"
                  >
                    ×
                  </button>
                </div>
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
              <div className="admin-section product-management-section">
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
                              setShowManageProducts(false);
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
                                  const tokenPayload = tokenService.getTokenPayload();
                                  const userId = tokenPayload?.userId;
                                  if (userId) {
                                    const response = await productAPI.getProductsBySellerId(userId, 0, 100);
                                    const productsList = response.content || response || [];
                                    setProducts(productsList);
                                    setCurrentPage(0); // Reset to first page after delete
                                  }
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

          {activeTab === 'users' && (
            <div className="admin-section">
              <h2>{t('manageUsers')}</h2>
              <div className="empty-state">
                <p>Foydalanuvchilarni boshqarish funksiyasi tez orada qo'shiladi</p>
              </div>
            </div>
          )}

          {activeTab === 'orders' && (
            <div className="admin-section">
              <h2>{t('viewOrders')}</h2>
              <div className="empty-state">
                <p>Buyurtmalarni ko'rish funksiyasi tez orada qo'shiladi</p>
              </div>
            </div>
          )}

          {activeTab === 'categories' && (
            <div className="admin-section">
              <h2>{t('manageCategories')}</h2>
              <div className="empty-state">
                <p>Kategoriyalarni boshqarish funksiyasi tez orada qo'shiladi</p>
              </div>
            </div>
          )}

          {activeTab === 'sellers' && (
            <div className="admin-section">
              <h2>{t('manageSellers')}</h2>
              <div className="empty-state">
                <p>Sotuvchilarni boshqarish funksiyasi tez orada qo'shiladi</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminPage;
