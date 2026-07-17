import React, { useEffect, useState } from 'react';
import { orderApi } from '../../api/orderApi';

const STATUS_OPTIONS = ['PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED', 'CANCELLED'];

export default function ManageOrders() {
  const [orders, setOrders] = useState([]);

  const loadOrders = () => orderApi.getAllOrders().then((res) => setOrders(res.data));

  useEffect(() => { loadOrders(); }, []);

  const handleStatusChange = async (orderId, status) => {
    await orderApi.updateStatus(orderId, status);
    loadOrders();
  };

  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  return (
    <div>
      <h2>Quản lý đơn hàng</h2>
      <table className="admin-table">
        <thead>
          <tr><th>Mã ĐH</th><th>Khách hàng</th><th>Tổng tiền</th><th>Thanh toán</th><th>Trạng thái</th><th>Ngày đặt</th></tr>
        </thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>#{o.id}</td>
              <td>{o.receiverName}<br /><small>{o.receiverPhone}</small></td>
              <td>{formatPrice(o.totalAmount)}</td>
              <td>{o.paymentMethod}</td>
              <td>
                <select value={o.status} onChange={(e) => handleStatusChange(o.id, e.target.value)}>
                  {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
                </select>
              </td>
              <td>{new Date(o.createdAt).toLocaleDateString('vi-VN')}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
