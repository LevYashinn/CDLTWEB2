import axiosClient, { uploadClient } from './axiosClient';

export const postApi = {
  // Public - trang chủ: N bài viết mới nhất (mặc định 3)
  getLatest: (limit = 3) => axiosClient.get('/posts/latest', { params: { limit } }),

  // Public - danh sách đầy đủ bài đã đăng
  getAllPublished: () => axiosClient.get('/posts'),

  // Admin - tất cả bài viết (kể cả bản nháp)
  getAllForAdmin: () => axiosClient.get('/posts/admin'),

  getById: (id) => axiosClient.get(`/posts/${id}`),
  create: (data) => axiosClient.post('/posts', data),
  update: (id, data) => axiosClient.put(`/posts/${id}`, data),
  remove: (id) => axiosClient.delete(`/posts/${id}`),

  // Upload ảnh bài viết - trả về { url: "/uploads/posts/xxx.jpg" }
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return uploadClient.post('/posts/upload', formData);
  },
};
