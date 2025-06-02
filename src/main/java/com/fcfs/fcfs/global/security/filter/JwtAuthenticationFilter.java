package com.fcfs.fcfs.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcfs.fcfs.global.security.UserDetailsImpl;
import com.fcfs.fcfs.global.security.dto.LoginRequestDto;
import com.fcfs.fcfs.global.security.util.JwtUtil;
import com.fcfs.fcfs.user.entity.UserRoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "JwtAuthenticationFilter - JWT 인증 필터. 로그인 시 작동")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    // 빈등록할때 경로 설정해줄 세터.
    // UsernamePasswordAuthenticationFilter에는 기본적으로 "/login"으로 되어 있지만 나는 "/api/auth/login"으로 할것이기 때문에 세터를 오버라이드 해온다.
    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }

    // 로그이니 시도 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            // 1) HTTP 요청의 바디(InputStream)에서 JSON을 읽어 LoginRequestDto로 매핑
            LoginRequestDto requestDto = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestDto.class);

            // 2) UsernamePasswordAuthenticationToken 생성
            //    - 첫 번째 파라미터: username (아이디)
            //    - 두 번째 파라미터: password (비밀번호)
            //    - 세 번째 파라미터: 권한 정보(null로 두면, AuthenticationManager가 자체적으로 UserDetails에서 권한을 로딩)
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            requestDto.email(),
                            requestDto.password(),
                            null
                    );

            // 3) AuthenticationManager에게 인증 위임
            //    - authToken을 넘겨서, 내부에 설정된 UserDetailsService나 커스텀 프로바이더를 통해 사용자 검증
            //    - 검증 성공 시 Authentication(예: UsernamePasswordAuthenticationToken with authorities)이 반환되며,
            //      이후 successfulAuthentication(...) 콜백이 호출된다.
            return getAuthenticationManager().authenticate(authToken);

        } catch (IOException e) {
            // 4) JSON 파싱이나 스트림 읽기 도중 예외가 발생했다면 로그를 찍고 RuntimeException으로 던짐
            //    - IOException이 발생했다면 클라이언트 요청이 잘못된 포맷이거나 전달이 깨진 상황.
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공");
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getEmail();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(email, role);
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}
