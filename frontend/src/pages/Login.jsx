import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import Navbar from '../components/Navbar';
import loginPageImage from '../assets/images/loginpage.png';
import './Auth.css';

const Login = () => {
  const [formData, setFormData] = useState({
    phoneNumber: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const location = useLocation();
  
  // Check for success message from navigation state
  useEffect(() => {
    if (location.state?.message) {
      setSuccess(location.state.message);
      // Clear the state to prevent showing message on refresh
      navigate(location.pathname, { replace: true, state: {} });
    }
  }, [location, navigate]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const result = await login(formData);

    if (result.success) {
      navigate('/');
    } else {
      setError(result.error || 'Login failed. Please try again.');
    }

    setLoading(false);
  };

  return (
    <div className="auth-container">
      <Navbar />

      {/* Main Content */}
      <div className="auth-main">
        <div className="auth-left">
          <div className="auth-image-container">
            <img 
              src={loginPageImage} 
              alt="Login Illustration" 
              className="auth-image"
            />
          </div>
        </div>

        <div className="auth-right">
          <div className="auth-form-container">
            <h1 className="auth-title">{t('loginTitle')}</h1>
            <p className="auth-subtitle">{t('loginSubtitle')}</p>

            {success && (
              <div className="success-message" style={{
                backgroundColor: '#d4edda',
                color: '#155724',
                padding: '14px 16px',
                borderRadius: '6px',
                marginBottom: '16px',
                borderLeft: '4px solid #28a745'
              }}>
                ✓ {success}
              </div>
            )}
            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit} className="auth-form">
              <input
                type="text"
                name="phoneNumber"
                placeholder={t('emailOrPhone')}
                className="input-field"
                value={formData.phoneNumber}
                onChange={handleChange}
                required
              />

              <input
                type="password"
                name="password"
                placeholder={t('password')}
                className="input-field"
                value={formData.password}
                onChange={handleChange}
                required
              />

              <button
                type="submit"
                className="btn btn-primary btn-full"
                disabled={loading}
              >
                {loading ? t('loggingIn') : t('logIn')}
              </button>
            </form>

            <p className="auth-footer">
              {t('dontHaveAccount')} <Link to="/signup" className="link">{t('signup')}</Link>
            </p>
          </div>
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

export default Login;











