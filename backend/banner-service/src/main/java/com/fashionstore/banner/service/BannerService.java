package com.fashionstore.banner.service;

import com.fashionstore.banner.dto.BannerRequest;
import com.fashionstore.banner.entity.Banner;
import com.fashionstore.banner.exception.ResourceNotFoundException;
import com.fashionstore.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    /** Dùng cho trang quản trị: trả về TẤT CẢ banner (kể cả đang ẩn), sắp theo thứ tự hiển thị. */
    public List<Banner> getAllBanners() {
        return bannerRepository.findAllByOrderByDisplayOrderAsc();
    }

    /** Dùng cho trang chủ: chỉ trả về banner đang bật (active = true), sắp theo thứ tự hiển thị. */
    public List<Banner> getActiveBanners() {
        return bannerRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public Banner getById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy banner với id = " + id));
    }

    @Transactional
    public Banner createBanner(BannerRequest request) {
        Banner banner = Banner.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return bannerRepository.save(banner);
    }

    @Transactional
    public Banner updateBanner(Long id, BannerRequest request) {
        Banner banner = getById(id);
        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) banner.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) banner.setActive(request.getActive());
        return bannerRepository.save(banner);
    }

    @Transactional
    public void deleteBanner(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy banner với id = " + id);
        }
        bannerRepository.deleteById(id);
    }
}
