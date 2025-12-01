import React, { useState } from 'react';
import Navbar from '../components/Navbar';
import { useLanguage } from '../context/LanguageContext';
import './Contact.css';

const Contact = () => {
  const { t } = useLanguage();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: '',
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

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

    // Simulate API call
    setTimeout(() => {
      setLoading(false);
      setSuccess(true);
      setFormData({
        name: '',
        email: '',
        phone: '',
        subject: '',
        message: '',
      });
    }, 1000);
  };

  return (
    <div className="contact-container">
      <Navbar />

      {/* Hero Section */}
      <section className="contact-hero">
        <div className="hero-content">
          <h1 className="hero-title">{t('getInTouch')}</h1>
          <p className="hero-subtitle">
            {t('contactSubtitle')}
          </p>
        </div>
      </section>

      {/* Main Content */}
      <section className="contact-main">
        <div className="contact-wrapper">
          {/* Contact Form */}
          <div className="contact-form-section">
            <h2 className="form-title">{t('sendMessage')}</h2>
            {success && (
              <div className="success-message">
                ✓ {t('messageSent')}
              </div>
            )}
            {error && (
              <div className="error-message">{error}</div>
            )}
            <form onSubmit={handleSubmit} className="contact-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="name">{t('fullName')} *</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    placeholder={t('fullName')}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="email">{t('emailAddress')} *</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                    placeholder={t('email')}
                  />
                </div>
              </div>
              <div className="form-group">
                <label htmlFor="phone">{t('phoneNumber')}</label>
                <input
                  type="tel"
                  id="phone"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  placeholder={t('phoneNumber')}
                />
              </div>
              <div className="form-group">
                <label htmlFor="subject">{t('subject')} *</label>
                <input
                  type="text"
                  id="subject"
                  name="subject"
                  value={formData.subject}
                  onChange={handleChange}
                  required
                  placeholder={t('subject')}
                />
              </div>
              <div className="form-group">
                <label htmlFor="message">{t('message')} *</label>
                <textarea
                  id="message"
                  name="message"
                  value={formData.message}
                  onChange={handleChange}
                  required
                  rows="6"
                  placeholder={t('message')}
                ></textarea>
              </div>
              <button
                type="submit"
                className="btn btn-primary btn-full"
                disabled={loading}
              >
                {loading ? t('sending') : t('sendMessageBtn')}
              </button>
            </form>
          </div>

          {/* Contact Information */}
          <div className="contact-info-section">
            <h2 className="info-title">{t('contactInformation')}</h2>
            <div className="info-content">
              <div className="info-item">
                <div className="info-icon">📍</div>
                <div className="info-details">
                  <h3>{t('address')}</h3>
                  <p>111 Bijoy saroni, Dhaka</p>
                  <p>DH 1515, Bangladesh</p>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">📧</div>
                <div className="info-details">
                  <h3>{t('email')}</h3>
                  <p>exclusive@gmail.com</p>
                  <p>support@exclusive.com</p>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">📞</div>
                <div className="info-details">
                  <h3>{t('phoneNumber')}</h3>
                  <p>+88015-8888-9999</p>
                  <p>+88015-8888-9998</p>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">🕒</div>
                <div className="info-details">
                  <h3>{t('businessHours')}</h3>
                  <p>{t('mondayFriday')}</p>
                  <p>{t('saturday')}</p>
                  <p>{t('sunday')}</p>
                </div>
              </div>
            </div>

            {/* Social Media */}
            <div className="social-section">
              <h3 className="social-title">{t('followUs')}</h3>
              <div className="social-links">
                <a href="#" className="social-link" aria-label="Facebook">
                  <span>📘</span> Facebook
                </a>
                <a href="#" className="social-link" aria-label="Twitter">
                  <span>🐦</span> Twitter
                </a>
                <a href="#" className="social-link" aria-label="Instagram">
                  <span>📷</span> Instagram
                </a>
                <a href="#" className="social-link" aria-label="LinkedIn">
                  <span>💼</span> LinkedIn
                </a>
              </div>
            </div>

            {/* Map Placeholder */}
            <div className="map-placeholder">
              <div className="placeholder-content">
                <span className="placeholder-icon">🗺️</span>
                <p>{t('mapPlaceholder')}</p>
                <small>{t('addMapIntegration')}</small>
              </div>
            </div>
          </div>
        </div>
      </section>

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
            <a href="/account">{t('myAccount')}</a>
            <a href="/login">{t('login')} / {t('signup')}</a>
            <a href="/cart">Cart</a>
            <a href="/wishlist">Wishlist</a>
            <a href="/shop">Shop</a>
          </div>
          <div className="footer-section">
            <h4>{t('quickLink')}</h4>
            <a href="/privacy">{t('privacyPolicy')}</a>
            <a href="/terms">{t('termsOfUse')}</a>
            <a href="/faq">{t('faq')}</a>
            <a href="/contact">{t('contact')}</a>
          </div>
        </div>
        <div className="footer-copyright">
          <p>{t('copyright')}</p>
        </div>
      </footer>
    </div>
  );
};

export default Contact;



