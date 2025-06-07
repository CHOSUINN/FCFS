package com.fcfs.moduleuser.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcfs.moduleuser.global.common.ApiResponse;
import com.fcfs.moduleuser.global.security.UserDetailsImpl;
import com.fcfs.moduleuser.global.security.dto.LoginRequestDto;
import com.fcfs.moduleuser.global.security.util.JwtUtil;
import com.fcfs.moduleuser.user.entity.UserRoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j(topic = "JwtAuthenticationFilter - JWT 인증 필터. 로그인 시 작동")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 빈등록할때 경로 설정해줄 세터.
    // UsernamePasswordAuthenticationFilter에는 기본적으로 "/login"으로 되어 있지만 나는 "/api/auth/login"으로 할것이기 때문에 세터를 오버라이드 해온다.
    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }

    // 로그인 시도 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            // 1) HTTP 요청의 바디(InputStream)에서 JSON을 읽어 LoginRequestDto로 매핑
            LoginRequestDto requestDto = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestDto.class);

            // 2) dto의 validation 검증
            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(requestDto);
            if (!violations.isEmpty()) {
                // 검증 위반 메시지들을 모아 하나의 문자열로 합칩니다.
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                log.error("Validation failed: {}", errorMessage);

                // AuthenticationException 계열의 예외로 던지면
                // 스프링 시큐리티가 자동으로 unsuccessfulAuthentication()을 호출합니다.
                throw new AuthenticationServiceException("입력값 검증 실패: " + errorMessage);
            }

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
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUserId();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(userId, role);

        response.addHeader("Authorization", "Bearer " + token);

        // 3) HTTP 상태 200, Content-Type JSON + UTF-8 설정
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        // 4) ApiResponse에 토큰을 담아 반환
        //    데이터 형태는 상황에 맞게 바꿔도 좋습니다. 예를 들어, 토큰만 보내도 되고, 사용자 정보를 함께 보내도 됩니다.
        Map<String, String> data = Map.of("token", token);

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.success(
                HttpStatus.OK,
                "로그인에 성공했습니다.",
                data
        );

        // 5) ObjectMapper로 JSON 직렬화하여 응답 본문에 쓰기
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.info("로그인 실패");

        // 1) HTTP 상태 코드 설정 (401 Unauthorized)
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 2) Content-Type을 application/problem+json으로 설정
         response.setContentType("application/problem+json");
         response.setCharacterEncoding("UTF-8");

        // 3) ProblemDetail 객체 생성해서 필드 채우기
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "로그인에 실패했습니다: " + failed.getMessage()
        );
        // type: RFC 7807의 "type" 필드 → 식별자 URI
        problem.setType(URI.create("https://localhost:8081/errors/AUTHENTICATION_FAILED"));
        // title: 짧은 요약
        problem.setTitle("Authentication Failed");
        // instance: 문제가 발생한 리소스 경로
        problem.setInstance(URI.create(request.getRequestURI()));

        // 4) Jackson을 이용해 JSON으로 직렬화 후 응답 본문에 쓰기
        objectMapper.writeValue(response.getWriter(), problem);
    }
}
