import React, { useEffect, useState } from 'react';
import { bannerApi } from '../../api/bannerApi';
import { resolveMediaUrl } from '../../api/axiosClient';
import ImageUploader from '../../components/ImageUploader';

const emptyForm = { title: '', imageUrl: '', linkUrl: '', description: '', displayOrder: 0, active: true };

export default function ManageBanners() {
  const [banners, setBanners] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const loadAll = () => {
    bannerApi.getAllBanners().then((res) => setBanners(res.data));
  };

  useEffect(() => { loadAll(); }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title.trim() || !form.imageUrl.trim()) return;
    const payload = { ...form, displayOrder: Number(form.displayOrder) || 0 };
    try {
      if (editingId) {
        await bannerApi.update(editingId, payload);
      } else {
        await bannerApi.create(payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  const handleEdit = (banner) => {
    setEditingId(banner.id);
    setForm({
      title: banner.title || '',
      imageUrl: banner.imageUrl || '',
      linkUrl: banner.linkUrl || '',
      description: banner.description || '',
      displayOrder: banner.displayOrder ?? 0,
      active: banner.active,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Xóa banner này?')) return;
    try {
      await bannerApi.remove(id);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  return (
    <div>
      <h2>Quản lý banner trang chủ</h2>
      <form className="admin-form" onSubmit={handleSubmit} style={{ display: 'grid', gap: 12, maxWidth: 640 }}>
        <label>Tiêu đề
          <input name="title" value={form.title} onChange={handleChange} required />
        </label>
        <ImageUploader
          label="Ảnh banner"
          value={form.imageUrl}
          onChange={(url) => setForm({ ...form, imageUrl: url })}
          uploadFn={bannerApi.uploadImage}
        />
        <label>Link khi bấm vào banner (tùy chọn)
          <input name="linkUrl" value={form.linkUrl} onChange={handleChange} placeholder="/products hoặc https://..." />
        </label>
        <label>Mô tả
          <input name="description" value={form.description} onChange={handleChange} />
        </label>
        <label>Thứ tự hiển thị (số nhỏ hơn hiện trước)
          <input type="number" name="displayOrder" value={form.displayOrder} onChange={handleChange} />
        </label>
        <label style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <input type="checkbox" name="active" checked={!!form.active} onChange={handleChange} />
          Hiển thị banner này ở trang chủ
        </label>

        <span style={{ display: 'flex', gap: 8 }}>
          <button type="submit">{editingId ? 'Cập nhật' : 'Thêm banner'}</button>
          {editingId && <button type="button" onClick={handleCancelEdit}>Hủy</button>}
        </span>
      </form>

      <table className="admin-table" style={{ marginTop: 24 }}>
        <thead>
          <tr>
            <th>ID</th><th>Ảnh</th><th>Tiêu đề</th><th>Thứ tự</th><th>Trạng thái</th><th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {banners.map((b) => (
            <tr key={b.id}>
              <td>{b.id}</td>
              <td><img src={resolveMediaUrl(b.imageUrl)} alt={b.title} style={{ width: 100, height: 50, objectFit: 'cover', borderRadius: 4 }} /></td>
              <td>{b.title}</td>
              <td>{b.displayOrder}</td>
              <td>{b.active ? 'Đang hiện' : 'Đang ẩn'}</td>
              <td>
                <button className="btn-danger-sm" onClick={() => handleEdit(b)} style={{ marginRight: 8 }}>Sửa</button>
                <button className="btn-danger-sm" onClick={() => handleDelete(b.id)}>Xóa</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
