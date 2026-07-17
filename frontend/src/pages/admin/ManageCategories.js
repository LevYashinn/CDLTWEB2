import React, { useEffect, useState } from 'react';
import { productApi } from '../../api/productApi';

const emptyForm = { name: '', description: '' };

export default function ManageCategories() {
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const loadAll = () => {
    productApi.getCategories().then((res) => setCategories(res.data));
  };

  useEffect(() => { loadAll(); }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim()) return;
    try {
      if (editingId) {
        await productApi.updateCategory(editingId, form);
      } else {
        await productApi.createCategory(form);
      }
      setForm(emptyForm);
      setEditingId(null);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  const handleEdit = (category) => {
    setEditingId(category.id);
    setForm({ name: category.name || '', description: category.description || '' });
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Xóa danh mục này?')) return;
    try {
      await productApi.deleteCategory(id);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  return (
    <div>
      <h2>Quản lý danh mục</h2>
      <form className="admin-form" onSubmit={handleSubmit} style={{ gridTemplateColumns: '1fr 1fr auto' }}>
        <label>Tên danh mục (vd: Áo thun, Quần jean, Váy...)
          <input name="name" value={form.name} onChange={handleChange} required />
        </label>
        <label>Mô tả
          <input name="description" value={form.description} onChange={handleChange} />
        </label>
        <span style={{ display: 'flex', alignItems: 'flex-end', gap: 8 }}>
          <button type="submit">{editingId ? 'Cập nhật' : 'Thêm'}</button>
          {editingId && <button type="button" onClick={handleCancelEdit}>Hủy</button>}
        </span>
      </form>
      <table className="admin-table">
        <thead><tr><th>ID</th><th>Tên</th><th>Mô tả</th><th>Hành động</th></tr></thead>
        <tbody>
          {categories.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.name}</td>
              <td>{c.description || '—'}</td>
              <td>
                <button className="btn-danger-sm" onClick={() => handleEdit(c)} style={{ marginRight: 8 }}>Sửa</button>
                <button className="btn-danger-sm" onClick={() => handleDelete(c.id)}>Xóa</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
