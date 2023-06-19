package com.sparta.springauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("수동 등록한 passwordEncoder를 주입받아와 문자열 암호화")
    void test1() {
        // 현재 비밀번호라고 생각
        String password = "Robbie's password";

        // 암호화
        String encodePassword = passwordEncoder.encode(password);
        System.out.println("encodePassword = " + encodePassword);

        // 암호화 되지 않은 것을 '평문'이라고 함
        String inputPassword = "Robbie";

        // 복호화를 통해 암호화된 비밀번호와 비교
        // 첫 번째 파라미터는 입력받은 문자열, 두 번째는 우리의 비밀번호(암호화한 상태값)
        boolean matches = passwordEncoder.matches(inputPassword, encodePassword);
        System.out.println("matches = " + matches); // 암호화할 때 사용된 값과 다른 문자열과 비교했기 때문에 false
    } // test1()
}
