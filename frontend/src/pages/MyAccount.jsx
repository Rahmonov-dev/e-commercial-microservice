import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { sellerAPI, orderAPI, wishlistAPI } from '../services/api';
import { tokenService } from '../services/tokenService';
import './MyAccount.css';

const MyAccount = () => {
  const { user, logout } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('profile');
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    phoneNumber: user?.phoneNumber || '',
    address: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');
  const [sellerFormData, setSellerFormData] = useState({
    email: user?.email || '',
    phoneNumber: user?.phoneNumber || '',
    businessName: '',
    address: '',
  });
  const [orders, setOrders] = useState([]);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [wishlistItems, setWishlistItems] = useState([]);
  const [wishlistLoading, setWishlistLoading] = useState(false);

  // Update seller form when user data changes
  useEffect(() => {
    if (user) {
      setSellerFormData(prev => ({
        ...prev,
        email: user.email || prev.email,
        phoneNumber: user.phoneNumber || prev.phoneNumber,
      }));
    }
  }, [user]);

  // Load orders when orders tab is active
  useEffect(() => {
    const loadOrders = async () => {
      if (activeTab !== 'orders') return;

      const tokenPayload = tokenService.getTokenPayload();
      const userId = tokenPayload?.userId;
      
      if (!userId) return;

      setOrdersLoading(true);
      try {
        const response = await orderAPI.getOrdersByUserId(userId, 0, 50);
        // Handle Page response
        if (response.content) {
          setOrders(response.content);
        } else if (Array.isArray(response)) {
          setOrders(response);
        } else {
          setOrders([]);
        }
      } catch (error) {
        console.error('Error loading orders:', error);
        setOrders([]);
      } finally {
        setOrdersLoading(false);
      }
    };

    loadOrders();
  }, [activeTab]);

  // Load wishlist when wishlist tab is active
  useEffect(() => {
    const loadWishlist = async () => {
      if (activeTab !== 'wishlist') return;

      const tokenPayload = tokenService.getTokenPayload();
      const userId = tokenPayload?.userId;
      
      if (!userId) return;

      setWishlistLoading(true);
      try {
        const wishlist = await wishlistAPI.getWishlistByUserId(userId);
        if (wishlist && wishlist.wishlistItems && wishlist.wishlistItems.length > 0) {
          setWishlistItems(wishlist.wishlistItems);
        } else {
          setWishlistItems([]);
        }
      } catch (error) {
        // If wishlist doesn't exist, it's okay - just show empty
        if (error.response?.status === 404 || error.response?.status === 500) {
          setWishlistItems([]);
        } else {
          console.error('Error loading wishlist:', error);
          setWishlistItems([]);
        }
      } finally {
        setWishlistLoading(false);
      }
    };

    loadWishlist();
  }, [activeTab]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
    setSuccess(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);
    setLoading(true);

    // Validate passwords if changing
    if (formData.newPassword) {
      if (formData.newPassword !== formData.confirmPassword) {
        setError('New passwords do not match');
        setLoading(false);
        return;
      }
      if (formData.newPassword.length < 6) {
        setError('Password must be at least 6 characters');
        setLoading(false);
        return;
      }
    }

    // Simulate API call
    setTimeout(() => {
      setLoading(false);
      setSuccess(true);
      setIsEditing(false);
      setFormData({
        ...formData,
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
      // Clear success message after 3 seconds
      setTimeout(() => {
        setSuccess(false);
      }, 3000);
    }, 1000);
  };

  const handleCancel = () => {
    setFormData({
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      phoneNumber: user?.phoneNumber || '',
      address: '',
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    });
    setError('');
    setSuccess(false);
    setIsEditing(false);
  };

  const handleEdit = () => {
    setIsEditing(true);
    setFormData({
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      phoneNumber: user?.phoneNumber || '',
      address: '',
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    });
    setError('');
    setSuccess(false);
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const handleSellerFormChange = (e) => {
    setSellerFormData({
      ...sellerFormData,
      [e.target.name]: e.target.value,
    });
    setError('');
    setSuccess(false);
  };

  const handleSellerSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);
    setLoading(true);

    try {
      // Validate required fields
      if (!sellerFormData.email || !sellerFormData.phoneNumber) {
        setError(t('fillRequiredFields'));
        setLoading(false);
        return;
      }

      await sellerAPI.registerSeller(sellerFormData);
      setSuccess(true);
      
      // Show success message for 2 seconds, then logout and redirect
      setTimeout(async () => {
        // Clear tokens and logout
        await logout();
        // Redirect to login page
        navigate('/login', { 
          state: { 
            message: t('sellerRegistrationSuccessLogin'),
            sellerRegistered: true 
          } 
        });
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || err.message || t('sellerRegistrationError'));
      setLoading(false);
    }
  };

  return (
    <div className="my-account-page">
      <Navbar showUserInfo={true} />

      {/* Breadcrumbs */}
      <div className="breadcrumbs">
        <div className="breadcrumbs-container">
          <Link to="/">{t('home')}</Link>
          <span>/</span>
          <span>{t('myAccount')}</span>
        </div>
        <div className="welcome-message">
          {t('welcome')}! {user?.firstName || user?.phoneNumber || 'User'}
        </div>
      </div>

      <div className="account-container">
        <div className="account-sidebar">
          <div className="sidebar-section">
            <h3 className="sidebar-title">{t('myAccount')}</h3>
            <ul className="sidebar-menu">
              <li>
                <button
                  className={activeTab === 'profile' ? 'active' : ''}
                  onClick={() => setActiveTab('profile')}
                >
                  {t('myProfile')}
                </button>
              </li>
              <li>
                <button
                  className={activeTab === 'address' ? 'active' : ''}
                  onClick={() => setActiveTab('address')}
                >
                  {t('addressBook')}
                </button>
              </li>
              <li>
                <button
                  className={activeTab === 'payment' ? 'active' : ''}
                  onClick={() => setActiveTab('payment')}
                >
                  {t('myPaymentOptions')}
                </button>
              </li>
            </ul>
          </div>

          <div className="sidebar-section">
            <h3 className="sidebar-title">{t('myOrders') || 'My Orders'}</h3>
            <ul className="sidebar-menu">
              <li>
                <button
                  className={activeTab === 'orders' ? 'active' : ''}
                  onClick={() => setActiveTab('orders')}
                >
                  {t('orderHistory') || 'Order History'}
                </button>
              </li>
              <li>
                <button
                  className={activeTab === 'returns' ? 'active' : ''}
                  onClick={() => setActiveTab('returns')}
                >
                  {t('myReturns')}
                </button>
              </li>
              <li>
                <button
                  className={activeTab === 'cancellations' ? 'active' : ''}
                  onClick={() => setActiveTab('cancellations')}
                >
                  {t('myCancellations')}
                </button>
              </li>
            </ul>
          </div>

          <div className="sidebar-section">
            <ul className="sidebar-menu">
              <li>
                <button
                  className={activeTab === 'wishlist' ? 'active' : ''}
                  onClick={() => setActiveTab('wishlist')}
                >
                  {t('myWishList')}
                </button>
              </li>
              <li>
                <button
                  className={activeTab === 'becomeSeller' ? 'active' : ''}
                  onClick={() => setActiveTab('becomeSeller')}
                >
                  {t('becomeSeller')}
                </button>
              </li>
            </ul>
          </div>

          <div className="sidebar-section">
            <ul className="sidebar-menu">
              <li>
                <button
                  className="logout-btn"
                  onClick={handleLogout}
                >
                  {t('logout')}
                </button>
              </li>
            </ul>
          </div>
        </div>

        <div className="account-content">
          {activeTab === 'profile' && (
            <div className="profile-section">
              <div className="profile-header">
                <h2 className="section-title">{t('myProfile')}</h2>
                {!isEditing && (
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={handleEdit}
                  >
                    {t('updateProfile')}
                  </button>
                )}
              </div>

              {success && (
                <div className="success-message">
                  ✓ {t('profileUpdated')}
                </div>
              )}
              {error && (
                <div className="error-message">{error}</div>
              )}

              {!isEditing ? (
                <div className="profile-info">
                  <div className="info-section">
                    <h3 className="info-section-title">{t('personalInformation')}</h3>
                    <div className="info-grid">
                      <div className="info-item">
                        <label>{t('firstName')}</label>
                        <p>{user?.firstName || t('notSet')}</p>
                      </div>
                      <div className="info-item">
                        <label>{t('lastName')}</label>
                        <p>{user?.lastName || t('notSet')}</p>
                      </div>
                      <div className="info-item">
                        <label>{t('email')}</label>
                        <p>{user?.email || t('notSet')}</p>
                      </div>
                      <div className="info-item">
                        <label>{t('phoneNumber')}</label>
                        <p>{user?.phoneNumber || t('notSet')}</p>
                      </div>
                      <div className="info-item">
                        <label>Address</label>
                        <p>{formData.address || t('notSet')}</p>
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <form onSubmit={handleSubmit} className="profile-form">
                <div className="form-section">
                  <h3 className="form-section-title">{t('personalInformation')}</h3>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="firstName">{t('firstName')}</label>
                      <input
                        type="text"
                        id="firstName"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleChange}
                        placeholder={t('firstName')}
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="lastName">{t('lastName')}</label>
                      <input
                        type="text"
                        id="lastName"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleChange}
                        placeholder={t('lastName')}
                      />
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="email">{t('email')}</label>
                      <input
                        type="email"
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder={t('email')}
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="phoneNumber">{t('phoneNumber')}</label>
                      <input
                        type="text"
                        id="phoneNumber"
                        name="phoneNumber"
                        value={formData.phoneNumber}
                        onChange={handleChange}
                        placeholder={t('phoneNumber')}
                        disabled
                      />
                      <small className="form-hint">{t('phoneCannotChange')}</small>
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="address">Address</label>
                      <input
                        type="text"
                        id="address"
                        name="address"
                        value={formData.address}
                        onChange={handleChange}
                        placeholder="Address"
                      />
                    </div>
                  </div>
                </div>

                <div className="form-section">
                  <h3 className="form-section-title">{t('passwordChanges')}</h3>
                  <div className="form-group">
                    <label htmlFor="currentPassword">{t('currentPassword')}</label>
                    <input
                      type="password"
                      id="currentPassword"
                      name="currentPassword"
                      value={formData.currentPassword}
                      onChange={handleChange}
                      placeholder={t('currentPassword')}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="newPassword">{t('newPassword')}</label>
                    <input
                      type="password"
                      id="newPassword"
                      name="newPassword"
                      value={formData.newPassword}
                      onChange={handleChange}
                      placeholder={t('newPassword')}
                    />
                  </div>
                  <div className="form-group">
                    <label htmlFor="confirmPassword">{t('confirmPassword')}</label>
                    <input
                      type="password"
                      id="confirmPassword"
                      name="confirmPassword"
                      value={formData.confirmPassword}
                      onChange={handleChange}
                      placeholder={t('confirmPassword')}
                    />
                  </div>
                </div>

                <div className="form-actions">
                  <button
                    type="button"
                    className="btn btn-cancel"
                    onClick={handleCancel}
                  >
                    {t('cancel')}
                  </button>
                  <button
                    type="submit"
                    className="btn btn-save"
                    disabled={loading}
                  >
                    {loading ? t('saving') : t('saveChanges')}
                  </button>
                </div>
              </form>
              )}
            </div>
          )}

          {activeTab === 'address' && (
            <div className="address-section">
              <h2 className="section-title">{t('addressBook')}</h2>
              <p className="empty-state">{t('noAddresses')}</p>
            </div>
          )}

          {activeTab === 'payment' && (
            <div className="payment-section">
              <h2 className="section-title">{t('myPaymentOptions')}</h2>
              <p className="empty-state">{t('noPaymentMethods')}</p>
            </div>
          )}

          {activeTab === 'orders' && (
            <div className="orders-section">
              <h2 className="section-title">{t('orderHistory') || 'Order History'}</h2>
              
              {ordersLoading ? (
                <div className="loading-state">
                  <p>{t('loading') || 'Loading...'}</p>
                </div>
              ) : orders.length === 0 ? (
                <div className="empty-state">
                  <p>{t('noOrders') || 'You have no orders yet'}</p>
                  <Link to="/" className="btn btn-primary">
                    {t('startShopping') || 'Start Shopping'}
                  </Link>
                </div>
              ) : (
                <div className="orders-list">
                  {orders.map((order) => (
                    <div key={order.id} className="order-card">
                      <div className="order-header">
                        <div className="order-info">
                          <h3 className="order-number">
                            {t('orderNumber') || 'Order'}: {order.orderNumber}
                          </h3>
                          <p className="order-date">
                            {new Date(order.createdAt).toLocaleDateString()}
                          </p>
                        </div>
                        <div className="order-status">
                          <span className={`status-badge status-${order.status?.toLowerCase()}`}>
                            {order.status}
                          </span>
                        </div>
                      </div>
                      
                      <div className="order-items">
                        <p className="items-count">
                          {order.orderItems?.length || 0} {t('items') || 'items'}
                        </p>
                        {order.orderItems?.slice(0, 3).map((item, index) => (
                          <div key={index} className="order-item-preview">
                            <span>{item.productName}</span>
                            <span>×{item.quantity}</span>
                          </div>
                        ))}
                        {order.orderItems?.length > 3 && (
                          <p className="more-items">
                            +{order.orderItems.length - 3} {t('moreItems') || 'more items'}
                          </p>
                        )}
                      </div>
                      
                      <div className="order-footer">
                        <div className="order-total">
                          <span className="total-label">{t('total') || 'Total'}:</span>
                          <span className="total-amount">${parseFloat(order.totalAmount || 0).toFixed(2)}</span>
                        </div>
                        <button
                          className="btn btn-secondary"
                          onClick={() => setSelectedOrder(order)}
                        >
                          {t('viewDetails') || 'View Details'}
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}

              {/* Order Details Modal */}
              {selectedOrder && (
                <div className="order-modal-overlay" onClick={() => setSelectedOrder(null)}>
                  <div className="order-modal" onClick={(e) => e.stopPropagation()}>
                    <div className="modal-header">
                      <h2>{t('orderDetails') || 'Order Details'}</h2>
                      <button
                        className="modal-close"
                        onClick={() => setSelectedOrder(null)}
                      >
                        ×
                      </button>
                    </div>
                    
                    <div className="modal-content">
                      <div className="order-detail-section">
                        <h3>{t('orderInformation') || 'Order Information'}</h3>
                        <div className="detail-row">
                          <span>{t('orderNumber') || 'Order Number'}:</span>
                          <span>{selectedOrder.orderNumber}</span>
                        </div>
                        <div className="detail-row">
                          <span>{t('orderDate') || 'Order Date'}:</span>
                          <span>{new Date(selectedOrder.createdAt).toLocaleString()}</span>
                        </div>
                        <div className="detail-row">
                          <span>{t('status') || 'Status'}:</span>
                          <span className={`status-badge status-${selectedOrder.status?.toLowerCase()}`}>
                            {selectedOrder.status}
                          </span>
                        </div>
                        <div className="detail-row">
                          <span>{t('paymentStatus') || 'Payment Status'}:</span>
                          <span className={`status-badge status-${selectedOrder.paymentStatus?.toLowerCase()}`}>
                            {selectedOrder.paymentStatus}
                          </span>
                        </div>
                        <div className="detail-row">
                          <span>{t('paymentMethod') || 'Payment Method'}:</span>
                          <span>{selectedOrder.paymentMethod}</span>
                        </div>
                        <div className="detail-row">
                          <span>{t('shippingAddress') || 'Shipping Address'}:</span>
                          <span>{selectedOrder.shippingAddress}</span>
                        </div>
                      </div>

                      <div className="order-detail-section">
                        <h3>{t('orderItems') || 'Order Items'}</h3>
                        <div className="order-items-list">
                          {selectedOrder.orderItems?.map((item, index) => (
                            <div key={index} className="order-item-detail">
                              <div className="item-info">
                                <h4>{item.productName}</h4>
                                <p>{t('quantity') || 'Quantity'}: {item.quantity}</p>
                                <p>{t('unitPrice') || 'Unit Price'}: ${parseFloat(item.unitPrice || 0).toFixed(2)}</p>
                              </div>
                              <div className="item-total">
                                ${parseFloat(item.totalAmount || 0).toFixed(2)}
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>

                      <div className="order-detail-section">
                        <div className="order-total-section">
                          <div className="total-row">
                            <span>{t('subtotal') || 'Subtotal'}:</span>
                            <span>${parseFloat(selectedOrder.totalAmount || 0).toFixed(2)}</span>
                          </div>
                          <div className="total-row final">
                            <span>{t('total') || 'Total'}:</span>
                            <span>${parseFloat(selectedOrder.totalAmount || 0).toFixed(2)}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'returns' && (
            <div className="returns-section">
              <h2 className="section-title">{t('myReturns')}</h2>
              <p className="empty-state">{t('noReturns')}</p>
            </div>
          )}

          {activeTab === 'cancellations' && (
            <div className="cancellations-section">
              <h2 className="section-title">{t('myCancellations')}</h2>
              <p className="empty-state">{t('noCancellations')}</p>
            </div>
          )}

          {activeTab === 'wishlist' && (
            <div className="wishlist-section">
              <h2 className="section-title">{t('myWishList')}</h2>
              {wishlistLoading ? (
                <div className="loading">{t('loading')}</div>
              ) : wishlistItems.length === 0 ? (
                <p className="empty-state">{t('wishlistEmpty')}</p>
              ) : (
                <div className="wishlist-items">
                  {wishlistItems.map((item) => {
                    const imageUrl = item.productImageUrl 
                      ? (item.productImageUrl.startsWith('http') 
                          ? item.productImageUrl 
                          : `http://localhost:8082${item.productImageUrl}`)
                      : 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTQiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=';
                    
                    return (
                      <div key={item.id} className="wishlist-item">
                        <div className="wishlist-item-image">
                          <img 
                            src={imageUrl} 
                            alt={item.productName}
                            onError={(e) => {
                              e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTQiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=';
                            }}
                          />
                        </div>
                        <div className="wishlist-item-info">
                          <h3 className="wishlist-item-name">{item.productName}</h3>
                          {item.productDescription && (
                            <p className="wishlist-item-description">{item.productDescription}</p>
                          )}
                          <div className="wishlist-item-price">${item.productPrice?.toFixed(2) || '0.00'}</div>
                          <div className="wishlist-item-actions">
                            <button
                              className="remove-from-wishlist-btn"
                              onClick={async () => {
                                try {
                                  const tokenPayload = tokenService.getTokenPayload();
                                  const userId = tokenPayload?.userId;
                                  if (!userId) return;

                                  const wishlist = await wishlistAPI.getWishlistByUserId(userId);
                                  if (wishlist) {
                                    await wishlistAPI.removeItemFromWishlist(wishlist.id, item.id);
                                    // Reload wishlist
                                    const updatedWishlist = await wishlistAPI.getWishlistByUserId(userId);
                                    if (updatedWishlist && updatedWishlist.wishlistItems) {
                                      setWishlistItems(updatedWishlist.wishlistItems);
                                    } else {
                                      setWishlistItems([]);
                                    }
                                  }
                                } catch (error) {
                                  console.error('Error removing from wishlist:', error);
                                  alert(t('wishlistError') || 'Failed to remove item');
                                }
                              }}
                            >
                              {t('removeFromWishlist') || 'Remove'}
                            </button>
                            <button
                              className="view-product-btn"
                              onClick={() => navigate(`/product/${item.productId}`)}
                            >
                              {t('viewProduct') || 'View Product'}
                            </button>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {activeTab === 'becomeSeller' && (
            <div className="become-seller-section">
              <div className="seller-header">
                <h2 className="section-title">{t('becomeSeller')}</h2>
                <p className="seller-subtitle">{t('becomeSellerSubtitle')}</p>
              </div>

              {success && (
                <div className="success-message">
                  ✓ {t('sellerRegistrationSuccess')}
                </div>
              )}
              {error && (
                <div className="error-message">{error}</div>
              )}

              <form onSubmit={handleSellerSubmit} className="seller-form">
                <div className="form-section">
                  <h3 className="form-section-title">{t('sellerInformation')}</h3>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="email">
                        {t('email')} <span className="required">*</span>
                      </label>
                      <input
                        type="email"
                        id="email"
                        name="email"
                        value={sellerFormData.email}
                        onChange={handleSellerFormChange}
                        placeholder={t('email')}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label htmlFor="phoneNumber">
                        {t('phoneNumber')} <span className="required">*</span>
                      </label>
                      <input
                        type="text"
                        id="phoneNumber"
                        name="phoneNumber"
                        value={sellerFormData.phoneNumber}
                        onChange={handleSellerFormChange}
                        placeholder={t('phoneNumber')}
                        required
                      />
                    </div>
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="businessName">{t('businessName')}</label>
                      <input
                        type="text"
                        id="businessName"
                        name="businessName"
                        value={sellerFormData.businessName}
                        onChange={handleSellerFormChange}
                        placeholder={t('enterBusinessName')}
                      />
                      <small className="form-hint">{t('businessNameOptional')}</small>
                    </div>
                    <div className="form-group">
                      <label htmlFor="address">{t('address')}</label>
                      <input
                        type="text"
                        id="address"
                        name="address"
                        value={sellerFormData.address}
                        onChange={handleSellerFormChange}
                        placeholder={t('enterAddress')}
                      />
                    </div>
                  </div>
                </div>

                <div className="seller-info-box">
                  <h4 className="info-box-title">ℹ️ {t('importantInformation')}</h4>
                  <ul className="info-box-list">
                    <li>{t('sellerInfo1')}</li>
                    <li>{t('sellerInfo2')}</li>
                  </ul>
                </div>

                <div className="form-actions">
                  <button
                    type="button"
                    className="btn btn-cancel"
                    onClick={() => {
                      setActiveTab('profile');
                      setError('');
                      setSuccess(false);
                    }}
                  >
                    {t('cancel')}
                  </button>
                  <button
                    type="submit"
                    className="btn btn-save"
                    disabled={loading}
                  >
                    {loading ? t('submitting') : t('submitApplication')}
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>
      </div>

      {/* Footer */}
      <footer className="footer">
        <div className="footer-content">
          <div className="footer-section">
            <h3>Exclusive</h3>
            <h4>{t('subscribe')}</h4>
            <p>{t('getDiscount')}</p>
            <div className="subscribe-input">
              <input type="email" placeholder={t('enterEmail')} />
              <button>→</button>
            </div>
          </div>
          <div className="footer-section">
            <h4>{t('support')}</h4>
            <p>111 Bijoy saroni, Dhaka, DH 1515, Bangladesh</p>
            <p>exclusive@gmail.com</p>
            <p>+88015-8888-9999</p>
          </div>
          <div className="footer-section">
            <h4>{t('account')}</h4>
            <Link to="/account">{t('myAccount')}</Link>
            <Link to="/login">{t('login')} / {t('signup')}</Link>
            <Link to="/cart">Cart</Link>
            <Link to="/wishlist">Wishlist</Link>
            <Link to="/shop">Shop</Link>
          </div>
          <div className="footer-section">
            <h4>{t('quickLink')}</h4>
            <Link to="/privacy">{t('privacyPolicy')}</Link>
            <Link to="/terms">{t('termsOfUse')}</Link>
            <Link to="/faq">{t('faq')}</Link>
            <Link to="/contact">{t('contact')}</Link>
          </div>
        </div>
        <div className="footer-copyright">
          <p>{t('copyright')}</p>
        </div>
      </footer>
    </div>
  );
};

export default MyAccount;


