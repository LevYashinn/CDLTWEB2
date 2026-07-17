import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import './OrderDetail.css';

const STATUS_LABELS = {
  PENDING: 'Chờ xác nhận',
  CONFIRMED: 'Đã xác nhận',
  SHIPPING: 'Đang giao',
  COMPLETED: 'Hoàn thành',
  CANCELLED: 'Đã hủy',
};

const STATUS_STEPS = ['PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED'];

const PAYMENT_LABELS = {
  COD: 'Thanh toán khi nhận hàng (COD)',
  ONLINE: 'Thanh toán trực tuyến',
};

export default function OrderDetail() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadOrder = () => {
    setLoading(true);
    setError('');
    orderApi.getById(id)
      .then((res) => setOrder(res.data))
      .catch(() => setError('Không tìm thấy đơn hàng này hoặc bạn không có quyền xem.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadOrder();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  const formatDateTime = (dateStr) => {
    if (!dateStr) return '';
    try {
      return new Date(dateStr).toLocaleString('vi-VN');
    } catch {
      return '';
    }
  };

  const handleCancel = async () => {
    if (!window.confirm('Bạn có chắc muốn hủy đơn hàng này?')) return;
    await orderApi.cancelOrder(id);
    loadOrder();
  };

  if (loading) return <p className="loading-text">Đang tải chi tiết đơn hàng...</p>;

  if (error || !order) {
    return (
      <div className="order-detail-page">
        <p className="loading-text">{error || 'Không tìm thấy đơn hàng này.'}</p>
        <Link to="/orders" className="btn-primary-inline">← Về lịch sử đơn hàng</Link>
      </div>
    );
  }

  const isCancelled = order.status === 'CANCELLED';
  const currentStepIndex = STATUS_STEPS.indexOf(order.status);
  const subtotal = order.items.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <div className="order-detail-page">
      <Link to="/orders" className="order-detail-back">← Về lịch sử đơn hàng</Link>

      <div className="order-detail-title-row">
        <div>
          <h2>Đơn hàng #{order.id}</h2>
          <span className="order-detail-date">Đặt lúc {formatDateTime(order.createdAt)}</span>
        </div>
        <span className={`status-badge status-${order.status.toLowerCase()}`}>
          {STATUS_LABELS[order.status] || order.status}
        </span>
      </div>

      {!isCancelled ? (
        <div className="order-progress">
          {STATUS_STEPS.map((step, idx) => (
            <React.Fragment key={step}>
              <div className={`order-progress-step ${idx <= currentStepIndex ? 'done' : ''}`}>
                <span className="order-progress-dot" />
                <span className="order-progress-label">{STATUS_LABELS[step]}</span>
              </div>
              {idx < STATUS_STEPS.length - 1 && (
                <div className={`order-progress-line ${idx < currentStepIndex ? 'done' : ''}`} />
              )}
            </React.Fragment>
          ))}
        </div>
      ) : (
        <div className="order-cancelled-banner">Đơn hàng này đã bị hủy.</div>
      )}

      <div className="order-detail-grid">
        <div className="order-detail-main">
          <div className="order-detail-card">
            <h3>Sản phẩm đã đặt</h3>
            <div className="order-detail-items">
              {order.items.map((item) => (
                <div className="order-detail-item" key={`${item.productId}-${item.size}-${item.color}`}>
                  {/* 👇 KHỐI HÌNH ẢNH SẢN PHẨM 👇 */}
                  <div className="order-detail-item-image-wrapper">
                    <img 
                      src={item.imageUrl || '/default-placeholder.png'} 
                      alt={item.productName} 
                      className="order-detail-item-image"
                    />
                  </div>
                  <div className="order-detail-item-info">
                    <p className="order-detail-item-name">{item.productName}</p>
                    {(item.size || item.color) && (
                      <p className="order-detail-item-variant">
                        {item.size && `Size: ${item.size}`}
                        {item.size && item.color && '  •  '}
                        {item.color && `Màu: ${item.color}`}
                      </p>
                    )}
                    <p className="order-detail-item-qty">Số lượng: {item.quantity}</p>
                  </div>
                  <div className="order-detail-item-price">
                    <p>{formatPrice(item.price)}</p>
                    <p className="order-detail-item-line-total">{formatPrice(item.price * item.quantity)}</p>
                  </div>
                </div>
              ))}
            </div>

            <div className="order-detail-totals">
              <div className="order-detail-total-row">
                <span>Tạm tính</span>
                <span>{formatPrice(subtotal)}</span>
              </div>
              {order.discountAmount > 0 && (
                <div className="order-detail-total-row">
                  <span>Giảm giá{order.promoCode ? ` (mã ${order.promoCode})` : ''}</span>
                  <span>-{formatPrice(order.discountAmount)}</span>
                </div>
              )}
              <div className="order-detail-total-row grand-total">
                <span>Tổng cộng</span>
                <span>{formatPrice(order.totalAmount)}</span>
              </div>
            </div>
          </div>
        </div>

        <div className="order-detail-side">
          <div className="order-detail-card">
            <h3>Thông tin giao hàng</h3>
            <dl className="order-detail-facts">
              <dt>Người nhận</dt>
              <dd>{order.receiverName}</dd>
              <dt>Số điện thoại</dt>
              <dd>{order.receiverPhone}</dd>
              <dt>Địa chỉ</dt>
              <dd>{order.shippingAddress}</dd>
            </dl>
          </div>

          <div className="order-detail-card">
            <h3>Thanh toán</h3>
            <dl className="order-detail-facts">
              <dt>Phương thức</dt>
              <dd>{PAYMENT_LABELS[order.paymentMethod] || order.paymentMethod}</dd>
            </dl>
          </div>

          {(order.status === 'PENDING' || order.status === 'CONFIRMED') && (
            <button className="btn-cancel-order full-width" onClick={handleCancel}>
              Hủy đơn hàng
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
