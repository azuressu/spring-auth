package com.sparta.springauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Bean으로 등록하려는 클래스 위에 붙여줌
// 등록될 때 passwordConfig (앞의 P가 소문자로 바뀜)
@Configuration
public class PasswordConfig { // passwordConfig

    // 빈으로 등록하고자 하는 객체를 반환하는 메서드를 선언
    @Bean
    public PasswordEncoder passwordEncoder() {  // passwordEncoder 이렇게 등록
        // DI 주입 받으면, BCryptPasswordEncoder 구현체가 등록됨
        // BCrypt는 비밀번호를 암호화해주는 Hash 함수
        // 현재까지 사용중인 것들 중에서 아주 강력한 Hash 매커니즘을 갖고 있음
        return new BCryptPasswordEncoder();
    }

}
