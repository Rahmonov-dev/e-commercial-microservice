import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import CategoryTree from '../components/CategoryTree';
import ProductCard from '../components/ProductCard';
import { productAPI, wishlistAPI } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import { tokenService } from '../services/tokenService';
import './Home.css';

const Home = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [products, setProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [productsLoading, setProductsLoading] = useState(false);
  const [wishlistItems, setWishlistItems] = useState([]);
  const [wishlistLoading, setWishlistLoading] = useState(false);

  const pageSize = 20;

  // Load products
  useEffect(() => {
    const loadProducts = async () => {
      setProductsLoading(true);
      try {
        let response;
        if (searchTerm) {
          response = await productAPI.searchProducts(searchTerm, currentPage, pageSize);
        } else if (selectedCategory) {
          response = await productAPI.getProductsByCategory(selectedCategory.id, currentPage, pageSize);
        } else {
          response = await productAPI.getAllProducts(currentPage, pageSize);
        }
        
        console.log('API Response:', response);
        const productsList = response.content || response || [];
        console.log('Products list:', productsList);
        if (productsList.length > 0) {
          console.log('First product:', productsList[0]);
        }
        setProducts(productsList);
        setTotalPages(response.totalPages || 0);
      } catch (error) {
        console.error('Error loading products:', error);
        setProducts([]);
      } finally {
        setProductsLoading(false);
        setLoading(false);
      }
    };

    loadProducts();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, selectedCategory, searchTerm]);

  const handleSearch = (e) => {
    e.preventDefault();
    setCurrentPage(0);
    setSelectedCategory(null);
  };

  const handleCategoryClick = (category) => {
    setSelectedCategory(category);
    setCurrentPage(0);
    setSearchTerm('');
  };

  const handleClearFilter = () => {
    setSelectedCategory(null);
    setSearchTerm('');
    setCurrentPage(0);
  };

  // Load wishlist items
  useEffect(() => {
    const loadWishlist = async () => {
      if (!isAuthenticated) {
        setWishlistItems([]);
        return;
      }

      try {
        const tokenPayload = tokenService.getTokenPayload();
        const userId = tokenPayload?.userId;
        
        if (!userId) return;

        const wishlist = await wishlistAPI.getWishlistByUserId(userId);
        if (wishlist && wishlist.wishlistItems) {
          setWishlistItems(wishlist.wishlistItems);
        }
      } catch (error) {
        console.error('Error loading wishlist:', error);
        setWishlistItems([]);
      }
    };

    loadWishlist();
  }, [isAuthenticated]);

  const handleAddToCart = (product) => {
    // TODO: Implement add to cart functionality
    console.log('Add to cart:', product);
  };

  const handleAddToWishlist = async (product) => {
    if (!isAuthenticated) {
      alert(t('pleaseLogin') || 'Please login to add items to wishlist');
      return;
    }

    setWishlistLoading(true);
    try {
      const tokenPayload = tokenService.getTokenPayload();
      const userId = tokenPayload?.userId;
      
      if (!userId) {
        alert(t('userNotAuthenticated') || 'User not authenticated');
        return;
      }

      // Check if product is already in wishlist
      const checkResult = await wishlistAPI.isProductInWishlist(userId, product.id);
      
      if (checkResult.exists && checkResult.itemId) {
        // Remove from wishlist
        const wishlist = await wishlistAPI.getWishlistByUserId(userId);
        if (wishlist) {
          await wishlistAPI.removeItemFromWishlist(wishlist.id, checkResult.itemId);
          // Update local state
          setWishlistItems(prev => prev.filter(item => item.productId !== product.id));
          alert(t('removedFromWishlist') || 'Removed from wishlist');
        }
      } else {
        // Get or create wishlist
        const wishlist = await wishlistAPI.getOrCreateWishlist(userId);
        
        // Add item to wishlist
        await wishlistAPI.addItemToWishlist(wishlist.id, product);
        
        // Reload wishlist items
        const updatedWishlist = await wishlistAPI.getWishlistByUserId(userId);
        if (updatedWishlist && updatedWishlist.wishlistItems) {
          setWishlistItems(updatedWishlist.wishlistItems);
        }
        
        alert(t('addedToWishlist') || 'Added to wishlist');
      }
    } catch (error) {
      console.error('Error managing wishlist:', error);
      alert(error.response?.data?.message || error.message || t('wishlistError') || 'Failed to update wishlist');
    } finally {
      setWishlistLoading(false);
    }
  };

  // Check if product is in wishlist
  const isProductInWishlist = (productId) => {
    return wishlistItems.some(item => item.productId === productId);
  };

  const handleProductClick = (product) => {
    navigate(`/product/${product.id}`);
  };

  const handlePreviousPage = () => {
    if (currentPage > 0) {
      setCurrentPage(currentPage - 1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      setCurrentPage(currentPage + 1);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  return (
    <div className="home-container">
      <Navbar />
      
      <div className="home-main">
        {/* Search Section */}
        <div className="search-section">
          <form className="search-form-main" onSubmit={handleSearch}>
            <input
              type="text"
              className="search-input-main"
              placeholder={t('search')}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button type="submit" className="search-btn">
              {t('search')}
            </button>
          </form>
        </div>

        {/* Categories Section */}
        <div className="categories-section">
          <CategoryTree onCategorySelect={handleCategoryClick} />
          {selectedCategory && (
            <div className="selected-category-info">
              <span>{t('categories')}: {selectedCategory.name}</span>
              <button className="clear-filter-btn" onClick={handleClearFilter}>
                {t('clearFilter')}
              </button>
            </div>
          )}
        </div>

        {/* Products Section */}
        <div className="products-section">
          <h2 className="section-title">{t('products')}</h2>
          {loading || productsLoading ? (
            <div className="loading">{t('loading')}</div>
          ) : products.length === 0 ? (
            <div className="no-products">
              <p>{t('noProducts')}</p>
            </div>
          ) : (
            <>
              <div className="products-grid">
                {products.map((product) => (
                  <ProductCard
                    key={product.id}
                    product={product}
                    onAddToCart={handleAddToCart}
                    onAddToWishlist={handleAddToWishlist}
                    onClick={handleProductClick}
                    isInWishlist={isProductInWishlist(product.id)}
                  />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="pagination">
                  <button
                    className="pagination-btn"
                    onClick={handlePreviousPage}
                    disabled={currentPage === 0}
                  >
                    {t('previous')}
                  </button>
                  <span className="pagination-info">
                    {t('page')} {currentPage + 1} {t('of')} {totalPages}
                  </span>
                  <button
                    className="pagination-btn"
                    onClick={handleNextPage}
                    disabled={currentPage >= totalPages - 1}
                  >
                    {t('next')}
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default Home;
