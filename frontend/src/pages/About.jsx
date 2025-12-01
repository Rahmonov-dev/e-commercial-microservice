import React from 'react';
import Navbar from '../components/Navbar';
import { useLanguage } from '../context/LanguageContext';
import './About.css';

const About = () => {
  const { t } = useLanguage();
  
  return (
    <div className="about-container">
      <Navbar />

      {/* Hero Section */}
      <section className="about-hero">
        <div className="hero-content">
          <h1 className="hero-title">{t('aboutExclusive')}</h1>
          <p className="hero-subtitle">
            {t('aboutSubtitle')}
          </p>
        </div>
        <div className="hero-image-placeholder">
          <div className="placeholder-content">
            <span className="placeholder-icon">📸</span>
            <p>{t('heroImagePlaceholder')}</p>
            <small>{t('addHeroImage')}</small>
          </div>
        </div>
      </section>

      {/* Mission Section */}
      <section className="about-section">
        <div className="section-content">
          <div className="section-text">
            <h2 className="section-title">{t('ourMission')}</h2>
            <p className="section-description">
              At Exclusive, we are committed to providing our customers with the highest quality products 
              and exceptional shopping experiences. Our mission is to make premium products accessible to 
              everyone while maintaining the highest standards of customer service and satisfaction.
            </p>
            <p className="section-description">
              We believe in building lasting relationships with our customers, suppliers, and partners 
              through transparency, integrity, and innovation.
            </p>
          </div>
          <div className="section-image-placeholder">
            <div className="placeholder-content">
              <span className="placeholder-icon">🖼️</span>
              <p>{t('missionImage')}</p>
              <small>{t('addMissionImage')}</small>
            </div>
          </div>
        </div>
      </section>

      {/* Vision Section */}
      <section className="about-section about-section-alt">
        <div className="section-content">
          <div className="section-image-placeholder">
            <div className="placeholder-content">
              <span className="placeholder-icon">🖼️</span>
              <p>{t('visionImage')}</p>
              <small>{t('addVisionImage')}</small>
            </div>
          </div>
          <div className="section-text">
            <h2 className="section-title">{t('ourVision')}</h2>
            <p className="section-description">
              We envision a future where shopping is seamless, personalized, and enjoyable for everyone. 
              Our goal is to become the leading e-commerce platform that sets new standards in customer 
              experience, product quality, and technological innovation.
            </p>
            <p className="section-description">
              Through continuous improvement and customer-centric approach, we strive to create value 
              for all stakeholders while contributing positively to our community and environment.
            </p>
          </div>
        </div>
      </section>

      {/* Values Section */}
      <section className="values-section">
        <div className="values-container">
          <h2 className="section-title">{t('ourCoreValues')}</h2>
          <div className="values-grid">
            <div className="value-card">
              <div className="value-icon">✨</div>
              <h3 className="value-title">{t('quality')}</h3>
              <p className="value-description">
                We never compromise on quality. Every product in our catalog is carefully selected 
                and tested to meet our high standards.
              </p>
            </div>
            <div className="value-card">
              <div className="value-icon">🤝</div>
              <h3 className="value-title">{t('customerFirst')}</h3>
              <p className="value-description">
                Our customers are at the heart of everything we do. We listen, learn, and adapt 
                to provide the best possible experience.
              </p>
            </div>
            <div className="value-card">
              <div className="value-icon">🚀</div>
              <h3 className="value-title">{t('innovation')}</h3>
              <p className="value-description">
                We embrace new technologies and ideas to continuously improve our platform and 
                enhance customer satisfaction.
              </p>
            </div>
            <div className="value-card">
              <div className="value-icon">🌱</div>
              <h3 className="value-title">{t('sustainability')}</h3>
              <p className="value-description">
                We are committed to sustainable practices and environmental responsibility in 
                all aspects of our business.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Team Section */}
      <section className="team-section">
        <div className="team-container">
          <h2 className="section-title">{t('ourTeam')}</h2>
          <p className="section-subtitle">
            {t('meetTeam')}
          </p>
          <div className="team-grid">
            <div className="team-member">
              <div className="member-image-placeholder">
                <div className="placeholder-content">
                  <span className="placeholder-icon">👤</span>
                </div>
              </div>
              <h3 className="member-name">{t('teamMember')} 1</h3>
              <p className="member-role">{t('positionRole')}</p>
              <p className="member-bio">
                {t('memberBio')}
              </p>
            </div>
            <div className="team-member">
              <div className="member-image-placeholder">
                <div className="placeholder-content">
                  <span className="placeholder-icon">👤</span>
                </div>
              </div>
              <h3 className="member-name">{t('teamMember')} 2</h3>
              <p className="member-role">{t('positionRole')}</p>
              <p className="member-bio">
                {t('memberBio')}
              </p>
            </div>
            <div className="team-member">
              <div className="member-image-placeholder">
                <div className="placeholder-content">
                  <span className="placeholder-icon">👤</span>
                </div>
              </div>
              <h3 className="member-name">{t('teamMember')} 3</h3>
              <p className="member-role">{t('positionRole')}</p>
              <p className="member-bio">
                {t('memberBio')}
              </p>
            </div>
            <div className="team-member">
              <div className="member-image-placeholder">
                <div className="placeholder-content">
                  <span className="placeholder-icon">👤</span>
                </div>
              </div>
              <h3 className="member-name">{t('teamMember')} 4</h3>
              <p className="member-role">{t('positionRole')}</p>
              <p className="member-bio">
                {t('memberBio')}
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="stats-section">
        <div className="stats-container">
          <div className="stat-item">
            <div className="stat-number">10K+</div>
            <div className="stat-label">{t('happyCustomers')}</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">5K+</div>
            <div className="stat-label">{t('products')}</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">50+</div>
            <div className="stat-label">{t('categories')}</div>
          </div>
          <div className="stat-item">
            <div className="stat-number">24/7</div>
            <div className="stat-label">{t('support')}</div>
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

export default About;



