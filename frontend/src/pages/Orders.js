import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderApi } from '../api/orderApi';
import './Orders.css';

const STATUS_LABELS = {
  PENDING: 'Chờ xác nhận',
  CONFIRMED: 'Đã xác nhận',
  SHIPPING: 'Đang giao',
  COMPLETED: 'Hoàn thành',
  CANCELLED: 'Đã hủy',
};

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadOrders = () => {
    setLoading(true);
    orderApi.getMyOrders().then((res) => setOrders(res.data)).finally(() => setLoading(false));
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  const handleCancel = async (id) => {
    if (!window.confirm('Bạn có chắc muốn hủy đơn hàng này?')) return;
    await orderApi.cancelOrder(id);
    loadOrders();
  };

  if (loading) return <p className="loading-text">Đang tải đơn hàng...</p>;

  return (
    <div className="orders-page">
      <h2>Lịch sử đơn hàng</h2>
      {orders.length === 0 ? (
        <p className="loading-text">Bạn chưa có đơn hàng nào.</p>
      ) : (
        orders.map((order) => (
          <div className="order-card" key={order.id}>
            <div className="order-header">
              <div>
                <strong>Đơn hàng #{order.id}</strong>
                <span className={`status-badge status-${order.status.toLowerCase()}`}>
                  {STATUS_LABELS[order.status] || order.status}
                </span>
              </div>
              <span className="order-date">{new Date(order.createdAt).toLocaleString('vi-VN')}</span>
            </div>
            <div className="order-items">
              {order.items.map((item) => (
                <div key={item.productId} className="order-item-row">
                  <span>
                    {item.productName}
                    {item.size && ` (${item.size}${item.color ? ', ' + item.color : ''})`} x{item.quantity}
                  </span>
                  <span>{formatPrice(item.price * item.quantity)}</span>
                </div>
              ))}
            </div>
            <div className="order-footer">
              <span>Người nhận: {order.receiverName} - {order.receiverPhone}</span>
              <span className="order-total">Tổng: {formatPrice(order.totalAmount)}</span>
            </div>
            <div className="order-card-actions">
              <Link to={`/orders/${order.id}`} className="btn-view-order">Xem chi tiết</Link>
              {(order.status === 'PENDING' || order.status === 'CONFIRMED') && (
                <button className="btn-cancel-order" onClick={() => handleCancel(order.id)}>Hủy đơn hàng</button>
              )}
            </div>
          </div>
        ))
      )}
    </div>
  );
}
