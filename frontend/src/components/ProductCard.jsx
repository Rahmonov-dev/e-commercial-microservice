import React from 'react';
import './ProductCard.css';

const ProductCard = ({ product, onAddToCart, onAddToWishlist, onClick, isInWishlist = false }) => {
  const handleAddToCart = (e) => {
    e.stopPropagation();
    if (onAddToCart) {
      onAddToCart(product);
    }
  };

  const handleAddToWishlist = (e) => {
    e.stopPropagation();
    if (onAddToWishlist) {
      onAddToWishlist(product);
    }
  };

  const handleCardClick = () => {
    if (onClick) {
      onClick(product);
    }
  };

  if (!product) {
    console.warn('ProductCard: product is null or undefined');
    return null;
  }

  // Try multiple possible image field names
  const imageUrl = product.imageUrl || product.image || product.imagePath || '';
  const finalImageUrl = imageUrl 
    ? (imageUrl.startsWith('http') 
        ? imageUrl 
        : `http://localhost:8082${imageUrl}`)
    : 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTgiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=';

  // Try multiple possible name field names
  const productName = product.name || product.productName || product.title || 'Noma\'lum mahsulot';
  
  // Try multiple possible price field names
  const priceValue = product.price || product.productPrice || product.unitPrice || 0;
  const productPrice = priceValue 
    ? (typeof priceValue === 'number' 
        ? priceValue.toFixed(2) 
        : parseFloat(priceValue).toFixed(2))
    : '0.00';

  return (
    <div className="product-card" onClick={handleCardClick}>
      <div className="product-image-container">
        <img 
          src={finalImageUrl} 
          alt={productName} 
          className="product-image"
          onError={(e) => {
            e.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTgiIGZpbGw9IiM5OTk5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5ObyBJbWFnZTwvdGV4dD48L3N2Zz4=';
          }}
        />
        {onAddToWishlist && (
          <button
            className={`wishlist-btn ${isInWishlist ? 'active' : ''}`}
            onClick={handleAddToWishlist}
            aria-label={isInWishlist ? 'Remove from wishlist' : 'Add to wishlist'}
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
          </button>
        )}
      </div>
      <div className="product-info">
        <h3 className="product-name">{productName}</h3>
        <div className="product-price">${productPrice}</div>
      </div>
    </div>
  );
};

export default ProductCard;


