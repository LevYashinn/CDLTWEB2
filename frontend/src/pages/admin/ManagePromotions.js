import React, { useEffect, useState } from 'react';
import { promotionApi } from '../../api/promotionApi';
import { productApi } from '../../api/productApi';

const emptyForm = {
  name: '', code: '', scope: 'ALL', categoryId: '', productId: '',
  discountPercent: 10, description: '', startDate: '', endDate: '',
  usageLimit: '', active: true,
};

// Chuyển "2026-07-01T10:00:00" (backend) <-> "2026-07-01T10:00" (input datetime-local)
const toInputDate = (v) => (v ? v.slice(0, 16) : '');

export default function ManagePromotions() {
  const [promotions, setPromotions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [products, setProducts] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const loadAll = () => {
    promotionApi.getAll().then((res) => setPromotions(res.data));
  };

  useEffect(() => {
    loadAll();
    productApi.getCategories().then((res) => setCategories(res.data));
    productApi.getAll().then((res) => setProducts(res.data));
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.name.trim() || !form.discountPercent) return;

    const payload = {
      name: form.name,
      code: form.code.trim() ? form.code.trim().toUpperCase() : null,
      scope: form.scope,
      categoryId: form.scope === 'CATEGORY' ? Number(form.categoryId) || null : null,
      productId: form.scope === 'PRODUCT' ? Number(form.productId) || null : null,
      discountPercent: Number(form.discountPercent),
      description: form.description,
      startDate: form.startDate ? form.startDate + ':00' : null,
      endDate: form.endDate ? form.endDate + ':00' : null,
      usageLimit: form.usageLimit ? Number(form.usageLimit) : null,
      active: form.active,
    };

    try {
      if (editingId) {
        await promotionApi.update(editingId, payload);
      } else {
        await promotionApi.create(payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  const handleEdit = (p) => {
    setEditingId(p.id);
    setForm({
      name: p.name || '',
      code: p.code || '',
      scope: p.scope || 'ALL',
      categoryId: p.categoryId || '',
      productId: p.productId || '',
      discountPercent: p.discountPercent,
      description: p.description || '',
      startDate: toInputDate(p.startDate),
      endDate: toInputDate(p.endDate),
      usageLimit: p.usageLimit ?? '',
      active: !!p.active,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Xóa chương trình khuyến mãi này?')) return;
    try {
      await promotionApi.remove(id);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  const scopeLabel = (p) => {
    if (p.scope === 'CATEGORY') {
      const c = categories.find((c) => c.id === p.categoryId);
      return 'Danh mục: ' + (c ? c.name : p.categoryId);
    }
    if (p.scope === 'PRODUCT') {
      const prod = products.find((x) => x.id === p.productId);
      return 'Sản phẩm: ' + (prod ? prod.name : p.productId);
    }
    return 'Toàn bộ đơn hàng';
  };

  const statusLabel = (p) => {
    if (!p.active) return 'Đã tắt';
    const now = new Date();
    if (p.startDate && new Date(p.startDate) > now) return 'Chưa bắt đầu';
    if (p.endDate && new Date(p.endDate) < now) return 'Hết hạn';
    if (p.usageLimit && p.usedCount >= p.usageLimit) return 'Hết lượt';
    return 'Đang chạy';
  };

  return (
    <div>
      <h2>Quản lý khuyến mãi</h2>
      <form className="admin-form" onSubmit={handleSubmit} style={{ display: 'grid', gap: 12, maxWidth: 640 }}>
        <label>Tên chương trình
          <input name="name" value={form.name} onChange={handleChange} required />
        </label>

        <label>Mã coupon (để trống = tự động áp dụng, không cần khách nhập mã)
          <input name="code" value={form.code} onChange={handleChange} placeholder="VD: SALE20" />
        </label>

        <label>Phạm vi áp dụng
          <select name="scope" value={form.scope} onChange={handleChange}>
            <option value="ALL">Toàn bộ đơn hàng</option>
            <option value="CATEGORY">Theo danh mục</option>
            <option value="PRODUCT">Theo 1 sản phẩm cụ thể</option>
          </select>
        </label>

        {form.scope === 'CATEGORY' && (
          <label>Chọn danh mục
            <select name="categoryId" value={form.categoryId} onChange={handleChange} required>
              <option value="">-- Chọn danh mục --</option>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </label>
        )}

        {form.scope === 'PRODUCT' && (
          <label>Chọn sản phẩm
            <select name="productId" value={form.productId} onChange={handleChange} required>
              <option value="">-- Chọn sản phẩm --</option>
              {products.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
            </select>
          </label>
        )}

        <label>Phần trăm giảm giá (%)
          <input type="number" name="discountPercent" min="1" max="100" value={form.discountPercent} onChange={handleChange} required />
        </label>

        <label>Mô tả
          <input name="description" value={form.description} onChange={handleChange} />
        </label>

        <label>Ngày bắt đầu
          <input type="datetime-local" name="startDate" value={form.startDate} onChange={handleChange} />
        </label>
        <label>Ngày kết thúc (để trống = không giới hạn)
          <input type="datetime-local" name="endDate" value={form.endDate} onChange={handleChange} />
        </label>
        <label>Giới hạn tổng số lần sử dụng (để trống = không giới hạn)
          <input type="number" name="usageLimit" min="1" value={form.usageLimit} onChange={handleChange} />
        </label>

        <label style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <input type="checkbox" name="active" checked={!!form.active} onChange={handleChange} />
          Đang hoạt động
        </label>

        <span style={{ display: 'flex', gap: 8 }}>
          <button type="submit">{editingId ? 'Cập nhật' : 'Thêm khuyến mãi'}</button>
          {editingId && <button type="button" onClick={handleCancelEdit}>Hủy</button>}
        </span>
      </form>

      <table className="admin-table" style={{ marginTop: 24 }}>
        <thead>
          <tr>
            <th>ID</th><th>Tên</th><th>Mã</th><th>Phạm vi</th><th>Giảm</th>
            <th>Lượt dùng</th><th>Trạng thái</th><th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {promotions.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.code || <em>Tự động</em>}</td>
              <td>{scopeLabel(p)}</td>
              <td>{p.discountPercent}%</td>
              <td>{p.usedCount ?? 0}{p.usageLimit ? ` / ${p.usageLimit}` : ' / ∞'}</td>
              <td>{statusLabel(p)}</td>
              <td>
                <button className="btn-danger-sm" onClick={() => handleEdit(p)} style={{ marginRight: 8 }}>Sửa</button>
                <button className="btn-danger-sm" onClick={() => handleDelete(p.id)}>Xóa</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
