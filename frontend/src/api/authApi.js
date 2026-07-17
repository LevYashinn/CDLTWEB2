import axiosClient from './axiosClient';

export const authApi = {
  register: (data) => axiosClient.post('/users/register', data),
  login: (data) => axiosClient.post('/users/login', data),
  getProfile: () => axiosClient.get('/users/profile'),
  updateProfile: (data) => axiosClient.put('/users/profile', data),
};
