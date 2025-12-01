import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import './Navbar.css';

// Cart Badge Component
const CartBadge = () => {
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    const updateCartCount = () => {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]');
      const count = cart.reduce((sum, item) => sum + (item.quantity || 0), 0);
      setCartCount(count);
    };

    updateCartCount();
    const interval = setInterval(updateCartCount, 500);
    window.addEventListener('storage', updateCartCount);
    // Listen for custom cart update event
    window.addEventListener('cartUpdated', updateCartCount);

    return () => {
      clearInterval(interval);
      window.removeEventListener('storage', updateCartCount);
      window.removeEventListener('cartUpdated', updateCartCount);
    };
  }, []);

  return cartCount > 0 ? <span className="cart-badge">{cartCount}</span> : null;
};

const Navbar = ({ showUserInfo = false }) => {
  const { user, logout, isAuthenticated, userRole } = useAuth();
  const { language, changeLanguage, t } = useLanguage();
  const navigate = useNavigate();
  const location = useLocation();

  // Get banner text from localStorage or use default
  const [bannerText, setBannerText] = React.useState(() => {
    return localStorage.getItem('bannerText') || 'Summer Sale For All Swim Suits And Free Express Delivery - OFF 50% Shop Now';
  });

  // Listen for banner text changes
  React.useEffect(() => {
    const handleStorageChange = () => {
      const newBannerText = localStorage.getItem('bannerText') || 'Summer Sale For All Swim Suits And Free Express Delivery - OFF 50% Shop Now';
      setBannerText(newBannerText);
    };

    // Listen for custom event when banner is updated
    window.addEventListener('bannerTextUpdated', handleStorageChange);
    
    // Also check localStorage periodically
    const interval = setInterval(() => {
      handleStorageChange();
    }, 1000);

    return () => {
      window.removeEventListener('bannerTextUpdated', handleStorageChange);
      clearInterval(interval);
    };
  }, []);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const handleLogoClick = () => {
    navigate('/');
  };

  return (
    <>
      {/* Top Banner */}
      <div className="top-banner">
        <p>{bannerText}</p>
      </div>

      {/* Header */}
      <header className="header">
        <div className="header-content">
          <div className="logo" onClick={handleLogoClick} style={{ cursor: 'pointer' }}>
            Exclusive
          </div>
          <nav className="nav-links">
            <Link to="/" className={location.pathname === '/' ? 'active' : ''}>{t('home')}</Link>
            <Link to="/contact" className={location.pathname === '/contact' ? 'active' : ''}>{t('contact')}</Link>
            <Link to="/about" className={location.pathname === '/about' ? 'active' : ''}>{t('about')}</Link>
            {isAuthenticated && (userRole === 'ROLE_SUPER_ADMIN' || userRole === 'SUPER_ADMIN') && (
              <Link to="/super-admin" className={location.pathname === '/super-admin' ? 'active' : ''}>
                {t('superAdminPage')}
              </Link>
            )}
            {isAuthenticated && userRole === 'ROLE_ADMIN' && (
              <Link to="/admin" className={location.pathname === '/admin' ? 'active' : ''}>
                {t('adminPage')}
              </Link>
            )}
            {isAuthenticated && (userRole === 'ROLE_SELLER' || userRole === 'SELLER') && (
              <Link to="/seller" className={location.pathname === '/seller' ? 'active' : ''}>
                {t('sellerPage')}
              </Link>
            )}
            {!isAuthenticated && (
              <>
                <Link to="/login" className={location.pathname === '/login' ? 'active' : ''}>
                  {t('login')}
                </Link>
                <Link to="/signup" className={location.pathname === '/signup' ? 'active' : ''}>
                  {t('signup')}
                </Link>
              </>
            )}
          </nav>
          <div className="header-actions">
            <select 
              className="language-select"
              value={language}
              onChange={(e) => changeLanguage(e.target.value)}
            >
              <option value="en">EN</option>
              <option value="uz">UZ</option>
              <option value="ru">RU</option>
            </select>
            {/* Cart Button */}
            <Link to="/cart" className="cart-btn" title={t('cart') || 'Cart'}>
              <span className="cart-icon">🛒</span>
              <CartBadge />
            </Link>
            {isAuthenticated && (
              <Link to="/account" className="my-account-btn" title={t('myAccount')}>
                <span className="account-icon">👤</span>
              </Link>
            )}
          </div>
        </div>
      </header>
    </>
  );
};

export default Navbar;
