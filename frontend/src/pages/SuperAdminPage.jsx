import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { useNavigate } from 'react-router-dom';
import { productAPI, categoryAPI, roleAPI, permissionAPI, productVariantAPI, warehouseAPIEndpoints, userAPI } from '../services/api';
import './SuperAdminPage.css';

const SuperAdminPage = () => {
  const { user, userRole } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [bannerText, setBannerText] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [showAddProduct, setShowAddProduct] = useState(false);
  const [showBannerManagement, setShowBannerManagement] = useState(false);
  const [showAddRole, setShowAddRole] = useState(false);
  const [showAddPermission, setShowAddPermission] = useState(false);
  const [showAddCategory, setShowAddCategory] = useState(false);
  const [showAddWarehouse, setShowAddWarehouse] = useState(false);
  const [categories, setCategories] = useState([]);
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);
  
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
  
  const [roleForm, setRoleForm] = useState({
    name: '',
    description: '',
    permissionIds: [],
  });
  
  const [permissionForm, setPermissionForm] = useState({
    name: '',
    description: '',
    resource: '',
    action: '',
  });
  
  const [categoryForm, setCategoryForm] = useState({
    name: '',
    description: '',
    parentId: '',
  });
  
  const [warehouseForm, setWarehouseForm] = useState({
    name: '',
    address: '',
    city: '',
    state: '',
    country: '',
    postalCode: '',
    phoneNumber: '',
    email: '',
    capacity: '',
  });
  
  const [activeTab, setActiveTab] = useState('dashboard');
  
  // Users management state
  const [users, setUsers] = useState([]);
  const [sellers, setSellers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [sellersLoading, setSellersLoading] = useState(false);
  const [currentUsersPage, setCurrentUsersPage] = useState(0);
  const [currentSellersPage, setCurrentSellersPage] = useState(0);
  const [usersPageSize] = useState(10);
  const [sellersPageSize] = useState(10);
  const [usersView, setUsersView] = useState('all'); // 'all' or 'sellers'
  
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
  
  const [productLoading, setProductLoading] = useState(false);
  const [roleLoading, setRoleLoading] = useState(false);
  const [permissionLoading, setPermissionLoading] = useState(false);
  const [categoryLoading, setCategoryLoading] = useState(false);
  const [warehouseLoading, setWarehouseLoading] = useState(false);
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

  // Redirect if not super admin
  useEffect(() => {
    if (userRole !== 'ROLE_SUPER_ADMIN' && userRole !== 'SUPER_ADMIN') {
      navigate('/');
    }
  }, [userRole, navigate]);

  // Load current banner text
  useEffect(() => {
    const currentBanner = localStorage.getItem('bannerText') || 'Summer Sale For All Swim Suits And Free Express Delivery - OFF 50% Shop Now';
    setBannerText(currentBanner);
  }, []);

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

  // Load roles
  useEffect(() => {
    const loadRoles = async () => {
      try {
        const response = await roleAPI.getAllRoles();
        setRoles(response || []);
      } catch (error) {
        console.error('Error loading roles:', error);
      }
    };
    loadRoles();
  }, []);

  // Load permissions
  useEffect(() => {
    const loadPermissions = async () => {
      try {
        const response = await permissionAPI.getAllPermissions();
        setPermissions(response || []);
      } catch (error) {
        console.error('Error loading permissions:', error);
      }
    };
    loadPermissions();
  }, []);

  // Load users when users tab is active
  useEffect(() => {
    const loadUsers = async () => {
      if (activeTab !== 'users') {
        return;
      }

      setUsersLoading(true);
      try {
        const allUsers = await userAPI.getAllUsers();
        setUsers(allUsers || []);
      } catch (error) {
        console.error('Error loading users:', error);
        setErrorMessage('Foydalanuvchilarni yuklashda xatolik yuz berdi.');
        setUsers([]);
      } finally {
        setUsersLoading(false);
      }
    };

    loadUsers();
  }, [activeTab]);

  // Load sellers when users tab is active and view is sellers
  useEffect(() => {
    const loadSellers = async () => {
      if (activeTab !== 'users' || usersView !== 'sellers') {
        return;
      }

      setSellersLoading(true);
      try {
        const sellersList = await userAPI.getAllSellers();
        setSellers(sellersList || []);
      } catch (error) {
        console.error('Error loading sellers:', error);
        setErrorMessage('Sotuvchilarni yuklashda xatolik yuz berdi.');
        setSellers([]);
      } finally {
        setSellersLoading(false);
      }
    };

    loadSellers();
  }, [activeTab, usersView]);

  const handleBannerUpdate = (e) => {
    e.preventDefault();
    if (!bannerText.trim()) {
      setErrorMessage('Banner text cannot be empty');
      setSuccessMessage('');
      return;
    }

    localStorage.setItem('bannerText', bannerText.trim());
    window.dispatchEvent(new Event('bannerTextUpdated'));
    
    setSuccessMessage(t('bannerUpdated'));
    setErrorMessage('');
    
    setTimeout(() => {
      setSuccessMessage('');
    }, 3000);
  };

  const handleReset = () => {
    const defaultText = 'Summer Sale For All Swim Suits And Free Express Delivery - OFF 50% Shop Now';
    setBannerText(defaultText);
    localStorage.setItem('bannerText', defaultText);
    window.dispatchEvent(new Event('bannerTextUpdated'));
    setSuccessMessage(t('bannerReset'));
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

      const createdProduct = await productAPI.createProduct(productData);
      
      setCreatedProductId(createdProduct.id);
      setSuccessMessage(t('productAdded'));
      setShowVariants(true);
      
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

  const handleRoleFormChange = (e) => {
    const { name, value } = e.target;
    setRoleForm({ ...roleForm, [name]: value });
  };

  const handlePermissionCheckboxChange = (permissionId) => {
    const currentIds = roleForm.permissionIds || [];
    if (currentIds.includes(permissionId)) {
      setRoleForm({
        ...roleForm,
        permissionIds: currentIds.filter(id => id !== permissionId),
      });
    } else {
      setRoleForm({
        ...roleForm,
        permissionIds: [...currentIds, permissionId],
      });
    }
  };

  const handleAddRole = async (e) => {
    e.preventDefault();
    setRoleLoading(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const roleData = {
        name: roleForm.name,
        description: roleForm.description,
        permissionIds: roleForm.permissionIds,
      };

      await roleAPI.createRole(roleData);
      
      setSuccessMessage(t('roleAdded'));
      setRoleForm({
        name: '',
        description: '',
        permissionIds: [],
      });
      setShowAddRole(false);
      
      // Reload roles
      const response = await roleAPI.getAllRoles();
      setRoles(response || []);
      
      setTimeout(() => {
        setSuccessMessage('');
      }, 3000);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || t('roleAddError'));
    } finally {
      setRoleLoading(false);
    }
  };

  const handlePermissionFormChange = (e) => {
    const { name, value } = e.target;
    setPermissionForm({ ...permissionForm, [name]: value });
  };

  const handleAddPermission = async (e) => {
    e.preventDefault();
    setPermissionLoading(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const permissionData = {
        name: permissionForm.name,
        description: permissionForm.description,
        resource: permissionForm.resource,
        action: permissionForm.action,
      };

      await permissionAPI.createPermission(permissionData);
      
      setSuccessMessage(t('permissionAdded'));
      setPermissionForm({
        name: '',
        description: '',
        resource: '',
        action: '',
      });
      setShowAddPermission(false);
      
      // Reload permissions
      const response = await permissionAPI.getAllPermissions();
      setPermissions(response || []);
      
      setTimeout(() => {
        setSuccessMessage('');
      }, 3000);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || t('permissionAddError'));
    } finally {
      setPermissionLoading(false);
    }
  };

  const handleCategoryFormChange = (e) => {
    const { name, value } = e.target;
    setCategoryForm({ ...categoryForm, [name]: value });
  };

  const handleAddCategory = async (e) => {
    e.preventDefault();
    setCategoryLoading(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const categoryData = {
        name: categoryForm.name,
        description: categoryForm.description,
        parentId: categoryForm.parentId ? parseInt(categoryForm.parentId) : null,
      };

      await categoryAPI.createCategory(categoryData);
      
      setSuccessMessage(t('categoryAdded'));
      setCategoryForm({
        name: '',
        description: '',
        parentId: '',
      });
      setShowAddCategory(false);
      
      // Reload categories
      const response = await categoryAPI.getAllCategories(0, 100);
      const categoriesList = response.content || [];
      setCategories(categoriesList);
      setAllCategoriesFlat(flattenCategories(categoriesList));
      
      // Dispatch event to refresh CategoryTree in other components
      window.dispatchEvent(new Event('categoryUpdated'));
      
      setTimeout(() => {
        setSuccessMessage('');
      }, 3000);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || t('categoryAddError'));
    } finally {
      setCategoryLoading(false);
    }
  };

  const handleWarehouseFormChange = (e) => {
    const { name, value } = e.target;
    setWarehouseForm({ ...warehouseForm, [name]: value });
  };

  const handleAddWarehouse = async (e) => {
    e.preventDefault();
    setWarehouseLoading(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      const warehouseData = {
        name: warehouseForm.name,
        address: warehouseForm.address,
        city: warehouseForm.city,
        state: warehouseForm.state || null,
        country: warehouseForm.country,
        postalCode: warehouseForm.postalCode || null,
        phoneNumber: warehouseForm.phoneNumber || null,
        email: warehouseForm.email || null,
        capacity: parseInt(warehouseForm.capacity),
      };

      await warehouseAPIEndpoints.createWarehouse(warehouseData);
      
      setSuccessMessage('Warehouse created successfully!');
      setWarehouseForm({
        name: '',
        address: '',
        city: '',
        state: '',
        country: '',
        postalCode: '',
        phoneNumber: '',
        email: '',
        capacity: '',
      });
      setShowAddWarehouse(false);
      
      setTimeout(() => {
        setSuccessMessage('');
      }, 3000);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || 'Failed to create warehouse');
    } finally {
      setWarehouseLoading(false);
    }
  };

  if (userRole !== 'ROLE_SUPER_ADMIN' && userRole !== 'SUPER_ADMIN') {
    return null;
  }

  return (
    <div className="super-admin-page">
      <Navbar showUserInfo={true} />
      
      <div className="super-admin-main-container">
        {/* Sidebar Navigation */}
        <aside className="super-admin-sidebar">
          <div className="sidebar-header">
            <div className="super-admin-avatar">
              <span>{user?.firstName?.charAt(0) || 'S'}</span>
            </div>
            <h3>{user?.firstName || 'Super Admin'}</h3>
            <p className="super-admin-role">Super Administrator</p>
          </div>
          
          <nav className="sidebar-nav">
            <button 
              className={`nav-item ${activeTab === 'dashboard' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('dashboard');
                setShowAddProduct(false);
                setShowBannerManagement(false);
                setShowAddRole(false);
                setShowAddPermission(false);
                setShowAddCategory(false);
                setShowAddWarehouse(false);
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
                setShowAddRole(false);
                setShowAddPermission(false);
                setShowAddCategory(false);
                setShowAddWarehouse(false);
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
                setShowBannerManagement(false);
                setShowAddRole(false);
                setShowAddPermission(false);
                setShowAddCategory(false);
                setShowAddWarehouse(false);
              }}
            >
              <span className="nav-icon">➕</span>
              <span className="nav-text">{t('addNewProduct')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'add-role' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('add-role');
                setShowAddRole(true);
                setShowAddProduct(false);
                setShowBannerManagement(false);
                setShowAddPermission(false);
                setShowAddCategory(false);
                setShowAddWarehouse(false);
              }}
            >
              <span className="nav-icon">👤</span>
              <span className="nav-text">{t('addNewRole')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'add-permission' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('add-permission');
                setShowAddPermission(true);
                setShowAddProduct(false);
                setShowBannerManagement(false);
                setShowAddRole(false);
                setShowAddCategory(false);
                setShowAddWarehouse(false);
              }}
            >
              <span className="nav-icon">🔐</span>
              <span className="nav-text">{t('addNewPermission')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'add-category' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('add-category');
                setShowAddCategory(true);
                setShowAddProduct(false);
                setShowBannerManagement(false);
                setShowAddRole(false);
                setShowAddPermission(false);
                setShowAddWarehouse(false);
              }}
            >
              <span className="nav-icon">📁</span>
              <span className="nav-text">{t('addNewCategory')}</span>
            </button>
            
            <button 
              className={`nav-item ${activeTab === 'add-warehouse' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('add-warehouse');
                setShowAddWarehouse(true);
                setShowAddProduct(false);
                setShowBannerManagement(false);
                setShowAddRole(false);
                setShowAddPermission(false);
                setShowAddCategory(false);
              }}
            >
              <span className="nav-icon">🏭</span>
              <span className="nav-text">Add Warehouse</span>
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
          </nav>
        </aside>

        {/* Main Content */}
        <div className="super-admin-container">
          {activeTab === 'dashboard' && (
            <>
              <div className="super-admin-header">
                <div>
                  <h1 className="super-admin-title">{t('welcome')}, {user?.firstName || 'Super Admin'}! 👋</h1>
                  <p className="super-admin-subtitle">Bu sizning super admin panelingiz</p>
                </div>
              </div>

              <div className="super-admin-stats">
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
                }}>
                  <div className="action-icon">🎨</div>
                  <h3>Banner boshqarish</h3>
                  <p>Banner matnini o'zgartirish</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('add-product');
                  setShowAddProduct(true);
                }}>
                  <div className="action-icon">➕</div>
                  <h3>Mahsulot qo'shish</h3>
                  <p>Yangi mahsulot yarating</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('add-role');
                  setShowAddRole(true);
                }}>
                  <div className="action-icon">👤</div>
                  <h3>Role qo'shish</h3>
                  <p>Yangi role yarating</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('add-permission');
                  setShowAddPermission(true);
                }}>
                  <div className="action-icon">🔐</div>
                  <h3>Permission qo'shish</h3>
                  <p>Yangi permission yarating</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('add-category');
                  setShowAddCategory(true);
                }}>
                  <div className="action-icon">📁</div>
                  <h3>Kategoriya qo'shish</h3>
                  <p>Yangi kategoriya yarating</p>
                </div>
                
                <div className="quick-action-card" onClick={() => {
                  setActiveTab('add-warehouse');
                  setShowAddWarehouse(true);
                }}>
                  <div className="action-icon">🏭</div>
                  <h3>Warehouse qo'shish</h3>
                  <p>Yangi ombor yarating</p>
                </div>
              </div>
            </>
          )}

          {activeTab === 'banner' && showBannerManagement && (
              <div className="super-admin-section">
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
              <div className="super-admin-section">
                <div className="section-header">
                  <h2>{t('addProduct')}</h2>
                  <button
                    type="button"
                    className="close-btn"
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

          {activeTab === 'add-role' && showAddRole && (
              <div className="super-admin-section">
                <div className="section-header">
                  <h2>{t('addRole')}</h2>
                  <button
                    type="button"
                    className="close-btn"
                    onClick={() => {
                      setShowAddRole(false);
                      setRoleForm({
                        name: '',
                        description: '',
                        permissionIds: [],
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
                <form onSubmit={handleAddRole} className="role-form">
                  <div className="form-group">
                    <label htmlFor="roleName">{t('roleName')} *</label>
                    <input
                      type="text"
                      id="roleName"
                      name="name"
                      value={roleForm.name}
                      onChange={handleRoleFormChange}
                      required
                      placeholder={t('roleName')}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="roleDescription">{t('roleDescription')}</label>
                    <textarea
                      id="roleDescription"
                      name="description"
                      value={roleForm.description}
                      onChange={handleRoleFormChange}
                      rows="3"
                      placeholder={t('roleDescription')}
                    />
                  </div>
                  <div className="form-group">
                    <label>{t('permissions')}</label>
                    <div className="permissions-checkbox-list">
                      {permissions.map((permission) => (
                        <label key={permission.id} className="checkbox-label">
                          <input
                            type="checkbox"
                            checked={roleForm.permissionIds?.includes(permission.id) || false}
                            onChange={() => handlePermissionCheckboxChange(permission.id)}
                          />
                          <span>{permission.name}</span>
                          {permission.description && (
                            <small className="permission-desc">{permission.description}</small>
                          )}
                        </label>
                      ))}
                    </div>
                  </div>
                  <div className="form-actions">
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => {
                        setShowAddRole(false);
                        setRoleForm({
                          name: '',
                          description: '',
                          permissionIds: [],
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
                      disabled={roleLoading}
                    >
                      {roleLoading ? t('creating') : t('submit')}
                    </button>
                  </div>
                </form>
              </div>
            )}

          {activeTab === 'add-permission' && showAddPermission && (
              <div className="super-admin-section">
                <div className="section-header">
                  <h2>{t('addPermission')}</h2>
                  <button
                    type="button"
                    className="close-btn"
                    onClick={() => {
                      setShowAddPermission(false);
                      setPermissionForm({
                        name: '',
                        description: '',
                        resource: '',
                        action: '',
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
                <form onSubmit={handleAddPermission} className="permission-form">
                  <div className="form-group">
                    <label htmlFor="permissionName">{t('permissionName')} *</label>
                    <input
                      type="text"
                      id="permissionName"
                      name="name"
                      value={permissionForm.name}
                      onChange={handlePermissionFormChange}
                      required
                      placeholder={t('permissionName')}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="permissionDescription">{t('permissionDescription')}</label>
                    <textarea
                      id="permissionDescription"
                      name="description"
                      value={permissionForm.description}
                      onChange={handlePermissionFormChange}
                      rows="3"
                      placeholder={t('permissionDescription')}
                    />
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="permissionResource">{t('permissionResource')}</label>
                      <input
                        type="text"
                        id="permissionResource"
                        name="resource"
                        value={permissionForm.resource}
                        onChange={handlePermissionFormChange}
                        placeholder="USER, PRODUCT, ORDER, etc."
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="permissionAction">{t('permissionAction')}</label>
                      <input
                        type="text"
                        id="permissionAction"
                        name="action"
                        value={permissionForm.action}
                        onChange={handlePermissionFormChange}
                        placeholder="CREATE, READ, UPDATE, DELETE"
                      />
                    </div>
                  </div>
                  <div className="form-actions">
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => {
                        setShowAddPermission(false);
                        setPermissionForm({
                          name: '',
                          description: '',
                          resource: '',
                          action: '',
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
                      disabled={permissionLoading}
                    >
                      {permissionLoading ? t('creating') : t('submit')}
                    </button>
                  </div>
                </form>
              </div>
            )}

          {activeTab === 'add-warehouse' && showAddWarehouse && (
              <div className="super-admin-section">
                <div className="section-header">
                  <h2>Add Warehouse</h2>
                  <button
                    type="button"
                    className="close-btn"
                    onClick={() => {
                      setShowAddWarehouse(false);
                      setWarehouseForm({
                        name: '',
                        address: '',
                        city: '',
                        state: '',
                        country: '',
                        postalCode: '',
                        phoneNumber: '',
                        email: '',
                        capacity: '',
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
                <form onSubmit={handleAddWarehouse} className="warehouse-form">
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="warehouseName">Warehouse Name *</label>
                      <input
                        type="text"
                        id="warehouseName"
                        name="name"
                        value={warehouseForm.name}
                        onChange={handleWarehouseFormChange}
                        required
                        placeholder="Warehouse Name"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="warehouseCapacity">Capacity *</label>
                      <input
                        type="number"
                        id="warehouseCapacity"
                        name="capacity"
                        value={warehouseForm.capacity}
                        onChange={handleWarehouseFormChange}
                        required
                        min="1"
                        placeholder="1000"
                      />
                    </div>
                  </div>
                  <div className="form-group">
                    <label htmlFor="warehouseAddress">Address *</label>
                    <input
                      type="text"
                      id="warehouseAddress"
                      name="address"
                      value={warehouseForm.address}
                      onChange={handleWarehouseFormChange}
                      required
                      placeholder="Street Address"
                    />
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="warehouseCity">City *</label>
                      <input
                        type="text"
                        id="warehouseCity"
                        name="city"
                        value={warehouseForm.city}
                        onChange={handleWarehouseFormChange}
                        required
                        placeholder="City"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="warehouseState">State/Province</label>
                      <input
                        type="text"
                        id="warehouseState"
                        name="state"
                        value={warehouseForm.state}
                        onChange={handleWarehouseFormChange}
                        placeholder="State/Province"
                      />
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="warehouseCountry">Country *</label>
                      <input
                        type="text"
                        id="warehouseCountry"
                        name="country"
                        value={warehouseForm.country}
                        onChange={handleWarehouseFormChange}
                        required
                        placeholder="Country"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="warehousePostalCode">Postal Code</label>
                      <input
                        type="text"
                        id="warehousePostalCode"
                        name="postalCode"
                        value={warehouseForm.postalCode}
                        onChange={handleWarehouseFormChange}
                        placeholder="Postal Code"
                      />
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="warehousePhone">Phone Number</label>
                      <input
                        type="tel"
                        id="warehousePhone"
                        name="phoneNumber"
                        value={warehouseForm.phoneNumber}
                        onChange={handleWarehouseFormChange}
                        placeholder="+1234567890"
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="warehouseEmail">Email</label>
                      <input
                        type="email"
                        id="warehouseEmail"
                        name="email"
                        value={warehouseForm.email}
                        onChange={handleWarehouseFormChange}
                        placeholder="warehouse@example.com"
                      />
                    </div>
                  </div>
                  <div className="form-actions">
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => {
                        setShowAddWarehouse(false);
                        setWarehouseForm({
                          name: '',
                          address: '',
                          city: '',
                          state: '',
                          country: '',
                          postalCode: '',
                          phoneNumber: '',
                          email: '',
                          capacity: '',
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
                      disabled={warehouseLoading}
                    >
                      {warehouseLoading ? t('creating') : 'Create Warehouse'}
                    </button>
                  </div>
                </form>
              </div>
            )}

          {activeTab === 'add-category' && showAddCategory && (
              <div className="super-admin-section">
                <div className="section-header">
                  <h2>{t('addCategory')}</h2>
                  <button
                    type="button"
                    className="close-btn"
                    onClick={() => {
                      setShowAddCategory(false);
                      setCategoryForm({
                        name: '',
                        description: '',
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
                <form onSubmit={handleAddCategory} className="category-form">
                  <div className="form-group">
                    <label htmlFor="categoryName">{t('categoryName')} *</label>
                    <input
                      type="text"
                      id="categoryName"
                      name="name"
                      value={categoryForm.name}
                      onChange={handleCategoryFormChange}
                      required
                      placeholder={t('categoryName')}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="categoryDescription">{t('categoryDescription')}</label>
                    <textarea
                      id="categoryDescription"
                      name="description"
                      value={categoryForm.description}
                      onChange={handleCategoryFormChange}
                      rows="3"
                      placeholder={t('categoryDescription')}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="categoryParent">{t('parentCategory')} ({t('optional')})</label>
                    <select
                      id="categoryParent"
                      name="parentId"
                      value={categoryForm.parentId}
                      onChange={handleCategoryFormChange}
                    >
                      <option value="">{t('noParentCategory')} (Root Category)</option>
                      {allCategoriesFlat.map((cat) => (
                        <option key={cat.id} value={cat.id}>
                          {cat.displayName}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="form-actions">
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => {
                        setShowAddCategory(false);
                        setCategoryForm({
                          name: '',
                          description: '',
                          parentId: '',
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
                      disabled={categoryLoading}
                    >
                      {categoryLoading ? t('creating') : t('submit')}
                    </button>
                  </div>
                </form>
              </div>
            )}

          {activeTab === 'users' && (
            <div className="super-admin-section">
              <div className="section-header">
                <h2>{t('manageUsers')}</h2>
                <div style={{ display: 'flex', gap: '12px' }}>
                  <button
                    className={`btn ${usersView === 'all' ? 'btn-primary' : 'btn-secondary'}`}
                    onClick={() => setUsersView('all')}
                    style={{ padding: '8px 16px', fontSize: '14px' }}
                  >
                    Barcha foydalanuvchilar
                  </button>
                  <button
                    className={`btn ${usersView === 'sellers' ? 'btn-primary' : 'btn-secondary'}`}
                    onClick={() => setUsersView('sellers')}
                    style={{ padding: '8px 16px', fontSize: '14px' }}
                  >
                    Sotuvchilar
                  </button>
                </div>
              </div>

              {errorMessage && (
                <div className="error-message">{errorMessage}</div>
              )}

              {usersView === 'all' ? (
                <>
                  {usersLoading ? (
                    <div className="loading-state">
                      <p>Yuklanmoqda...</p>
                    </div>
                  ) : users.length === 0 ? (
                    <div className="empty-state">
                      <p>Foydalanuvchilar topilmadi</p>
                    </div>
                  ) : (
                    <>
                      <div className="users-list">
                        {users
                          .slice(currentUsersPage * usersPageSize, (currentUsersPage + 1) * usersPageSize)
                          .map((user) => (
                            <div key={user.id} className="user-card">
                              <div className="user-avatar">
                                <span>{user.firstName?.charAt(0) || 'U'}</span>
                              </div>
                              <div className="user-info">
                                <h3>{user.firstName} {user.lastName}</h3>
                                <p className="user-email">{user.email || user.phoneNumber}</p>
                                <div className="user-meta">
                                  <span className="user-role">{user.roleName || 'CUSTOMER'}</span>
                                  <span className={`user-status ${user.status?.toLowerCase() || 'active'}`}>
                                    {user.status || 'ACTIVE'}
                                  </span>
                                </div>
                              </div>
                            </div>
                          ))}
                      </div>
                      {users.length > usersPageSize && (
                        <div className="pagination">
                          <button
                            className="pagination-btn"
                            onClick={() => setCurrentUsersPage(currentUsersPage - 1)}
                            disabled={currentUsersPage === 0}
                          >
                            ← Oldingi
                          </button>
                          <span className="pagination-info">
                            Sahifa {currentUsersPage + 1} / {Math.ceil(users.length / usersPageSize)} ({users.length} jami)
                          </span>
                          <button
                            className="pagination-btn"
                            onClick={() => setCurrentUsersPage(currentUsersPage + 1)}
                            disabled={currentUsersPage >= Math.ceil(users.length / usersPageSize) - 1}
                          >
                            Keyingi →
                          </button>
                        </div>
                      )}
                    </>
                  )}
                </>
              ) : (
                <>
                  {sellersLoading ? (
                    <div className="loading-state">
                      <p>Yuklanmoqda...</p>
                    </div>
                  ) : sellers.length === 0 ? (
                    <div className="empty-state">
                      <p>Sotuvchilar topilmadi</p>
                    </div>
                  ) : (
                    <>
                      <div className="users-list">
                        {sellers
                          .slice(currentSellersPage * sellersPageSize, (currentSellersPage + 1) * sellersPageSize)
                          .map((seller) => (
                            <div key={seller.id} className="user-card seller-card">
                              <div className="user-avatar seller-avatar">
                                <span>{seller.firstName?.charAt(0) || 'S'}</span>
                              </div>
                              <div className="user-info">
                                <h3>{seller.firstName} {seller.lastName}</h3>
                                <p className="user-email">{seller.email || seller.phoneNumber}</p>
                                <div className="user-meta">
                                  <span className="user-role seller-role">SELLER</span>
                                  <span className={`user-status ${seller.status?.toLowerCase() || 'active'}`}>
                                    {seller.status || 'ACTIVE'}
                                  </span>
                                </div>
                              </div>
                            </div>
                          ))}
                      </div>
                      {sellers.length > sellersPageSize && (
                        <div className="pagination">
                          <button
                            className="pagination-btn"
                            onClick={() => setCurrentSellersPage(currentSellersPage - 1)}
                            disabled={currentSellersPage === 0}
                          >
                            ← Oldingi
                          </button>
                          <span className="pagination-info">
                            Sahifa {currentSellersPage + 1} / {Math.ceil(sellers.length / sellersPageSize)} ({sellers.length} jami)
                          </span>
                          <button
                            className="pagination-btn"
                            onClick={() => setCurrentSellersPage(currentSellersPage + 1)}
                            disabled={currentSellersPage >= Math.ceil(sellers.length / sellersPageSize) - 1}
                          >
                            Keyingi →
                          </button>
                        </div>
                      )}
                    </>
                  )}
                </>
              )}
            </div>
          )}

          {activeTab === 'orders' && (
            <div className="super-admin-section">
              <h2>{t('viewOrders')}</h2>
              <div className="empty-state">
                <p>Buyurtmalarni ko'rish funksiyasi tez orada qo'shiladi</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SuperAdminPage;

