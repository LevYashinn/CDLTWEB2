import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { resolveMediaUrl } from '../api/axiosClient';
import './Cart.css';

export default function Cart() {
  const { cart, updateQuantity, removeItem } = useCart();
  const navigate = useNavigate();

  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  if (!cart.items || cart.items.length === 0) {
    return (
      <div className="cart-page">
        <h2>Giỏ hàng của bạn</h2>
        <p className="empty-cart">Giỏ hàng đang trống.</p>
        <Link to="/" className="btn-primary-inline">Tiếp tục mua sắm</Link>
      </div>
    );
  }

  // 👇 Quét toàn bộ giỏ hàng xem có món nào bị quá số lượng tồn kho không
  const hasOutOfStockItems = cart.items.some(
    (item) => item.stock !== undefined && (item.stock === 0 || item.quantity > item.stock)
  );

  return (
    <div className="cart-page">
      <h2>Giỏ hàng của bạn</h2>
      <div className="cart-list">
        {cart.items.map((item) => {
          // 👇 Phân loại trạng thái lỗi tồn kho cho từng sản phẩm
          const isOutOfStock = item.stock !== undefined && (item.stock === 0 || item.quantity > item.stock);
          const isZeroStock = item.stock === 0;

          return (
            <div className={`cart-item ${isOutOfStock ? 'out-of-stock-item' : ''}`} key={item.productId}>
              <div className="cart-item-image">
                {item.imageUrl ? <img src={resolveMediaUrl(item.imageUrl)} alt={item.name} /> : <div className="placeholder">👕</div>}
              </div>
              
              <div className="cart-item-info">
                <h4>{item.name}</h4>
                {(item.size || item.color) && (
                  <p className="cart-item-variant">
                    {item.size && `Size: ${item.size}`}{item.size && item.color && ' • '}{item.color && `Màu: ${item.color}`}
                  </p>
                )}
                <p>{formatPrice(item.price)}</p>
                
                {/* 👇 Dòng cảnh báo hết hàng màu đỏ 👇 */}
                {isOutOfStock && (
                  <p className="out-of-stock-msg">
                    {isZeroStock ? '❌ Sản phẩm này hiện đã hết hàng' : `❌ Chỉ còn ${item.stock} sản phẩm trong kho`}
                  </p>
                )}
              </div>

              <div className="cart-item-qty">
                {/* Khóa nút giảm nếu kho = 0 (bắt khách phải bấm nút X để xóa) */}
                <button onClick={() => updateQuantity(item.productId, item.quantity - 1)} disabled={isZeroStock}>-</button>
                <span>{item.quantity}</span>
                {/* Khóa nút tăng nếu số lượng khách chọn đã chạm trần tồn kho */}
                <button onClick={() => updateQuantity(item.productId, item.quantity + 1)} disabled={item.stock !== undefined && item.quantity >= item.stock}>+</button>
              </div>

              <div className="cart-item-total">{formatPrice(item.price * item.quantity)}</div>
              <button className="cart-item-remove" onClick={() => removeItem(item.productId)}>✕</button>
            </div>
          );
        })}
      </div>

      <div className="cart-summary">
        <div className="cart-summary-row">
          <span>Tổng cộng ({cart.totalItems} sản phẩm)</span>
          <span className="cart-total-amount">{formatPrice(cart.totalAmount)}</span>
        </div>
        
        {/* 👇 Nút thanh toán sẽ bị mờ và không bấm được nếu có lỗi tồn kho 👇 */}
        <button 
          className="btn-checkout" 
          onClick={() => navigate('/checkout')}
          disabled={hasOutOfStockItems}
        >
          {hasOutOfStockItems ? 'Vui lòng cập nhật sản phẩm hết hàng' : 'Tiến hành thanh toán'}
        </button>
      </div>
    </div>
  );
}