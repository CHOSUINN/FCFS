package com.fcfs.moduleuser.wishlist.controller;

import com.fcfs.moduleuser.global.common.ApiResponse;
import com.fcfs.moduleuser.global.security.annotation.UserId;
import com.fcfs.moduleuser.wishlist.dto.request.WishlistDetailRequestDto;
import com.fcfs.moduleuser.wishlist.dto.response.WishlistResponseDto;
import com.fcfs.moduleuser.wishlist.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "WishlistController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    // 위시리스트 상품 등록 및 수량 조절
    @PostMapping
    public ResponseEntity<ApiResponse<WishlistResponseDto>> create(@UserId Long userId,
                                                                   @RequestBody @Valid WishlistDetailRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "위시리스트 상품 추가에 성공하였습니다.",
                wishlistService.createOrUpdateWishlist(userId, requestDto)
        ));
    }

    // 위시리스트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<WishlistResponseDto>> list(@UserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "위시리스트 조회에 성공하였습니다.",
                wishlistService.listWishlist(userId)
        ));
    }

    // 위시리스트 상품 제거
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistResponseDto>> delete(@UserId Long userId,
                                                                   @PathVariable(name = "productId") Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "위시리스트 상품을 제거하였습니다.",
                wishlistService.deleteWishlistItem(userId, productId)
        ));
    }
}
