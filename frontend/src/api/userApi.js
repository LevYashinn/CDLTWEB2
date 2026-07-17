import axiosClient from './axiosClient';

export const userApi = {
  getAll: () => axiosClient.get('/users'),
  remove: (id) => axiosClient.delete(`/users/${id}`),
};
