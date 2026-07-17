import React, { useEffect, useState } from 'react';
import { userApi } from '../../api/userApi';

export default function ManageUsers() {
  const [users, setUsers] = useState([]);

  const loadUsers = () => userApi.getAll().then((res) => setUsers(res.data));

  useEffect(() => { loadUsers(); }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Xóa người dùng này?')) return;
    await userApi.remove(id);
    loadUsers();
  };

  return (
    <div>
      <h2>Quản lý người dùng</h2>
      <table className="admin-table">
        <thead>
          <tr><th>ID</th><th>Username</th><th>Email</th><th>Họ tên</th><th>Vai trò</th><th>Hành động</th></tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>{u.username}</td>
              <td>{u.email}</td>
              <td>{u.fullName || '—'}</td>
              <td>{u.role}</td>
              <td>
                {u.role !== 'ADMIN' && (
                  <button className="btn-danger-sm" onClick={() => handleDelete(u.id)}>Xóa</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
