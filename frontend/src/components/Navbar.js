import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="navbar-brand">FashionStore</Link>

        <nav className="navbar-links">
          <Link to="/">Trang chủ</Link>
          {isAdmin && <Link to="/admin">Quản trị</Link>}
        </nav>

        <div className="navbar-actions">
          {user ? (
            <>
              <Link to="/cart" className="cart-link">
                <svg viewBox="0 0 24 24" fill="none" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M3 4h2l2.4 12.2a2 2 0 0 0 2 1.6h7.2a2 2 0 0 0 2-1.6L20 8H6" />
                  <circle cx="9" cy="21" r="1.4" />
                  <circle cx="17" cy="21" r="1.4" />
                </svg>
                Giỏ hàng
                {cart.totalItems > 0 && <span className="cart-badge">{cart.totalItems}</span>}
              </Link>
              <Link to="/orders">Đơn hàng</Link>
              <Link to="/profile">{user.username}</Link>
              <button className="btn-link" onClick={handleLogout}>Đăng xuất</button>
            </>
          ) : (
            <>
              <Link to="/login">Đăng nhập</Link>
              <Link to="/register" className="btn-primary-sm">Đăng ký</Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
