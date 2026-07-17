import axiosClient from './axiosClient';

export const cartApi = {
  getCart: () => axiosClient.get('/cart'),
  addToCart: (data) => axiosClient.post('/cart/add', data),
  updateQuantity: (data) => axiosClient.put('/cart/update', data),
  removeItem: (productId) => axiosClient.delete(`/cart/remove/${productId}`),
  clearCart: () => axiosClient.delete('/cart/clear'),
};
