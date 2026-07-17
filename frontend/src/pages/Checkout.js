import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { orderApi } from '../api/orderApi';
import { promotionApi } from '../api/promotionApi';
import { authApi } from '../api/authApi';
import './Checkout.css';

export default function Checkout() {
  const { cart, clearCart } = useCart();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    shippingAddress: '', receiverName: '', receiverPhone: '', paymentMethod: 'COD',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);



  const [promoCode, setPromoCode] = useState('');
  const [promoResult, setPromoResult] = useState(null);
  const [promoChecking, setPromoChecking] = useState(false);

  useEffect(() => {
    const fetchFullProfile = async () => {
      try {
        // Dùng chính hàm getProfile() mà bạn đã viết cho trang Profile
        const res = await authApi.getProfile(); 
        const profile = res.data;
        
        setForm(prev => ({
          ...prev,
          receiverName: profile.fullName || '',
          receiverPhone: profile.phone || '',
          shippingAddress: profile.address || ''
        }));
      } catch (err) {
        console.log("Khách chưa đăng nhập hoặc không lấy được thông tin profile", err);
      }
    };

    fetchFullProfile();
  }, []);
  // 👆 HẾT ĐOẠN TỰ ĐỘNG ĐIỀN 👆

  const formatPrice = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const buildPromoItems = () =>
    (cart.items || []).map((i) => ({
      productId: i.productId,
      categoryId: i.categoryId,
      price: i.price,
      quantity: i.quantity,
    }));

  useEffect(() => {
    if (!cart.items || cart.items.length === 0) return;
    runCalculate('');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cart.items]);

  const runCalculate = async (code) => {
    setPromoChecking(true);
    try {
      const res = await promotionApi.calculate(buildPromoItems(), code || undefined);
      setPromoResult(res.data);
    } catch (err) {
      setPromoResult(null);
    } finally {
      setPromoChecking(false);
    }
  };

  const handleApplyPromo = () => {
    if (!promoCode.trim()) return;
    runCalculate(promoCode.trim());
  };

  const handleRemovePromo = () => {
    setPromoCode('');
    runCalculate('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const items = cart.items.map((i) => ({ productId: i.productId, quantity: i.quantity, size: i.size, color: i.color, imageUrl: i.imageUrl}));
      const payload = { ...form, items };
      if (promoResult?.couponApplied && promoCode.trim()) {
        payload.promoCode = promoCode.trim();
      }
      const res = await orderApi.createOrder(payload);
      await clearCart();
      navigate(`/orders`, { state: { newOrderId: res.data.id } });
    } catch (err) {
      setError(err.response?.data?.message || 'Đặt hàng thất bại, vui lòng thử lại');
    } finally {
      setLoading(false);
    }
  };

  if (!cart.items || cart.items.length === 0) {
    return <p className="loading-text">Giỏ hàng trống, không thể thanh toán.</p>;
  }

  const subtotal = promoResult?.subtotal ?? cart.totalAmount;
  const autoDiscount = promoResult?.autoDiscount ?? 0;
  const couponDiscount = promoResult?.couponDiscount ?? 0;
  const finalTotal = promoResult?.total ?? cart.totalAmount;

  return (
    <div className="checkout-page">
      <h2>Thanh toán đơn hàng</h2>
      <div className="checkout-grid">
        <form className="checkout-form" onSubmit={handleSubmit}>
          {error && <div className="auth-error">{error}</div>}
          <label>Họ tên người nhận</label>
          <input name="receiverName" value={form.receiverName} onChange={handleChange} required />
          <label>Số điện thoại</label>
          <input name="receiverPhone" value={form.receiverPhone} onChange={handleChange} required />
          <label>Địa chỉ giao hàng</label>
          <textarea name="shippingAddress" value={form.shippingAddress} onChange={handleChange} required />
          <label>Phương thức thanh toán</label>
          <select name="paymentMethod" value={form.paymentMethod} onChange={handleChange}>
            <option value="COD">Thanh toán khi nhận hàng (COD)</option>
            <option value="ONLINE">Thanh toán online</option>
          </select>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Đang xử lý...' : 'Đặt hàng'}
          </button>
        </form>

        <div className="checkout-summary">
          <h3>Đơn hàng của bạn</h3>
          {cart.items.map((item) => (
            <div className="checkout-item" key={item.productId}>
              <span>{item.name}{item.size && ` (${item.size}${item.color ? ', ' + item.color : ''})`} x{item.quantity}</span>
              <span>{formatPrice(item.price * item.quantity)}</span>
            </div>
          ))}

          <div className="checkout-promo" style={{ margin: '14px 0' }}>
            {!promoResult?.couponApplied ? (
              <div style={{ display: 'flex', gap: 8 }}>
                <input
                  placeholder="Mã giảm giá (nếu có)"
                  value={promoCode}
                  onChange={(e) => setPromoCode(e.target.value)}
                  style={{ flex: 1 }}
                />
                <button type="button" onClick={handleApplyPromo} disabled={promoChecking || !promoCode.trim()}>
                  {promoChecking ? 'Đang kiểm tra...' : 'Áp dụng'}
                </button>
              </div>
            ) : (
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>✅ Đã áp dụng mã "{promoCode.trim().toUpperCase()}"</span>
                <button type="button" onClick={handleRemovePromo}>Bỏ mã</button>
              </div>
            )}
            {promoResult?.message && (
              <p style={{ fontSize: 13, marginTop: 6, color: promoResult.couponApplied ? '#3e7a5b' : '#c0392b' }}>
                {promoResult.message}
              </p>
            )}
          </div>

          <div className="checkout-item">
            <span>Tạm tính</span>
            <span>{formatPrice(subtotal)}</span>
          </div>
          {autoDiscount > 0 && (
            <div className="checkout-item">
              <span>Khuyến mãi tự động</span>
              <span>-{formatPrice(autoDiscount)}</span>
            </div>
          )}
          {couponDiscount > 0 && (
            <div className="checkout-item">
              <span>Mã giảm giá</span>
              <span>-{formatPrice(couponDiscount)}</span>
            </div>
          )}

          <div className="checkout-total">
            <span>Tổng cộng</span>
            <span>{formatPrice(finalTotal)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
