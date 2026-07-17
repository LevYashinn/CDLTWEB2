import React, { useEffect, useState } from 'react';
import { productApi } from '../../api/productApi';
import { userApi } from '../../api/userApi';
import { orderApi } from '../../api/orderApi';

export default function Dashboard() {
  const [stats, setStats] = useState({ products: 0, users: 0, orders: 0, revenue: 0 });

  useEffect(() => {
    Promise.all([productApi.getAll(), userApi.getAll(), orderApi.getAllOrders()]).then(
      ([productsRes, usersRes, ordersRes]) => {
        const revenue = ordersRes.data
          .filter((o) => o.status !== 'CANCELLED')
          .reduce((sum, o) => sum + o.totalAmount, 0);
        setStats({
          products: productsRes.data.length,
          users: usersRes.data.length,
          orders: ordersRes.data.length,
          revenue,
        });
      }
    ).catch(() => {});
  }, []);

  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  return (
    <div>
      <h2>Thống kê tổng quan</h2>
      <div className="stat-grid">
        <div className="stat-card">
          <h4>Tổng số sản phẩm</h4>
          <div className="stat-value">{stats.products}</div>
        </div>
        <div className="stat-card">
          <h4>Tổng số người dùng</h4>
          <div className="stat-value">{stats.users}</div>
        </div>
        <div className="stat-card">
          <h4>Tổng số đơn hàng</h4>
          <div className="stat-value">{stats.orders}</div>
        </div>
        <div className="stat-card">
          <h4>Doanh thu</h4>
          <div className="stat-value">{formatPrice(stats.revenue)}</div>
        </div>
      </div>
    </div>
  );
}
