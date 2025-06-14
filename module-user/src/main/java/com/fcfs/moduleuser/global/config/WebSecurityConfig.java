package com.fcfs.moduleuser.global.config;

import com.fcfs.moduleuser.global.security.filter.JwtAuthenticationFilter;
import com.fcfs.moduleuser.global.security.util.JwtUtil;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final Validator validator;
    private final AuthenticationConfiguration authenticationConfiguration; // authentication manger 주입을 위해 사용


    // authentication manger 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, validator);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration)); // 바로 위에서 등록한 authenticationManager(AuthenticationConfiguration authenticationConfiguration) 사용
        filter.setFilterProcessesUrl("/api/auth/login");
        return filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // session 사용 안함
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 로그인 페이지 사용 안함
        http.formLogin(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        // 회원가입, 이메일 인증 api
                        .requestMatchers("/api/auth/login", "/api/auth/register", "api/verify/**").permitAll()
                        // 정적 리소스
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // 그 외 모든 요청도 일단 열어두기
                        .anyRequest().permitAll()
        );

        // 필터관리
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}