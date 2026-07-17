import React, { useEffect, useState } from 'react';
import { postApi } from '../../api/postApi';
import { resolveMediaUrl } from '../../api/axiosClient';
import ImageUploader from '../../components/ImageUploader';

const emptyForm = { title: '', summary: '', content: '', imageUrl: '', author: '', published: true };

export default function ManagePosts() {
  const [posts, setPosts] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const loadAll = () => {
    postApi.getAllForAdmin().then((res) => setPosts(res.data));
  };

  useEffect(() => { loadAll(); }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title.trim() || !form.content.trim()) return;
    try {
      if (editingId) {
        await postApi.update(editingId, form);
      } else {
        await postApi.create(form);
      }
      setForm(emptyForm);
      setEditingId(null);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  const handleEdit = (post) => {
    setEditingId(post.id);
    setForm({
      title: post.title || '',
      summary: post.summary || '',
      content: post.content || '',
      imageUrl: post.imageUrl || '',
      author: post.author || '',
      published: !!post.published,
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Xóa bài viết này?')) return;
    try {
      await postApi.remove(id);
      loadAll();
    } catch (err) {
      alert(err.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại.');
    }
  };

  return (
    <div>
      <h2>Quản lý bài viết</h2>
      <form className="admin-form" onSubmit={handleSubmit} style={{ display: 'grid', gap: 12, maxWidth: 720 }}>
        <label>Tiêu đề
          <input name="title" value={form.title} onChange={handleChange} required />
        </label>
        <label>Mô tả ngắn (hiện ở trang chủ)
          <input name="summary" value={form.summary} onChange={handleChange} maxLength={500} />
        </label>
        <label>Nội dung bài viết
          <textarea name="content" value={form.content} onChange={handleChange} rows={8} required />
        </label>

        <ImageUploader
          label="Ảnh đại diện bài viết"
          value={form.imageUrl}
          onChange={(url) => setForm({ ...form, imageUrl: url })}
          uploadFn={postApi.uploadImage}
        />

        <label>Tác giả
          <input name="author" value={form.author} onChange={handleChange} placeholder="FashionStore" />
        </label>
        <label style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <input type="checkbox" name="published" checked={!!form.published} onChange={handleChange} />
          Đăng công khai (hiện ở trang chủ)
        </label>

        <span style={{ display: 'flex', gap: 8 }}>
          <button type="submit">{editingId ? 'Cập nhật' : 'Thêm bài viết'}</button>
          {editingId && <button type="button" onClick={handleCancelEdit}>Hủy</button>}
        </span>
      </form>

      <table className="admin-table" style={{ marginTop: 24 }}>
        <thead>
          <tr>
            <th>ID</th><th>Ảnh</th><th>Tiêu đề</th><th>Tác giả</th><th>Trạng thái</th><th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>
                {p.imageUrl ? (
                  <img src={resolveMediaUrl(p.imageUrl)} alt={p.title} style={{ width: 90, height: 60, objectFit: 'cover', borderRadius: 4 }} />
                ) : '—'}
              </td>
              <td>{p.title}</td>
              <td>{p.author}</td>
              <td>{p.published ? 'Đã đăng' : 'Bản nháp'}</td>
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
