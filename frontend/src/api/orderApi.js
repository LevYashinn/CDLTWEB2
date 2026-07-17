import axiosClient from './axiosClient';

export const orderApi = {
  createOrder: (data) => axiosClient.post('/orders', data),
  getMyOrders: () => axiosClient.get('/orders'),
  getAllOrders: () => axiosClient.get('/orders', { params: { all: true } }),
  getById: (id) => axiosClient.get(`/orders/${id}`),
  updateStatus: (orderId, status) => axiosClient.put(`/orders/status?orderId=${orderId}`, { status }),
  cancelOrder: (id) => axiosClient.put(`/orders/${id}/cancel`),
};
