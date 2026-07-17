import React, { useEffect, useState } from 'react';
import { productApi } from '../api/productApi';
import ProductCard from '../components/ProductCard';
import BannerSlider from '../components/BannerSlider';
import LatestPosts from '../components/LatestPosts';
import './Home.css';

export default function Home() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [loading, setLoading] = useState(true);

  const loadProducts = async (params = {}) => {
    setLoading(true);
    try {
      const res = await productApi.getAll(params);
      setProducts(res.data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProducts();
    productApi.getCategories().then((res) => setCategories(res.data)).catch(() => {});
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    loadProducts({ keyword, categoryId: categoryId || undefined });
  };

  const handleCategoryClick = (id) => {
    setCategoryId(id);
    setKeyword('');
    loadProducts(id ? { categoryId: id } : {});
  };

  return (
    <div className="home-page">
        <BannerSlider />
      <section className="hero">
        <span className="eyebrow">Bộ sưu tập mới</span>
        <h1>Thời trang cho mọi phong cách</h1>
        <p>Hàng ngàn mẫu quần áo mới nhất đang chờ bạn khám phá</p>
        <form className="search-bar" onSubmit={handleSearch}>
          <input
            placeholder="Tìm sản phẩm theo tên hoặc thương hiệu..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <button type="submit">Tìm kiếm</button>
        </form>
      </section>

      <div className="category-filters">
        <button className={!categoryId ? 'active' : ''} onClick={() => handleCategoryClick('')}>
          Tất cả
        </button>
        {categories.map((c) => (
          <button
            key={c.id}
            className={categoryId === c.id ? 'active' : ''}
            onClick={() => handleCategoryClick(c.id)}
          >
            {c.name}
          </button>
        ))}
      </div>

      {loading ? (
        <p className="loading-text">Đang tải sản phẩm...</p>
      ) : products.length === 0 ? (
        <p className="loading-text">Không tìm thấy sản phẩm nào.</p>
      ) : (
        <div className="product-grid">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}

      <LatestPosts />
    </div>
  );
}
