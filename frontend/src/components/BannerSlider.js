import React, { useEffect, useState } from 'react';
import { bannerApi } from '../api/bannerApi';
import { resolveMediaUrl } from '../api/axiosClient';
import './BannerSlider.css';

export default function BannerSlider() {
  const [banners, setBanners] = useState([]);
  const [index, setIndex] = useState(0);

  useEffect(() => {
    bannerApi.getActiveBanners().then((res) => setBanners(res.data)).catch(() => {});
  }, []);

  useEffect(() => {
    if (banners.length <= 1) return;
    const timer = setInterval(() => {
      setIndex((i) => (i + 1) % banners.length);
    }, 4000);
    return () => clearInterval(timer);
  }, [banners.length]);

  if (banners.length === 0) return null;

  const current = banners[index];

  const handleClick = () => {
    if (!current.linkUrl) return;
    if (current.linkUrl.startsWith('http')) {
      window.open(current.linkUrl, '_blank', 'noopener,noreferrer');
    } else {
      window.location.href = current.linkUrl;
    }
  };

  return (
    <div className="banner-slider">
      <div
        className={`banner-slide ${current.linkUrl ? 'clickable' : ''}`}
        onClick={handleClick}
        title={current.title}
      >
        <img src={resolveMediaUrl(current.imageUrl)} alt={current.title} />
        {current.title && <div className="banner-caption">{current.title}</div>}
      </div>

      {banners.length > 1 && (
        <div className="banner-dots">
          {banners.map((b, i) => (
            <button
              key={b.id}
              className={i === index ? 'dot active' : 'dot'}
              onClick={() => setIndex(i)}
              aria-label={`Banner ${i + 1}`}
            />
          ))}
        </div>
      )}
    </div>
  );
}
