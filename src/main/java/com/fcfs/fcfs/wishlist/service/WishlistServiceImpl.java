package com.fcfs.fcfs.wishlist.service;

import com.fcfs.fcfs.global.exception.CustomException;
import com.fcfs.fcfs.global.exception.ErrorCode;
import com.fcfs.fcfs.product.entity.Product;
import com.fcfs.fcfs.product.repository.ProductRepository;
import com.fcfs.fcfs.user.entity.User;
import com.fcfs.fcfs.user.repository.UserRepository;
import com.fcfs.fcfs.wishlist.dto.WishlistItemDto;
import com.fcfs.fcfs.wishlist.dto.request.WishlistDetailRequestDto;
import com.fcfs.fcfs.wishlist.dto.response.WishlistResponseDto;
import com.fcfs.fcfs.wishlist.entity.Wishlist;
import com.fcfs.fcfs.wishlist.entity.WishlistDetail;
import com.fcfs.fcfs.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j(topic = "WishlistService")
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 위시리스트 물품 등록 및 수량
    @Override
    public WishlistResponseDto createOrUpdateWishlist(Long userId, WishlistDetailRequestDto requestDto) {

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
        Optional<Product> optionalProduct = productRepository.findById(requestDto.productId());
        Product product = optionalProduct.orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );

        // 위시리스트 내에 중복 상품인지 확인.
        List<WishlistDetail> wishlistDetails = wishlist.getWishlistDetail();
        WishlistDetail existingDetail = null;
        for (WishlistDetail detail : wishlistDetails) {
            if (detail.getProduct().getId().equals(product.getId())) {
                existingDetail = detail;
                break;
            }
        }

        // 중복이면 값 수정. 아니면 물품 추가
        if (existingDetail != null) {
            existingDetail.setQuantity(requestDto.quantity());
        } else {
            WishlistDetail newDetail = WishlistDetail.from(product, requestDto.quantity());
            wishlist.addWishlistDetail(newDetail);
        }

        wishlistRepository.save(wishlist);

        return toResponseDto(wishlist);
    }

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

        List<WishlistDetail> wishlistDetails = wishlist.getWishlistDetail();
        WishlistDetail toRemove = null;
        for (WishlistDetail detail : wishlistDetails) {
            if (detail.getProduct().getId().equals(productId)) {
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

    // wishlistResponseDto로 변환
    private WishlistResponseDto toResponseDto(Wishlist wishlist) {
        List<WishlistDetail> wishlistDetails = wishlist.getWishlistDetail();

        List<WishlistItemDto> itemDtos = new ArrayList<>();
        Integer totalPrice = 0;
        for (WishlistDetail wishlistDetail : wishlistDetails) {
            itemDtos.add(WishlistItemDto.from(wishlistDetail));
            totalPrice += (wishlistDetail.getQuantity() * wishlistDetail.getProduct().getPrice());
        }

        return WishlistResponseDto.toDto(wishlist, itemDtos, totalPrice);
    }
}
