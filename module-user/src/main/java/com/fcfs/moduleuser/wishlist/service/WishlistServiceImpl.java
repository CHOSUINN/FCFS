package com.fcfs.moduleuser.wishlist.service;

import com.fcfs.moduleuser.client.ProductFeignClient;
import com.fcfs.moduleuser.global.exception.CustomException;
import com.fcfs.moduleuser.global.exception.ErrorCode;
import com.fcfs.moduleuser.user.entity.User;
import com.fcfs.moduleuser.user.repository.UserRepository;
import com.fcfs.moduleuser.wishlist.dto.WishlistItemDto;
import com.fcfs.moduleuser.wishlist.dto.request.WishlistItemRequestDto;
import com.fcfs.moduleuser.wishlist.dto.response.ProductResponseDto;
import com.fcfs.moduleuser.wishlist.dto.response.WishlistResponseDto;
import com.fcfs.moduleuser.wishlist.entity.Wishlist;
import com.fcfs.moduleuser.wishlist.entity.WishlistItem;
import com.fcfs.moduleuser.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "WishlistServiceImpl")
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductFeignClient productClient;

    // 위시리스트 물품 등록 및 수량 수정
    @Override
    public WishlistResponseDto createOrUpdateWishlist(Long userId, WishlistItemRequestDto requestDto) {

        // User 조회
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // User에 연결된 wishlist 없으면 새로 생성
        Optional<Wishlist> optionalWishlist = wishlistRepository.findByUser_id(userId);
        Wishlist wishlist = optionalWishlist.orElseGet(
                () -> {
                    Wishlist newWishlist = Wishlist.builder().build();
                    user.assignWishlist(newWishlist);
                    return newWishlist;
                });

        // Product 조회
        ProductResponseDto product;
        product = productClient.getProductById(requestDto.productId());
        log.info("product id == {}", product.id());
        log.info("product id == {}", product.name());
        log.info("product id == {}", product.description());
        // fallback 함수 에러 잡아내기 : id = -1L
        if (product.id() == -1L) {
            log.error("product is null");
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        // 위시리스트 내에 중복 상품인지 확인.
        List<WishlistItem> wishlistDetails = wishlist.getWishlistItems();
        WishlistItem existingDetail = null;
        for (WishlistItem detail : wishlistDetails) {
            if (detail.getProductId().equals(product.id())) {
                existingDetail = detail;
                break;
            }
        }

        // 중복이면 값 수정. 아니면 물품 추가
        if (existingDetail != null) {
            existingDetail.setQuantity(requestDto.quantity());
        } else {
            WishlistItem newDetail = WishlistItem.from(product.id(), requestDto.quantity());
            wishlist.addWishlistDetail(newDetail);
        }

        wishlistRepository.save(wishlist);

        return toResponseDto(wishlist);
    }

    // 위시리스트 조회
    @Override
    public WishlistResponseDto listWishlist(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        Optional<Wishlist> optionalWishlist = wishlistRepository.findByUser_id(userId);
        Wishlist wishlist = optionalWishlist.orElseGet(
                () -> {
                    Wishlist newWishlist = Wishlist.builder().build();
                    user.assignWishlist(newWishlist);
                    return newWishlist;
                });
        return toResponseDto(wishlist);
    }

    // 위시리스트 상품 제거
    @Override
    public WishlistResponseDto deleteWishlistItem(Long userId, Long productId) {

        Wishlist wishlist = wishlistRepository.findByUser_id(userId).orElseThrow(
                () -> new CustomException(ErrorCode.WISHLIST_NOT_FOUND)
        );

        List<WishlistItem> wishlistDetails = wishlist.getWishlistItems();
        WishlistItem toRemove = null;
        for (WishlistItem detail : wishlistDetails) {
            if (detail.getProductId().equals(productId)) {
                toRemove = detail;
                wishlist.removeWishlistDetail(detail);
                break;
            }
        }

        if (toRemove == null) {
            throw new CustomException(ErrorCode.WISHLIST_DETAIL_NOT_FOUND);
        } else {
            wishlistRepository.save(wishlist);
        }
        return toResponseDto(wishlist);
    }

    @Transactional
    @Override
    public void clearWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUser_id(userId).orElseThrow(
                () -> new CustomException(ErrorCode.WISHLIST_NOT_FOUND)
        );
        wishlist.initWishlist();
    }

    // wishlistResponseDto로 변환
    private WishlistResponseDto toResponseDto(Wishlist wishlist) {
        List<WishlistItem> wishlistDetails = wishlist.getWishlistItems();

        List<WishlistItemDto> itemDtos = new ArrayList<>();
        Integer totalPrice = 0;
        for (WishlistItem wishlistDetail : wishlistDetails) {
            ProductResponseDto product = productClient.getProductById(wishlistDetail.getProductId());
            itemDtos.add(WishlistItemDto.from(product, wishlistDetail.getQuantity()));
            totalPrice += (wishlistDetail.getQuantity() * product.price());
        }

        return WishlistResponseDto.toDto(wishlist, itemDtos, totalPrice);
    }
}
