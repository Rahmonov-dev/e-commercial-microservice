import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productAPI, cartAPI, wishlistAPI, productVariantAPI } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import { tokenService } from '../services/tokenService';
import Navbar from '../components/Navbar';
import './ProductDetail.css';

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useLanguage();
  const { isAuthenticated } = useAuth();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [isImageFullscreen, setIsImageFullscreen] = useState(false);
  const [cartItems, setCartItems] = useState([]);
  const [cartLoading, setCartLoading] = useState(false);
  const [isInWishlist, setIsInWishlist] = useState(false);
  const [wishlistItemId, setWishlistItemId] = useState(null);
  const [wishlistLoading, setWishlistLoading] = useState(false);
  const [variants, setVariants] = useState([]);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [images, setImages] = useState([]);
  const [selectedVariant, setSelectedVariant] = useState(null);

  useEffect(() => {
    const loadProduct = async () => {
      try {
        const data = await productAPI.getProductById(id);
        setProduct(data);
        
        // Load variants
        try {
          const variantsData = await productVariantAPI.getProductVariants(id);
          setVariants(variantsData || []);
          
          // Prepare images array: main product image + variant images
          const imageList = [];
          
          // Add main product image first
          if (data.imageUrl) {
            imageList.push({
              url: data.imageUrl.startsWith('http') 
                ? data.imageUrl 
                : `http://localhost:8082${data.imageUrl}`,
              type: 'product',
              label: data.name
            });
          }
          
          // Add variant images
          if (variantsData && variantsData.length > 0) {
            variantsData.forEach(variant => {
              if (variant.imageUrl) {
                imageList.push({
                  url: variant.imageUrl.startsWith('http')
                    ? variant.imageUrl
                    : `http://localhost:8082${variant.imageUrl}`,
                  type: 'variant',
                  label: `${variant.variantName}: ${variant.variantValue}`,
                  variant: variant
                });
              }
            });
          }
          
          setImages(imageList);
        } catch (variantError) {
          console.error('Error loading variants:', variantError);
          // If no variants, just use product image
          if (data.imageUrl) {
            setImages([{
              url: data.imageUrl.startsWith('http') 
                ? data.imageUrl 
                : `http://localhost:8082${data.imageUrl}`,
              type: 'product',
              label: data.name
            }]);
          }
        }
      } catch (error) {
        console.error('Error loading product:', error);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      loadProduct();
    }
  }, [id]);

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
          cartItemId: item.id, // Store cart item ID for updates
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
  }, [isAuthenticated, id]);

  // Load wishlist status
  useEffect(() => {
    const checkWishlist = async () => {
      if (!isAuthenticated || !product) {
        setIsInWishlist(false);
        setWishlistItemId(null);
        return;
      }

      try {
        const tokenPayload = tokenService.getTokenPayload();
        const userId = tokenPayload?.userId;
        
        if (!userId) return;

        const checkResult = await wishlistAPI.isProductInWishlist(userId, product.id);
        setIsInWishlist(checkResult.exists);
        setWishlistItemId(checkResult.itemId);
      } catch (error) {
        console.error('Error checking wishlist:', error);
        setIsInWishlist(false);
        setWishlistItemId(null);
      }
    };

    checkWishlist();
  }, [isAuthenticated, product]);

  // Close fullscreen on ESC key
  useEffect(() => {
    const handleEsc = (event) => {
      if (event.key === 'Escape' && isImageFullscreen) {
        setIsImageFullscreen(false);
      }
    };

    if (isImageFullscreen) {
      document.addEventListener('keydown', handleEsc);
      // Prevent body scroll when modal is open
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('keydown', handleEsc);
      document.body.style.overflow = 'unset';
    };
  }, [isImageFullscreen]);

  const handleAddToCart = async () => {
    if (!product) return;

    // Determine price - use variant price if selected, otherwise product price
    const finalPrice = selectedVariant?.price ? parseFloat(selectedVariant.price) : product.price;
    const productName = selectedVariant 
      ? `${product.name} (${selectedVariant.variantName}: ${selectedVariant.variantValue})`
      : product.name;

    if (!isAuthenticated) {
      // Fallback to localStorage for non-authenticated users
      const itemKey = selectedVariant ? `${product.id}-${selectedVariant.id}` : product.id;
      const existingItemIndex = cartItems.findIndex(item => {
        const itemKey2 = item.variantId ? `${item.id}-${item.variantId}` : item.id;
        return itemKey2 === itemKey;
      });
      
      if (existingItemIndex >= 0) {
        const updatedCart = [...cartItems];
        updatedCart[existingItemIndex].quantity += quantity;
        setCartItems(updatedCart);
        localStorage.setItem('cart', JSON.stringify(updatedCart));
        window.dispatchEvent(new Event('cartUpdated'));
      } else {
        const newCart = [...cartItems, {
          id: product.id,
          name: productName,
          price: finalPrice,
          imageUrl: product.imageUrl,
          quantity: quantity,
          variantId: selectedVariant?.id,
          variantName: selectedVariant ? `${selectedVariant.variantName}: ${selectedVariant.variantValue}` : null
        }];
        setCartItems(newCart);
        localStorage.setItem('cart', JSON.stringify(newCart));
        window.dispatchEvent(new Event('cartUpdated'));
      }
      alert(t('addedToCart') || 'Added to cart!');
      return;
    }

    setCartLoading(true);
    try {
      const tokenPayload = tokenService.getTokenPayload();
      const userId = tokenPayload?.userId;
      
      if (!userId) {
        alert(t('userNotAuthenticated') || 'User not authenticated');
        return;
      }

      // Get or create cart
      await cartAPI.getOrCreateCart(userId);

      // Check if item already exists in cart (with same variant if selected)
      const itemKey = selectedVariant ? `${product.id}-${selectedVariant.id}` : product.id;
      const existingItem = cartItems.find(item => {
        const itemKey2 = item.variantId ? `${item.id}-${item.variantId}` : item.id;
        return itemKey2 === itemKey;
      });
      
      if (existingItem && existingItem.cartItemId) {
        // Update quantity
        const newQuantity = existingItem.quantity + quantity;
        await cartAPI.updateCartItemQuantity(existingItem.cartItemId, newQuantity);
      } else {
        // Add new item
        await cartAPI.addItemToCart({
          userId: userId,
          productId: product.id,
          productName: productName,
          quantity: quantity,
          unitPrice: finalPrice,
          variantId: selectedVariant?.id, // Include variant ID if selected
        });
      }

      // Reload cart items
      const cartResponse = await cartAPI.getCartItemsByUserId(userId);
      const items = cartResponse.map(item => ({
        id: item.productId,
        name: item.productName,
        price: parseFloat(item.unitPrice),
        quantity: item.quantity,
        cartItemId: item.id,
        variantId: item.variantId,
      }));
      setCartItems(items);
      window.dispatchEvent(new Event('cartUpdated'));
      
      alert(t('addedToCart') || 'Added to cart!');
    } catch (error) {
      console.error('Error adding to cart:', error);
      alert(error.response?.data?.message || error.message || t('addToCartError') || 'Failed to add to cart');
    } finally {
      setCartLoading(false);
    }
  };

  const handleVariantSelect = (variant) => {
    // If clicking the same variant, deselect it
    if (selectedVariant?.id === variant.id) {
      setSelectedVariant(null);
      // Reset to main product image
      if (images.length > 0) {
        setCurrentImageIndex(0);
      }
    } else {
      setSelectedVariant(variant);
      // Update image to variant image if available
      if (variant.imageUrl) {
        const variantImageIndex = images.findIndex(img => 
          img.type === 'variant' && img.variant?.id === variant.id
        );
        if (variantImageIndex !== -1) {
          setCurrentImageIndex(variantImageIndex);
        }
      }
    }
  };

  const handleRemoveFromCart = async () => {
    if (!product) return;

    if (!isAuthenticated) {
      // Fallback to localStorage
      const updatedCart = cartItems.filter(item => item.id !== product.id);
      setCartItems(updatedCart);
      localStorage.setItem('cart', JSON.stringify(updatedCart));
      window.dispatchEvent(new Event('cartUpdated'));
      alert(t('removedFromCart') || 'Removed from cart!');
      return;
    }

    setCartLoading(true);
    try {
      const existingItem = cartItems.find(item => item.id === product.id);
      
      if (existingItem && existingItem.cartItemId) {
        await cartAPI.removeItemFromCart(existingItem.cartItemId);
        
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
      
      alert(t('removedFromCart') || 'Removed from cart!');
    } catch (error) {
      console.error('Error removing from cart:', error);
      alert(error.response?.data?.message || error.message || t('removeFromCartError') || 'Failed to remove from cart');
    } finally {
      setCartLoading(false);
    }
  };

  const handleToggleWishlist = async () => {
    if (!isAuthenticated) {
      alert(t('pleaseLogin') || 'Please login to add items to wishlist');
      return;
    }

    if (!product) return;

    setWishlistLoading(true);
    try {
      const tokenPayload = tokenService.getTokenPayload();
      const userId = tokenPayload?.userId;
      
      if (!userId) {
        alert(t('userNotAuthenticated') || 'User not authenticated');
        return;
      }

      if (isInWishlist && wishlistItemId) {
        // Remove from wishlist
        const wishlist = await wishlistAPI.getWishlistByUserId(userId);
        if (wishlist) {
          await wishlistAPI.removeItemFromWishlist(wishlist.id, wishlistItemId);
          setIsInWishlist(false);
          setWishlistItemId(null);
          alert(t('removedFromWishlist') || 'Removed from wishlist');
        }
      } else {
        // Get or create wishlist
        const wishlist = await wishlistAPI.getOrCreateWishlist(userId);
        
        // Add item to wishlist
        await wishlistAPI.addItemToWishlist(wishlist.id, product);
        
        // Check again to get item ID
        const checkResult = await wishlistAPI.isProductInWishlist(userId, product.id);
        setIsInWishlist(checkResult.exists);
        setWishlistItemId(checkResult.itemId);
        
        alert(t('addedToWishlist') || 'Added to wishlist');
      }
    } catch (error) {
      console.error('Error managing wishlist:', error);
      alert(error.response?.data?.message || error.message || t('wishlistError') || 'Failed to update wishlist');
    } finally {
      setWishlistLoading(false);
    }
  };

  const isInCart = cartItems.some(item => item.id === product?.id);
  const cartItem = cartItems.find(item => item.id === product?.id);
  const currentQuantity = cartItem?.quantity || 0;

  const handleIncreaseQuantity = () => {
    const stock = product?.inventory?.currentStock || 0;
    if (quantity < stock) {
      setQuantity(quantity + 1);
    }
  };

  const handleDecreaseQuantity = () => {
    if (quantity > 1) {
      setQuantity(quantity - 1);
    }
  };

  const handlePreviousImage = () => {
    if (images.length > 0) {
      setCurrentImageIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
    }
  };

  const handleNextImage = () => {
    if (images.length > 0) {
      setCurrentImageIndex((prev) => (prev === images.length - 1 ? 0 : prev + 1));
    }
  };

  const currentImage = images.length > 0 ? images[currentImageIndex] : null;
  const imageUrl = currentImage?.url || (product?.imageUrl 
    ? (product.imageUrl.startsWith('http') 
        ? product.imageUrl 
        : `http://localhost:8082${product.imageUrl}`)
    : 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAwIiBoZWlnaHQ9IjYwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNjAwIiBoZWlnaHQ9IjYwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=');

  // Determine stock - use variant stock if selected, otherwise product stock
  const stock = selectedVariant?.stockQuantity !== undefined 
    ? selectedVariant.stockQuantity 
    : (product?.inventory?.currentStock || 0);
  const isInStock = stock > 0;
  
  // Determine price - use variant price if selected, otherwise product price
  const displayPrice = selectedVariant?.price 
    ? parseFloat(selectedVariant.price) 
    : product?.price;

  if (loading) {
    return (
      <div className="product-detail-container">
        <Navbar />
        <div className="loading-container">
          <div className="loading">{t('loading')}</div>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="product-detail-container">
        <Navbar />
        <div className="error-container">
          <p>{t('productNotFound')}</p>
          <button onClick={() => navigate('/')} className="back-btn">
            {t('backToHome')}
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="product-detail-container">
      <Navbar />
      <div className="product-detail-main">
        <button onClick={() => navigate(-1)} className="back-button">
          ← {t('back')}
        </button>

        <div className="product-detail-content">
          <div className="product-image-section">
            {images.length > 0 ? (
              <div className="image-carousel">
                {images.length > 1 && (
                  <>
                    <button 
                      className="carousel-btn carousel-btn-prev"
                      onClick={handlePreviousImage}
                      aria-label="Previous image"
                    >
                      ←
                    </button>
                    <button 
                      className="carousel-btn carousel-btn-next"
                      onClick={handleNextImage}
                      aria-label="Next image"
                    >
                      →
                    </button>
                    <div className="image-indicator">
                      {currentImageIndex + 1} / {images.length}
                    </div>
                  </>
                )}
                <img 
                  src={imageUrl} 
                  alt={currentImage?.label || product.name} 
                  className="product-detail-image"
                  onClick={() => setIsImageFullscreen(true)}
                  onError={(e) => {
                    e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAwIiBoZWlnaHQ9IjYwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNjAwIiBoZWlnaHQ9IjYwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=';
                  }}
                />
                {currentImage?.label && (
                  <div className="image-label">{currentImage.label}</div>
                )}
              </div>
            ) : (
              <img 
                src={imageUrl} 
                alt={product.name} 
                className="product-detail-image"
                onClick={() => setIsImageFullscreen(true)}
                onError={(e) => {
                  e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAwIiBoZWlnaHQ9IjYwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNjAwIiBoZWlnaHQ9IjYwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=';
                }}
              />
            )}
          </div>

          <div className="product-info-section">
            <h1 className="product-detail-name">{product.name}</h1>
            
            {product.description && (
              <div className="product-description-section">
                <h3>{t('description')}</h3>
                <p className="product-detail-description">{product.description}</p>
              </div>
            )}

            <div className="product-price-section">
              <span className="product-detail-price">${displayPrice?.toFixed(2) || '0.00'}</span>
              {selectedVariant?.price && (
                <span className="price-note">({t('variantPrice') || 'Variant Price'})</span>
              )}
            </div>

            <div className="product-stock-section">
              <span className={`stock-label ${isInStock ? 'in-stock' : 'out-of-stock'}`}>
                {isInStock 
                  ? `${t('inStock')}: ${stock} ${t('items')}`
                  : t('outOfStock')}
              </span>
            </div>

            {product.sku && (
              <div className="product-sku-section">
                <span className="sku-label">{t('productCode')}:</span>
                <span className="sku-value">{product.sku}</span>
              </div>
            )}

            {product.category && (
              <div className="product-category-section">
                <span className="category-label">{t('category')}:</span>
                <span className="category-value">{product.category.name}</span>
              </div>
            )}

            {/* Variants Section */}
            {variants && variants.length > 0 && (
              <div className="product-variants-section">
                <h3 className="variants-title">{t('variants') || 'Select Variant'}</h3>
                <div className="variants-list">
                  {variants.map((variant, index) => {
                    const isSelected = selectedVariant?.id === variant.id;
                    const isVariantInStock = variant.stockQuantity > 0 && variant.status === 'ACTIVE';
                    
                    return (
                      <button
                        key={variant.id || index}
                        className={`variant-item ${isSelected ? 'selected' : ''} ${!isVariantInStock ? 'out-of-stock' : ''}`}
                        onClick={() => handleVariantSelect(variant)}
                        disabled={!isVariantInStock}
                        type="button"
                      >
                        <div className="variant-info">
                          <div className="variant-main">
                            <span className="variant-name">{variant.variantName}:</span>
                            <span className="variant-value">{variant.variantValue}</span>
                          </div>
                          <div className="variant-details">
                            {variant.price && (
                              <span className="variant-price">${parseFloat(variant.price).toFixed(2)}</span>
                            )}
                            {variant.stockQuantity !== undefined && (
                              <span className={`variant-stock ${variant.stockQuantity > 0 ? 'in-stock' : 'out-of-stock'}`}>
                                {variant.stockQuantity > 0 
                                  ? `${variant.stockQuantity} ${t('items') || 'items'}` 
                                  : t('outOfStock')}
                              </span>
                            )}
                          </div>
                        </div>
                        {isSelected && (
                          <div className="variant-selected-indicator">✓</div>
                        )}
                      </button>
                    );
                  })}
                </div>
                {selectedVariant && (
                  <div className="selected-variant-info">
                    <span className="selected-label">{t('selectedVariant') || 'Selected'}:</span>
                    <span className="selected-value">
                      {selectedVariant.variantName}: {selectedVariant.variantValue}
                    </span>
                  </div>
                )}
              </div>
            )}

            {isInStock && (
              <div className="quantity-section">
                <label>{t('quantity')}:</label>
                <div className="quantity-controls">
                  <button 
                    className="quantity-btn"
                    onClick={handleDecreaseQuantity}
                    disabled={quantity <= 1}
                  >
                    −
                  </button>
                  <span className="quantity-value">{quantity}</span>
                  <button 
                    className="quantity-btn"
                    onClick={handleIncreaseQuantity}
                    disabled={quantity >= stock}
                  >
                    +
                  </button>
                </div>
                <span className="max-quantity">({t('max')}: {stock})</span>
              </div>
            )}

            <div className="cart-actions">
              <div className="wishlist-action">
                <button
                  className={`wishlist-toggle-btn ${isInWishlist ? 'active' : ''}`}
                  onClick={handleToggleWishlist}
                  disabled={wishlistLoading}
                  title={isInWishlist ? t('removeFromWishlist') || 'Remove from wishlist' : t('addToWishlist') || 'Add to wishlist'}
                >
                  <svg 
                    width="24" 
                    height="24" 
                    viewBox="0 0 24 24" 
                    fill={isInWishlist ? '#DB4444' : 'none'} 
                    stroke={isInWishlist ? '#DB4444' : '#000'} 
                    strokeWidth="2"
                  >
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                  </svg>
                  <span>{isInWishlist ? (t('inWishlist') || 'In Wishlist') : (t('addToWishlist') || 'Add to Wishlist')}</span>
                </button>
              </div>
              {isInCart ? (
                <>
                  <div className="cart-info">
                    <span>{t('inCart')}: {currentQuantity} {t('items')}</span>
                  </div>
                  <div className="cart-buttons">
                    <button 
                      className="add-to-cart-btn"
                      onClick={handleAddToCart}
                      disabled={!isInStock}
                    >
                      {t('addMoreToCart')}
                    </button>
                    <button 
                      className="remove-from-cart-btn"
                      onClick={handleRemoveFromCart}
                    >
                      {t('removeFromCart')}
                    </button>
                  </div>
                </>
              ) : (
                <button 
                  className="add-to-cart-btn primary"
                  onClick={handleAddToCart}
                  disabled={!isInStock}
                >
                  {t('addToCart')}
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Fullscreen Image Modal */}
      {isImageFullscreen && images.length > 0 && (
        <div 
          className="fullscreen-image-modal"
          onClick={() => setIsImageFullscreen(false)}
        >
          <button 
            className="close-fullscreen-btn"
            onClick={() => setIsImageFullscreen(false)}
            aria-label="Close"
          >
            ×
          </button>
          {images.length > 1 && (
            <>
              <button 
                className="fullscreen-carousel-btn fullscreen-carousel-btn-prev"
                onClick={(e) => {
                  e.stopPropagation();
                  handlePreviousImage();
                }}
                aria-label="Previous image"
              >
                ←
              </button>
              <button 
                className="fullscreen-carousel-btn fullscreen-carousel-btn-next"
                onClick={(e) => {
                  e.stopPropagation();
                  handleNextImage();
                }}
                aria-label="Next image"
              >
                →
              </button>
              <div className="fullscreen-image-indicator">
                {currentImageIndex + 1} / {images.length}
              </div>
            </>
          )}
          <img 
            src={imageUrl} 
            alt={currentImage?.label || product.name} 
            className="fullscreen-image"
            onClick={(e) => e.stopPropagation()}
            onError={(e) => {
              e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTIwMCIgaGVpZ2h0PSIxMjAwIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxyZWN0IHdpZHRoPSIxMjAwIiBoZWlnaHQ9IjEyMDAiIGZpbGw9IiNmNWY1ZjUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjM2IiBmaWxsPSIjOTk5OTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+Tm8gSW1hZ2U8L3RleHQ+PC9zdmc+';
            }}
          />
          {currentImage?.label && (
            <div className="fullscreen-image-label">{currentImage.label}</div>
          )}
        </div>
      )}
    </div>
  );
};

export default ProductDetail;

