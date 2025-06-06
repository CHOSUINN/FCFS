package com.fcfs.moduleorder.global.security.filter;

import com.fcfs.moduleproduct.global.security.UserDetailsServiceImpl;
import com.fcfs.moduleproduct.global.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JwtAuthorizationFilter - 요청 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getJwtFromHeader(request);

        if (StringUtils.hasText(token)) {

            if (!jwtUtil.validateToken(token)) {
                log.error("Token Error");
                return;
            }

            Claims claims = jwtUtil.getUserInfoFromToken(token);

            setAuthentication(claims.getSubject());
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String subject) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(subject);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String subject) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
