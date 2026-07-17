import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productApi } from '../api/productApi';
import { resolveMediaUrl } from '../api/axiosClient';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import './ProductDetail.css';

export default function ProductDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [size, setSize] = useState('');
  const [color, setColor] = useState('');
  const [message, setMessage] = useState('');
  const { addToCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    productApi.getById(id).then((res) => setProduct(res.data));
  }, [id]);

  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  const sizeOptions = (product?.sizes || '').split(',').map((s) => s.trim()).filter(Boolean);
  const colorOptions = (product?.colors || '').split(',').map((c) => c.trim()).filter(Boolean);

  const handleAddToCart = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    await addToCart(product.id, quantity, size, color);
    setMessage('Đã thêm vào giỏ hàng!');
    setTimeout(() => setMessage(''), 2000);
  };

  if (!product) return <p className="loading-text">Đang tải...</p>;

  return (
    <div className="product-detail-page">
      <div className="product-detail-image">
        {product.imageUrl ? <img src={resolveMediaUrl(product.imageUrl)} alt={product.name} /> : (
          <div className="placeholder">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.2" width="72" height="72" style={{ opacity: 0.35, color: 'var(--ink-faint)' }}>
              <path d="M8 4l-4 3 2 3h2v10h8V10h2l2-3-4-3-2 2h-2l-2-2z" strokeLinejoin="round" />
            </svg>
          </div>
        )}
      </div>
      <div className="product-detail-info">
        <h1>{product.name}</h1>
        <p className="bd-brand">Thương hiệu: {product.brandName || 'Đang cập nhật'}</p>
        <p className="bd-meta">
          Danh mục: {product.categoryName || 'Chưa phân loại'}
          {product.material && ` | Chất liệu: ${product.material}`}
          {product.gender && ` | ${product.gender}`}
        </p>
        <p className="bd-price">{formatPrice(product.price)}</p>
        <p className="bd-stock">
          {product.stock > 0 ? `Còn ${product.stock} sản phẩm trong kho` : 'Hiện đã hết hàng'}
        </p>
        <p className="bd-description">{product.description || 'Chưa có mô tả cho sản phẩm này.'}</p>

        {sizeOptions.length > 0 && (
          <div className="bd-options">
            <span className="bd-options-label">Size:</span>
            {sizeOptions.map((s) => (
              <button
                key={s}
                type="button"
                className={`bd-option-btn ${size === s ? 'active' : ''}`}
                onClick={() => setSize(s)}
              >
                {s}
              </button>
            ))}
          </div>
        )}

        {colorOptions.length > 0 && (
          <div className="bd-options">
            <span className="bd-options-label">Màu:</span>
            {colorOptions.map((c) => (
              <button
                key={c}
                type="button"
                className={`bd-option-btn ${color === c ? 'active' : ''}`}
                onClick={() => setColor(c)}
              >
                {c}
              </button>
            ))}
          </div>
        )}

        {product.stock > 0 && (
          <div className="bd-actions">
            <input
              type="number"
              min="1"
              max={product.stock}
              value={quantity}
              onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))}
            />
            <button className="btn-add-cart" onClick={handleAddToCart}>Thêm vào giỏ hàng</button>
          </div>
        )}
        {message && <p className="bd-success">{message}</p>}
      </div>
    </div>
  );
}
