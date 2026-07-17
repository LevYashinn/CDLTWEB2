import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { postApi } from '../api/postApi';
import { resolveMediaUrl } from '../api/axiosClient';
import './PostDetail.css';

export default function PostDetail() {
  const { id } = useParams();
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    setError('');
    postApi.getById(id)
      .then((res) => setPost(res.data))
      .catch(() => setError('Không tìm thấy bài viết này.'))
      .finally(() => setLoading(false));
  }, [id]);

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    try {
      return new Date(dateStr).toLocaleDateString('vi-VN', {
        day: '2-digit', month: '2-digit', year: 'numeric',
      });
    } catch {
      return '';
    }
  };

  if (loading) return <p className="loading-text">Đang tải bài viết...</p>;

  if (error || !post) {
    return (
      <div className="post-detail-page">
        <p className="loading-text">{error || 'Không tìm thấy bài viết này.'}</p>
        <Link to="/" className="btn-primary-inline">Về trang chủ</Link>
      </div>
    );
  }

  return (
    <article className="post-detail-page">
      <div className="post-detail-header">
        <Link to="/" className="post-detail-back">← Về trang chủ</Link>
        <span className="eyebrow">Góc chia sẻ</span>
        <h1>{post.title}</h1>
        <div className="post-detail-meta">
          <span className="post-detail-author">{post.author}</span>
          <span className="post-detail-dot">•</span>
          <span>{formatDate(post.publishedAt)}</span>
        </div>
      </div>

      {post.imageUrl && (
        <div className="post-detail-cover">
          <img src={resolveMediaUrl(post.imageUrl)} alt={post.title} />
        </div>
      )}

      {post.summary && <p className="post-detail-summary">{post.summary}</p>}

      <div className="post-detail-content">
        {post.content.split(/\n+/).filter(Boolean).map((para, idx) => (
          <p key={idx}>{para}</p>
        ))}
      </div>
    </article>
  );
}
