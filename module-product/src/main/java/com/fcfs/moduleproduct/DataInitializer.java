package com.fcfs.moduleproduct;

import com.fcfs.moduleproduct.product.entity.Product;
import com.fcfs.moduleproduct.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductRepository productRepository;

    @PostConstruct
    public void init() {
        // ────── 1. 기본 유저 로드 ──────
        // 예를 들어, ID가 1L인 유저가 미리 존재한다고 가정
        Long defaultUser = 1L;

        // ────── 2. Product 엔티티 5개 생성 ──────
        Product p1 = new Product(
                null,
                "텀블러",
                "스테인리스 스틸 텀블러 (0.5L, 핑크)",
                50,
                25000,
                defaultUser,
                true
        );

        Product p2 = new Product(
                null,
                "노트북 가방",
                "15인치 노트북 수납용 방수 백팩",
                30,
                40000,
                defaultUser,
                true
        );

        Product p3 = new Product(
                null,
                "무선 마우스",
                "저소음 무선 블루투스 마우스 (검정)",
                100,
                15000,
                defaultUser,
                true
        );

        Product p4 = new Product(
                null,
                "게이밍 키보드",
                "RGB LED 기계식 키보드 (청축)",
                20,
                80000,
                defaultUser,
                true
        );

        Product p5 = new Product(
                null,
                "USB-C 케이블",
                "2m 고속 충전 USB-C 케이블",
                200,
                8000,
                defaultUser,
                true
        );

        // ────── 3. 한 번에 저장 (saveAll) ──────
        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
    }
}