package com.fcfs.moduleapigateway.filter;

import com.fcfs.moduleapigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j(topic = "JwtGlobalFilter")
@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 토큰 검증을 스킵할 경로 목록 */
    private static final List<String> WHITELIST = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/products/**",
            "/public",        // prefix 매칭
            "/swagger-ui"     // swagger-ui 등
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("글로벌 필터 인증 시작 : {}", path);

        // 1) 패턴 매칭된 경로는 토큰 검증 건너뛰기
        for (String pattern : WHITELIST) {
            if (pathMatcher.match(pattern, path)) {
                return chain.filter(exchange);
            }
        }

        // 2) 그 외 경로에 대해 토큰 검증
        // Authorization 헤더 검사
        String auth = exchange.getRequest().getHeaders().getFirst(JwtUtil.AUTHORIZATION_HEADER);
        if (auth == null || !auth.startsWith(JwtUtil.BEARER_PREFIX)) {
            log.info("잘못된 토큰");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = auth.substring(JwtUtil.BEARER_PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            log.info("토큰 검증에 문제 발생");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 3) 사용자 정보 추출 후 헤더에 추가
        Long userId = jwtUtil.getUserId(token);         // 사용자 식별자
        String roles = jwtUtil.getRoles(token);        // 역할 리스트

        log.info("글로벌 필터 인증 완료: userId={}, roles={}", userId, roles);

        // downstream으로 헤더 추가
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.add("X-USER-ID", String.valueOf(userId));
                    headers.add("X-USER-ROLES", roles);
                })
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.info("글로벌 필터 인증 완료");
        return chain.filter(mutatedExchange);
    }

    /** 필터 순서 — 인증 필터이므로 라우팅 이전에 적용 */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}