import React from 'react';
import { Link } from 'react-router-dom';
import { resolveMediaUrl } from '../api/axiosClient';
import './ProductCard.css';

export default function ProductCard({ product }) {
  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  return (
    <Link to={`/products/${product.id}`} className="product-card">
      <div className="product-cover">
        {product.imageUrl ? (
          <img src={resolveMediaUrl(product.imageUrl)} alt={product.name} />
        ) : (
          <div className="product-cover-placeholder">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.3" width="44" height="44">
              <path d="M8 4l-4 3 2 3h2v10h8V10h2l2-3-4-3-2 2h-2l-2-2z" strokeLinejoin="round" />
            </svg>
          </div>
        )}
      </div>
      <div className="product-info">
        <h3 className="product-title">{product.name}</h3>
        <p className="product-brand">{product.brandName || 'Đang cập nhật'}</p>
        <p className="product-price">{formatPrice(product.price)}</p>
        {product.stock === 0 && <span className="badge-out-of-stock">Hết hàng</span>}
      </div>
    </Link>
  );
}
