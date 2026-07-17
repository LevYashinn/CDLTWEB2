import React, { useRef, useState } from 'react';
import { resolveMediaUrl } from '../api/axiosClient';
import './ImageUploader.css';

/**
 * Ô chọn/upload ảnh dùng chung cho các trang quản trị (Sản phẩm, Banner, Bài viết).
 * Thay cho việc gõ tay URL ảnh: người dùng chọn file trên máy, component tự upload
 * lên backend rồi trả URL về qua onChange.
 *
 * Props:
 *  - value: URL ảnh hiện tại (tương đối, vd "/uploads/products/xxx.jpg")
 *  - onChange(url): gọi khi upload xong, url mới sẽ được lưu vào state form của trang cha
 *  - uploadFn(file): hàm gọi API upload, trả về promise với response.data = { url }
 *  - label: nhãn hiển thị (tùy chọn)
 */
export default function ImageUploader({ value, onChange, uploadFn, label = 'Ảnh' }) {
  const inputRef = useRef(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');

  const handlePick = () => inputRef.current?.click();

  const handleFileChange = async (e) => {
    const file = e.target.files?.[0];
    e.target.value = ''; // cho phép chọn lại cùng 1 file lần nữa nếu cần
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      setError('Vui lòng chọn 1 file ảnh (jpg, png, webp...)');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setError('Ảnh vượt quá 5MB, vui lòng chọn ảnh nhỏ hơn.');
      return;
    }

    setError('');
    setUploading(true);
    try {
      const res = await uploadFn(file);
      onChange(res.data.url);
    } catch (err) {
      setError(err.response?.data?.message || 'Upload ảnh thất bại, vui lòng thử lại.');
    } finally {
      setUploading(false);
    }
  };

  const handleRemove = () => {
    onChange('');
    setError('');
  };

  return (
    <div className="image-uploader">
      <label className="image-uploader__label">{label}</label>

      <div className="image-uploader__body">
        {value ? (
          <div className="image-uploader__preview">
            <img src={resolveMediaUrl(value)} alt="Xem trước" />
            <button type="button" className="image-uploader__remove" onClick={handleRemove} title="Xóa ảnh">
              ✕
            </button>
          </div>
        ) : (
          <div className="image-uploader__placeholder" onClick={handlePick}>
            <span>+ Chọn ảnh</span>
          </div>
        )}

        <div className="image-uploader__actions">
          <button type="button" onClick={handlePick} disabled={uploading}>
            {uploading ? 'Đang tải lên...' : value ? 'Đổi ảnh khác' : 'Chọn ảnh từ máy'}
          </button>
          <input
            ref={inputRef}
            type="file"
            accept="image/*"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />
        </div>
      </div>

      {error && <p className="image-uploader__error">{error}</p>}
    </div>
  );
}
