import axiosClient from './axiosClient';

export const promotionApi = {
  // Admin - CRUD khuyến mãi
  getAll: () => axiosClient.get('/promotions'),
  getById: (id) => axiosClient.get(`/promotions/${id}`),
  create: (data) => axiosClient.post('/promotions', data),
  update: (id, data) => axiosClient.put(`/promotions/${id}`, data),
  remove: (id) => axiosClient.delete(`/promotions/${id}`),

  // Dùng lúc thanh toán: tính giảm giá tự động + mã coupon (nếu có nhập)
  calculate: (items, code) => axiosClient.post('/promotions/calculate', { items, code }),
};
