import React, { useState, useEffect, useRef, useCallback } from 'react';
import { categoryAPI } from '../services/api';
import { useLanguage } from '../context/LanguageContext';
import './CategoryTree.css';

const CategoryTree = ({ onCategorySelect, refreshTrigger }) => {
  const { t } = useLanguage();
  const [categories, setCategories] = useState([]);
  const [expandedCategories, setExpandedCategories] = useState(new Set());
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const observerRef = useRef(null);
  const pageSize = 10;

  // Load categories with pagination
  const loadCategories = useCallback(async (page) => {
    setLoading(true);
    try {
      const response = await categoryAPI.getAllCategories(page, pageSize);
      const newCategories = response.content || [];
      
      if (page === 0) {
        setCategories(newCategories);
      } else {
        setCategories(prev => {
          // Merge and deduplicate
          const existingIds = new Set(prev.map(c => c.id));
          const uniqueNew = newCategories.filter(c => !existingIds.has(c.id));
          return [...prev, ...uniqueNew];
        });
      }
      
      setHasMore(!response.last);
    } catch (error) {
      console.error('Error loading categories:', error);
    } finally {
      setLoading(false);
    }
  }, [pageSize]);

  useEffect(() => {
    loadCategories(0);
  }, [loadCategories]);

  // Refresh categories when refreshTrigger changes
  useEffect(() => {
    if (refreshTrigger !== undefined) {
      setCurrentPage(0);
      loadCategories(0);
    }
  }, [refreshTrigger, loadCategories]);

  // Listen for category update events
  useEffect(() => {
    const handleCategoryUpdate = () => {
      setCurrentPage(0);
      loadCategories(0);
    };

    window.addEventListener('categoryUpdated', handleCategoryUpdate);
    return () => {
      window.removeEventListener('categoryUpdated', handleCategoryUpdate);
    };
  }, [loadCategories]);

  // Intersection Observer for infinite scroll
  useEffect(() => {
    if (!hasMore || loading) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !loading) {
          setCurrentPage(prev => {
            const nextPage = prev + 1;
            loadCategories(nextPage);
            return nextPage;
          });
        }
      },
      { threshold: 0.1 }
    );

    const currentRef = observerRef.current;
    if (currentRef) {
      observer.observe(currentRef);
    }

    return () => {
      if (currentRef) {
        observer.unobserve(currentRef);
      }
    };
  }, [hasMore, loading]);

  const toggleCategory = (categoryId) => {
    setExpandedCategories(prev => {
      const newSet = new Set(prev);
      if (newSet.has(categoryId)) {
        newSet.delete(categoryId);
      } else {
        newSet.add(categoryId);
      }
      return newSet;
    });
  };

  const handleCategoryClick = (category) => {
    if (onCategorySelect) {
      onCategorySelect(category);
    }
  };

  const renderCategory = (category, level = 0) => {
    const isExpanded = expandedCategories.has(category.id);
    // Backend'dan kelgan ma'lumotda subCategories bor
    const hasChildren = category.subCategories && category.subCategories.length > 0;

    return (
      <div key={category.id} className="category-tree-item">
        <div
          className={`category-tree-node ${hasChildren ? 'has-children' : ''}`}
          style={{ paddingLeft: `${level * 24}px` }}
        >
          {hasChildren && (
            <button
              className="category-toggle-btn"
              onClick={(e) => {
                e.stopPropagation();
                toggleCategory(category.id);
              }}
            >
              <span className={`toggle-icon ${isExpanded ? 'expanded' : ''}`}>
                ▶
              </span>
            </button>
          )}
          {!hasChildren && <span className="category-spacer" />}
          <div
            className="category-content"
            onClick={() => handleCategoryClick(category)}
          >
            <span className="category-name">{category.name}</span>
            {category.description && (
              <span className="category-description">{category.description}</span>
            )}
          </div>
        </div>
        {hasChildren && isExpanded && (
          <div className="category-children">
            {category.subCategories.map(child => renderCategory(child, level + 1))}
          </div>
        )}
      </div>
    );
  };

  // Backend'dan kelgan kategoriyalar allaqachon hierarchical strukturada (subCategories bilan)
  // Faqat root kategoriyalarni (parentId === null) ko'rsatamiz
  const rootCategories = categories.filter(cat => !cat.parentId || cat.parentId === null);

  return (
    <div className="category-tree-container">
      <h2 className="category-tree-title">{t('categories')}</h2>
      <div className="category-tree">
        {rootCategories.length === 0 && !loading ? (
          <div className="no-categories">{t('noCategories')}</div>
        ) : (
          <>
            {rootCategories.map(category => renderCategory(category, 0))}
            {hasMore && (
              <div ref={observerRef} className="category-load-more">
                {loading && <div className="loading-spinner">{t('loading')}</div>}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default CategoryTree;

