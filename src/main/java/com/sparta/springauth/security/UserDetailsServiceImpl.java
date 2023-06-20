package com.sparta.springauth.security;

import com.sparta.springauth.entity.User;
import com.sparta.springauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 메서드
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 해당 유저가 있는지 없는지 확인하면서 user를 받아옴
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

        // 객체를 생성하면서 반환
        return new UserDetailsImpl(user);
    }



}
