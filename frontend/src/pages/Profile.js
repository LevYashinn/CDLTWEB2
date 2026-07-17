import React, { useEffect, useState } from 'react';
import { authApi } from '../api/authApi';
import './Auth.css';
import './Profile.css';

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ fullName: '', phone: '', address: '' });
  const [message, setMessage] = useState('');

  useEffect(() => {
    authApi.getProfile().then((res) => {
      setProfile(res.data);
      setForm({
        fullName: res.data.fullName || '',
        phone: res.data.phone || '',
        address: res.data.address || '',
      });
    });
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const res = await authApi.updateProfile(form);
    setProfile(res.data);
    setMessage('Cập nhật thông tin thành công!');
    setTimeout(() => setMessage(''), 2000);
  };

  if (!profile) return <p className="loading-text">Đang tải...</p>;

  return (
    <div className="auth-page">
      <form className="auth-card profile-card" onSubmit={handleSubmit}>
        <h2>Thông tin tài khoản</h2>
        {message && <div className="profile-success">{message}</div>}
        <label>Tên đăng nhập</label>
        <input value={profile.username} disabled />
        <label>Email</label>
        <input value={profile.email} disabled />
        <label>Họ và tên</label>
        <input name="fullName" value={form.fullName} onChange={handleChange} />
        <label>Số điện thoại</label>
        <input name="phone" value={form.phone} onChange={handleChange} />
        <label>Địa chỉ</label>
        <input name="address" value={form.address} onChange={handleChange} />
        <button type="submit" className="btn-primary">Lưu thay đổi</button>
      </form>
    </div>
  );
}
