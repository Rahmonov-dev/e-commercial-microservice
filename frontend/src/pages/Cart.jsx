import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import { orderAPI, cartAPI } from '../services/api';
import { tokenService } from '../services/tokenService';
import './Cart.css';

const Cart = () => {
  const { user, isAuthenticated } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [checkoutForm, setCheckoutForm] = useState({
    shippingAddress: '',
    paymentMethod: 'CASH',
  });

  // Load cart items from backend
  useEffect(() => {
    const loadCartItems = async () => {
      if (!isAuthenticated) {
        // Fallback to localStorage for non-authenticated users
        const saved = localStorage.getItem('cart');
        if (saved) {
          setCartItems(JSON.parse(saved));
        }
        return;
      }

      try {
        const tokenPayload = tokenService.getTokenPayload();
        const userId = tokenPayload?.userId;
        
        if (!userId) return;

        const cartResponse = await cartAPI.getCartItemsByUserId(userId);
        // Convert backend cart items to frontend format
        const items = cartResponse.map(item => ({
          id: item.productId,
          name: item.productName,
          price: parseFloat(item.unitPrice),
          quantity: item.quantity,
          cartItemId: item.id,
        }));
        setCartItems(items);
      } catch (error) {
        console.error('Error loading cart items:', error);
        // Fallback to localStorage
        const saved = localStorage.getItem('cart');
        if (saved) {
          setCartItems(JSON.parse(saved));
        }
      }
    };

    loadCartItems();
  }, [isAuthenticated]);

  // Listen for cart updates
  useEffect(() => {
    const handleCartUpdate = async () => {
      if (!isAuthenticated) return;
      
      try {
        const tokenPayload = tokenService.getTokenPayload();
        const userId = tokenPayload?.userId;
        if (!userId) return;

        const cartResponse = await cartAPI.getCartItemsByUserId(userId);
        const items = cartResponse.map(item => ({
          id: item.productId,
          name: item.productName,
          price: parseFloat(item.unitPrice),
          quantity: item.quantity,
          cartItemId: item.id,
        }));
        setCartItems(items);
      } catch (error) {
        console.error('Error reloading cart:', error);
      }
    };

    window.addEventListener('cartUpdated', handleCartUpdate);
    return () => {
      window.removeEventListener('cartUpdated', handleCartUpdate);
    };
  }, [isAuthenticated]);

  const handleRemoveItem = async (productId) => {
    if (!isAuthenticated) {
      // Fallback to localStorage
      const updatedCart = cartItems.filter(item => item.id !== productId);
      setCartItems(updatedCart);
      localStorage.setItem('cart', JSON.stringify(updatedCart));
      window.dispatchEvent(new Event('cartUpdated'));
      return;
    }

    try {
      const item = cartItems.find(item => item.id === productId);
      if (item && item.cartItemId) {
        await cartAPI.removeItemFromCart(item.cartItemId);
        
        // Reload cart items
        const tokenPayload = tokenService.getTokenPayload();
        const userId = tokenPayload?.userId;
        if (userId) {
          const cartResponse = await cartAPI.getCartItemsByUserId(userId);
          const items = cartResponse.map(item => ({
            id: item.productId,
            name: item.productName,
            price: parseFloat(item.unitPrice),
            quantity: item.quantity,
            cartItemId: item.id,
          }));
          setCartItems(items);
          window.dispatchEvent(new Event('cartUpdated'));
        }
      }
    } catch (error) {
      console.error('Error removing item:', error);
      alert(error.response?.data?.message || error.message || 'Failed to remove item');
    }
  };

  const handleUpdateQuantity = async (productId, newQuantity) => {
    if (newQuantity < 1) {
      handleRemoveItem(productId);
      return;
    }

    if (!isAuthenticated) {
      // Fallback to localStorage
      const updatedCart = cartItems.map(item =>
        item.id === productId ? { ...item, quantity: newQuantity } : item
      );
      setCartItems(updatedCart);
      localStorage.setItem('cart', JSON.stringify(updatedCart));
      window.dispatchEvent(new Event('cartUpdated'));
      return;
    }

    try {
      const item = cartItems.find(item => item.id === productId);
      if (item && item.cartItemId) {
        await cartAPI.updateCartItemQuantity(item.cartItemId, newQuantity);
        
        // Reload cart items
        const tokenPayload = tokenService.getTokenPayload();
        const userId = tokenPayload?.userId;
        if (userId) {
          const cartResponse = await cartAPI.getCartItemsByUserId(userId);
          const items = cartResponse.map(item => ({
            id: item.productId,
            name: item.productName,
            price: parseFloat(item.unitPrice),
            quantity: item.quantity,
            cartItemId: item.id,
          }));
          setCartItems(items);
          window.dispatchEvent(new Event('cartUpdated'));
        }
      }
    } catch (error) {
      console.error('Error updating quantity:', error);
      alert(error.response?.data?.message || error.message || 'Failed to update quantity');
    }
  };

  const calculateSubtotal = () => {
    return cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  };

  const calculateTotal = () => {
    return calculateSubtotal();
  };

  const handleCheckout = async (e) => {
    e.preventDefault();
    
    if (!isAuthenticated) {
      navigate('/login', { state: { from: '/cart' } });
      return;
    }

    if (cartItems.length === 0) {
      setError(t('cartEmpty') || 'Cart is empty');
      return;
    }

    if (!checkoutForm.shippingAddress.trim()) {
      setError(t('shippingAddressRequired') || 'Shipping address is required');
      return;
    }

    setLoading(true);
    setError('');
    setSuccess('');

    try {
      // Get userId from JWT token
      const tokenPayload = tokenService.getTokenPayload();
      const userId = tokenPayload?.userId;
      
      if (!userId) {
        setError(t('userNotAuthenticated') || 'User not authenticated');
        setLoading(false);
        return;
      }

      const orderData = {
        userId: userId,
        shippingAddress: checkoutForm.shippingAddress,
        paymentMethod: checkoutForm.paymentMethod,
        orderItems: cartItems.map(item => ({
          productId: item.id,
          productName: item.name,
          quantity: item.quantity,
          unitPrice: item.price,
        })),
      };

      console.log('Creating order with data:', orderData);

      // Add timeout to prevent hanging
      const timeoutPromise = new Promise((_, reject) => {
        setTimeout(() => reject(new Error('Request timeout. Please try again.')), 30000);
      });

      const order = await Promise.race([
        orderAPI.createOrder(orderData),
        timeoutPromise
      ]);
      
      console.log('Order created successfully:', order);
      
      setSuccess(t('orderCreated') || 'Order created successfully!');
      
      // Clear cart items after successful order creation
      if (isAuthenticated) {
        try {
          await cartAPI.clearCartItemsByUserId(userId);
          console.log('Cart cleared successfully');
        } catch (error) {
          console.error('Error clearing cart items:', error);
          // Fallback to clearCart if clearCartItemsByUserId fails
          try {
            await cartAPI.clearCart(userId);
            console.log('Cart cleared using fallback method');
          } catch (fallbackError) {
            console.error('Error clearing cart (fallback):', fallbackError);
          }
        }
      } else {
        localStorage.removeItem('cart');
      }
      setCartItems([]);
      window.dispatchEvent(new Event('cartUpdated'));
      
      // Redirect to order confirmation or home
      setTimeout(() => {
        navigate('/', { state: { orderCreated: true, orderNumber: order.orderNumber } });
      }, 2000);
    } catch (err) {
      console.error('Checkout error:', err);
      const errorMessage = err.response?.data?.message || err.message || t('orderError') || 'Failed to create order';
      setError(errorMessage);
      console.error('Error details:', {
        message: errorMessage,
        response: err.response,
        status: err.response?.status,
        data: err.response?.data
      });
    } finally {
      setLoading(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="cart-page">
        <Navbar />
        <div className="cart-container">
          <div className="cart-empty">
            <h2>{t('pleaseLogin') || 'Please login to view your cart'}</h2>
            <button onClick={() => navigate('/login')} className="btn btn-primary">
              {t('login')}
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <Navbar />
      <div className="cart-container">
        <h1 className="cart-title">{t('cart') || 'Shopping Cart'}</h1>

        {success && (
          <div className="success-message">{success}</div>
        )}
        {error && (
          <div className="error-message">{error}</div>
        )}

        {cartItems.length === 0 ? (
          <div className="cart-empty">
            <h2>{t('cartEmpty') || 'Your cart is empty'}</h2>
            <p>{t('addItemsToCart') || 'Add items to your cart to continue shopping'}</p>
            <button onClick={() => navigate('/')} className="btn btn-primary">
              {t('continueShopping') || 'Continue Shopping'}
            </button>
          </div>
        ) : (
          <div className="cart-content">
            <div className="cart-items">
              <h2>{t('cartItems') || 'Cart Items'}</h2>
              {cartItems.map((item) => (
                <div key={item.id} className="cart-item">
                  <div className="cart-item-image">
                    <img 
                      src={item.imageUrl || 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTIiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4='} 
                      alt={item.name}
                      onError={(e) => {
                        e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTIiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=';
                      }}
                    />
                  </div>
                  <div className="cart-item-info">
                    <h3>{item.name}</h3>
                    <p className="cart-item-price">${item.price?.toFixed(2) || '0.00'}</p>
                  </div>
                  <div className="cart-item-quantity">
                    <button 
                      onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                      className="quantity-btn"
                    >
                      −
                    </button>
                    <span className="quantity-value">{item.quantity}</span>
                    <button 
                      onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                      className="quantity-btn"
                    >
                      +
                    </button>
                  </div>
                  <div className="cart-item-total">
                    <p>${(item.price * item.quantity).toFixed(2)}</p>
                  </div>
                  <button 
                    onClick={() => handleRemoveItem(item.id)}
                    className="remove-btn"
                    title={t('remove') || 'Remove'}
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>

            <div className="cart-summary">
              <h2>{t('orderSummary') || 'Order Summary'}</h2>
              <div className="summary-row">
                <span>{t('subtotal') || 'Subtotal'}:</span>
                <span>${calculateSubtotal().toFixed(2)}</span>
              </div>
              <div className="summary-row">
                <span>{t('shipping') || 'Shipping'}:</span>
                <span>{t('free') || 'Free'}</span>
              </div>
              <div className="summary-row total">
                <span>{t('total') || 'Total'}:</span>
                <span>${calculateTotal().toFixed(2)}</span>
              </div>

              <form onSubmit={handleCheckout} className="checkout-form">
                <div className="form-group">
                  <label htmlFor="shippingAddress">{t('shippingAddress') || 'Shipping Address'} *</label>
                  <textarea
                    id="shippingAddress"
                    name="shippingAddress"
                    value={checkoutForm.shippingAddress}
                    onChange={(e) => setCheckoutForm({ ...checkoutForm, shippingAddress: e.target.value })}
                    required
                    rows="3"
                    placeholder={t('enterShippingAddress') || 'Enter your shipping address'}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="paymentMethod">{t('paymentMethod') || 'Payment Method'} *</label>
                  <select
                    id="paymentMethod"
                    name="paymentMethod"
                    value={checkoutForm.paymentMethod}
                    onChange={(e) => setCheckoutForm({ ...checkoutForm, paymentMethod: e.target.value })}
                    required
                  >
                    <option value="CASH">{t('cash') || 'Cash on Delivery'}</option>
                    <option value="CARD">{t('card') || 'Credit Card'}</option>
                    <option value="PAYPAL">{t('paypal') || 'PayPal'}</option>
                  </select>
                </div>
                <button 
                  type="submit" 
                  className="btn btn-primary checkout-btn"
                  disabled={loading}
                >
                  {loading ? t('processing') || 'Processing...' : t('checkout') || 'Checkout'}
                </button>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Cart;

