package com.example.cc.config;

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

@Configuration
@EnableWebSecurity //웹 보안 활성화 spring security 이용해서
@RequiredArgsConstructor
public class SecurityConfig {
    private final PrincipalDetailsService principalDetailsService;
    @Bean
    // securityFilterChain 스프링 시큐리티 설정 파일, 인증 인가 등 엮여있음
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception {
     /* 전체 요청에 접근할 수 있도록 하는 코드(별도의 인증 없이 모든 서비스 이용할 수 있게)
        return http
                .csrf(csrf ->csrf.disable())
                .authorizeHttpRequests(auth->auth.anyRequest().permitAll()).build();
     */
        // 요청에 대한 인증,인가 설정 요청 했을때 막아주는게 필요하다 ? (로그인 안되어있다 ->로그인페이지로)
        return http
                .csrf(csrf ->csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/user/login"),
                                new AntPathRequestMatcher("/user/auth"),
                                new AntPathRequestMatcher("/user/join")
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
            response.sendRedirect("http://localhost:8090/swagger-ui/index.html#/");
        };
    }
}
