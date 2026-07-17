import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Guards a route so only authenticated users can access it.
 * If adminOnly is true, non-admin users are redirected home.
 */
export default function ProtectedRoute({ children, adminOnly = false }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (adminOnly && user.role !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }
  return children;
}
