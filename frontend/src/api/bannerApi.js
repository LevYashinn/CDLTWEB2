import axiosClient, { uploadClient } from './axiosClient';

export const bannerApi = {
  // Public - trang chủ: chỉ banner đang bật
  getActiveBanners: () => axiosClient.get('/banners'),

  // Admin - trang quản trị: tất cả banner (kể cả đang ẩn)
  getAllBanners: () => axiosClient.get('/banners/admin'),

  getById: (id) => axiosClient.get(`/banners/${id}`),
  create: (data) => axiosClient.post('/banners', data),
  update: (id, data) => axiosClient.put(`/banners/${id}`, data),
  remove: (id) => axiosClient.delete(`/banners/${id}`),

  // Upload ảnh banner - trả về { url: "/uploads/banners/xxx.jpg" }
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return uploadClient.post('/banners/upload', formData);
  },
};
