import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { postApi } from '../api/postApi';
import { resolveMediaUrl } from '../api/axiosClient';
import './LatestPosts.css';

export default function LatestPosts() {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    postApi.getLatest(3)
      .then((res) => setPosts(res.data))
      .catch(() => setPosts([]))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return null;
  if (posts.length === 0) return null;

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    try {
      return new Date(dateStr).toLocaleDateString('vi-VN');
    } catch {
      return '';
    }
  };

  return (
    <section className="latest-posts">
      <div className="latest-posts-header">
        <span className="eyebrow">Góc chia sẻ</span>
        <h2>Bài viết mới nhất</h2>
      </div>

      <div className="latest-posts-grid">
        {posts.map((post) => (
          <Link className="post-card" key={post.id} to={`/posts/${post.id}`}>
            <div className="post-card-cover">
              {post.imageUrl ? (
                <img src={resolveMediaUrl(post.imageUrl)} alt={post.title} />
              ) : (
                <div className="post-card-cover-placeholder">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.3" width="40" height="40">
                    <rect x="3.5" y="4" width="17" height="16" rx="1.5" />
                    <path d="M7 8.5h10M7 12h10M7 15.5h6" strokeLinecap="round" />
                  </svg>
                </div>
              )}
            </div>
            <div className="post-card-body">
              <span className="post-card-date">{formatDate(post.publishedAt)}</span>
              <h3>{post.title}</h3>
              {post.summary && <p>{post.summary}</p>}
              <span className="post-card-author">{post.author}</span>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}
