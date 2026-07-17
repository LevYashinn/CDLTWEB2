import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';
// Gốc gateway (bỏ "/api" ở cuối) - dùng để ghép với đường dẫn ảnh tương đối, vd "/uploads/products/xxx.jpg"
const API_ORIGIN = API_BASE_URL.replace(/\/api\/?$/, '');

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Client riêng cho việc upload file (multipart/form-data) - không ép Content-Type: application/json,
// để trình duyệt tự set đúng "multipart/form-data; boundary=..." khi gửi FormData.
export const uploadClient = axios.create({ baseURL: API_BASE_URL });

uploadClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * Ảnh trả về từ backend là đường dẫn tương đối (vd "/uploads/products/xxx.jpg").
 * Hàm này ghép với gốc gateway để dùng trực tiếp trong <img src>.
 * Nếu đã là URL đầy đủ (http...) thì giữ nguyên (vẫn hỗ trợ dán link ngoài nếu cần).
 */
export function resolveMediaUrl(path) {
  if (!path) return '';
  if (path.startsWith('http://') || path.startsWith('https://') || path.startsWith('data:')) {
    return path;
  }
  return `${API_ORIGIN}${path}`;
}

// Attach JWT access token to every outgoing request
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Global handling: if the token has expired, force the user back to login
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default axiosClient;
