package com.example.cc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity //웹 보안 활성화 spring security 이용해서
@RequiredArgsConstructor
public class SecurityConfig {
    private final PrincipalDetailsService principalDetailsService;
    @Bean
    // securityFilterChain 스프링 시큐리티 설정 파일, 인증 인가 등 엮여있음
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception {
        // 요청에 대한 인증,인가 설정 요청 했을때 막아주는게 필요하다 ? (로그인 안되어있다 ->로그인페이지로)
        return http
                .csrf(csrf ->csrf.disable())
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/user/login"),
                                new AntPathRequestMatcher("/user/auth"),
                                new AntPathRequestMatcher("/user/join"),
                                new AntPathRequestMatcher("/stomp/chat/**"), // WebSocket 엔드포인트 허용
                                new AntPathRequestMatcher("/stomp/match/**")  // WebSocket 엔드포인트 허용
                        ).permitAll() // 해당 요청에 대해 접근 허용
                        .anyRequest().authenticated() //나머지 요청들은 인증 필요함
                )
                //로그인 폼
                .formLogin(form->form
                                .loginProcessingUrl("/user/login")//로그인 form action url
                                .usernameParameter("username")//아이디 파라미터명 설청
                                .passwordParameter("password")//패스워드 파라미터명 설정
                                .successHandler(authenticationSuccessHandler())//로그인 성공 핸들러
                )
                .logout(logout->logout
                        .logoutUrl("/user/logout")
                        .invalidateHttpSession(true) //현제 세션 무효화
                )
                .build();

    }
    //    사용자가 로그인 할 때 인증 요청 처리하는 애
    @Bean
    public AuthenticationManager authenticationManager(BCryptPasswordEncoder bCryptPasswordEncoder, PrincipalDetailsService principalDetailsService)throws Exception{
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(principalDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return (request , response, auth)->{
            response.setStatus(HttpServletResponse.SC_OK); // HTTP 200 응답 코드
        };
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://192.168.0.3:3000"));// 프론트 디바이스 ip주소
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setMaxAge(3600L); //1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
