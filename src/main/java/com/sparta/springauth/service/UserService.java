package com.sparta.springauth.service;

import com.sparta.springauth.dto.SignupRequestDto;
import com.sparta.springauth.entity.User;
import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ADMIN_TOKEN
    // 우리가 일반 사용자인지 관리하는 관리자인지 구분하기 위해서 만들어준 것
    // Token을 사용해서 관리자 여부를 판단함
    // 대개 현업에서는 관리자 권한을 부여할 수 있는 관리자 페이지를 따로 구현하거나 승인자에 의해서 결제하는 과정으로 구현함
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {  // Optional 타입 내부에 isPresent 메서드가 존재 (넣어준 값이 존재하는지 안하는지)
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER; // 일반 사용자 권한을 우선 넣어둠
        if (requestDto.isAdmin()) { // boolean 타입은 is~로 시작함 (기본적으로는 false임)
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) { // ADMIN_TOKEN을 제대로 반환했다면 true로 설정
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;  // 새롭게 ADMIN 권한으로 덮어씌움
        }

        // 사용자 등록
        User user = new User(username, password, email, role);
        userRepository.save(user);
    }
}