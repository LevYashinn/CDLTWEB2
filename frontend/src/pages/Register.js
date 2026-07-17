import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

export default function Register() {
  const [form, setForm] = useState({
    username: '', email: '', password: '', fullName: '', phone: '', address: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng ký thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h2>Đăng ký tài khoản</h2>
        {error && <div className="auth-error">{error}</div>}
        <label>Tên đăng nhập</label>
        <input name="username" value={form.username} onChange={handleChange} required />
        <label>Email</label>
        <input name="email" type="email" value={form.email} onChange={handleChange} required />
        <label>Mật khẩu</label>
        <input name="password" type="password" value={form.password} onChange={handleChange} required />
        <label>Họ và tên</label>
        <input name="fullName" value={form.fullName} onChange={handleChange} />
        <label>Số điện thoại</label>
        <input name="phone" value={form.phone} onChange={handleChange} />
        <label>Địa chỉ</label>
        <input name="address" value={form.address} onChange={handleChange} />
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Đang xử lý...' : 'Đăng ký'}
        </button>
        <p className="auth-switch">
          Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
        </p>
      </form>
    </div>
  );
}
