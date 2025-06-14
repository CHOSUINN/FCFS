package com.fcfs.fcfs.wishlist.service;

import com.fcfs.fcfs.wishlist.dto.request.WishlistDetailRequestDto;
import com.fcfs.fcfs.wishlist.dto.response.WishlistResponseDto;

public interface WishlistService {
    // 위시리스트 물품 등록 및 수량
    WishlistResponseDto createOrUpdateWishlist(Long userId, WishlistDetailRequestDto requestDto);

    WishlistResponseDto listWishlist(Long userId);

    // 위시리스트 상품 제거
    WishlistResponseDto deleteWishlistItem(Long userId, Long productId);
}
