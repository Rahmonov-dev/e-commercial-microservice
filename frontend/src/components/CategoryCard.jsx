import React from 'react';
import './CategoryCard.css';

const CategoryCard = ({ category, onClick }) => {
  const handleClick = () => {
    if (onClick) {
      onClick(category);
    }
  };

  return (
    <div className="category-card" onClick={handleClick}>
      <div className="category-icon">
        {category.name?.charAt(0).toUpperCase() || '?'}
      </div>
      <h3 className="category-name">{category.name || 'Unnamed Category'}</h3>
      {category.description && (
        <p className="category-description">
          {category.description.length > 50 
            ? `${category.description.substring(0, 50)}...` 
            : category.description}
        </p>
      )}
    </div>
  );
};

export default CategoryCard;


