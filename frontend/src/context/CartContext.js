import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { cartApi } from '../api/cartApi';
import { useAuth } from './AuthContext';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [cart, setCart] = useState({ items: [], totalAmount: 0, totalItems: 0 });

  const refreshCart = useCallback(async () => {
    if (!user) {
      setCart({ items: [], totalAmount: 0, totalItems: 0 });
      return;
    }
    try {
      const res = await cartApi.getCart();
      setCart(res.data);
    } catch (e) {
      // silently ignore - user might not be logged in yet
    }
  }, [user]);

  // Đồng bộ giỏ hàng với server ngay khi có user (lúc app khởi động / F5 reload,
  // hoặc ngay sau khi đăng nhập). Trước đây refreshCart được định nghĩa nhưng
  // không hề được gọi ở đâu, nên sau khi load lại trang, cart luôn hiển thị rỗng
  // cho tới khi người dùng thêm sản phẩm mới (lúc đó mới lộ ra dữ liệu cũ đã có sẵn
  // trong Redis, gây cảm giác "mất rồi lại hiện ra").
  useEffect(() => {
    refreshCart();
  }, [refreshCart]);

  const addToCart = async (productId, quantity = 1, size, color) => {
    const res = await cartApi.addToCart({ productId, quantity, size, color });
    setCart(res.data);
  };

  const updateQuantity = async (productId, quantity) => {
    const res = await cartApi.updateQuantity({ productId, quantity });
    setCart(res.data);
  };

  const removeItem = async (productId) => {
    const res = await cartApi.removeItem(productId);
    setCart(res.data);
  };

  const clearCart = async () => {
    await cartApi.clearCart();
    setCart({ items: [], totalAmount: 0, totalItems: 0 });
  };

  return (
    <CartContext.Provider value={{ cart, refreshCart, addToCart, updateQuantity, removeItem, clearCart }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
