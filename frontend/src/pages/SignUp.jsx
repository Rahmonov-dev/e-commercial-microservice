import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../context/LanguageContext';
import Navbar from '../components/Navbar';
import loginPageImage from '../assets/images/loginpage.png';
import './Auth.css';

const SignUp = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { register } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

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

    const result = await register(formData);

    if (result.success) {
      navigate('/');
    } else {
      setError(result.error || 'Registration failed. Please try again.');
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
              alt="Sign Up Illustration" 
              className="auth-image"
            />
          </div>
        </div>

        <div className="auth-right">
          <div className="auth-form-container">
            <h1 className="auth-title">{t('createAccount')}</h1>
            <p className="auth-subtitle">{t('enterDetails')}</p>

            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit} className="auth-form">
              <input
                type="text"
                name="firstName"
                placeholder={t('firstName')}
                className="input-field"
                value={formData.firstName}
                onChange={handleChange}
                required
              />

              <input
                type="text"
                name="lastName"
                placeholder={t('lastName')}
                className="input-field"
                value={formData.lastName}
                onChange={handleChange}
                required
              />

              <input
                type="text"
                name="phoneNumber"
                placeholder={t('phoneNumber')}
                className="input-field"
                value={formData.phoneNumber}
                onChange={handleChange}
                required
              />

              <input
                type="email"
                name="email"
                placeholder={t('email')}
                className="input-field"
                value={formData.email}
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
                minLength={6}
              />

              <button
                type="submit"
                className="btn btn-primary btn-full"
                disabled={loading}
              >
                {loading ? t('creatingAccount') : t('createAccountBtn')}
              </button>
            </form>

            <p className="auth-footer">
              {t('alreadyHaveAccount')} <Link to="/login" className="link">{t('logIn')}</Link>
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

export default SignUp;






