import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import './Admin.css';

export default function AdminLayout() {
  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <h3>Quản trị</h3>
        <NavLink to="/admin" end>Dashboard</NavLink>
        <NavLink to="/admin/products">Quản lý sản phẩm</NavLink>
        <NavLink to="/admin/categories">Danh mục</NavLink>
        <NavLink to="/admin/brands">Thương hiệu</NavLink>
        <NavLink to="/admin/banners">Banner</NavLink>
        <NavLink to="/admin/posts">Bài viết</NavLink>
        <NavLink to="/admin/promotions">Khuyến mãi</NavLink>
        <NavLink to="/admin/users">Người dùng</NavLink>
        <NavLink to="/admin/orders">Đơn hàng</NavLink>
      </aside>
      <div className="admin-content">
        <Outlet />
      </div>
    </div>
  );
}
