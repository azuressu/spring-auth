package com.sparta.springauth.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 수동 등록이 가능하게 함
@EnableWebSecurity // Spring Security 지원을 가능하게 함
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 (CSRF(사이트 간 요청 위조, Cross-site request forgery))
        http.csrf((csrf) -> csrf.disable()); // 사용하지 않겠다

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정 (모든 것을 허가하겠다 !)
                        .requestMatchers("/api/user/**").permitAll()
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 로그인 사용
//        http.formLogin(Customizer.withDefaults());

        // Security 자체에서의 로그인
        http.formLogin((formLogin) ->
                formLogin
                        // 로그인 View 제공(Get /api/user/login-page) - 우리가 만든 로그인 뷰를 제공하고 싶을 때
                        .loginPage("/api/user/login-page")
                        // 로그인 처리 (Post /api/user/login) -> 우리가 구현했던 곳 안으로 들어오는 것이 아니라, Security 앞단에서 처리하게 됨
                        .loginProcessingUrl("/api/user/login")
                        // 로그인 처리 후 성공 시 URL
                        .defaultSuccessUrl("/")
                        // 로그인 처리 후 실패 시 URL
                        .failureUrl("/api/user/login-page?error")
                        .permitAll()
        );

        return http.build();
    }
}