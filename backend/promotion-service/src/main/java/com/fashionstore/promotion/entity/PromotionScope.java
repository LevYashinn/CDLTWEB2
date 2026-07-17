package com.fashionstore.promotion.entity;

public enum PromotionScope {
    /** Áp dụng cho toàn bộ đơn hàng / mọi sản phẩm. */
    ALL,
    /** Chỉ áp dụng cho sản phẩm thuộc 1 danh mục (categoryId). */
    CATEGORY,
    /** Chỉ áp dụng cho 1 sản phẩm cụ thể (productId). */
    PRODUCT
}
