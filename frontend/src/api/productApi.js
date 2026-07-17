import axiosClient, { uploadClient } from './axiosClient';

export const productApi = {
  getAll: (params) => axiosClient.get('/products', { params }),
  getById: (id) => axiosClient.get(`/products/${id}`),
  create: (data) => axiosClient.post('/products', data),
  update: (id, data) => axiosClient.put(`/products/${id}`, data),
  remove: (id) => axiosClient.delete(`/products/${id}`),

  // Upload ảnh sản phẩm - trả về { url: "/uploads/products/xxx.jpg" }
  uploadImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return uploadClient.post('/products/upload', formData);
  },

  getCategories: () => axiosClient.get('/categories'),
  createCategory: (data) => axiosClient.post('/categories', data),
  updateCategory: (id, data) => axiosClient.put(`/categories/${id}`, data),
  deleteCategory: (id) => axiosClient.delete(`/categories/${id}`),

  getBrands: () => axiosClient.get('/brands'),
  createBrand: (data) => axiosClient.post('/brands', data),
  updateBrand: (id, data) => axiosClient.put(`/brands/${id}`, data),
  deleteBrand: (id) => axiosClient.delete(`/brands/${id}`),
};
