package com.sparta.springauth.auth;

import com.sparta.springauth.entity.UserRoleEnum;
import com.sparta.springauth.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
public class AuthController {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    // 쿠키를 만드는 코드
    @GetMapping("/create-cookie")
    public String createCookie(HttpServletResponse res) {
        addCookie("Robbie Auth", res);

        return "createCookie";
    }

    // 쿠키를 가져오는 코드
    @GetMapping("/get-cookie")
    // 쿠키 중에서 Authorization이라는 이름으로 된 쿠키를 에노테이션을 통해서 가지고 옴
    public String getCookie(@CookieValue(AUTHORIZATION_HEADER) String value) { // 상수를 CookieValue 괄호 안에다 넣음
        System.out.println("value = " + value);

        return "getCookie : " + value;
    }

    @GetMapping("/create-session")
    public String createSession(HttpServletRequest req) {
        // 세션이 존재할 경우 세션 반환, 없을 경우 새로운 세션을 생성한 후 반환
        HttpSession session = req.getSession(true);

        // 세션에 저장될 정보 Name - Value 를 추가합니다.
        session.setAttribute(AUTHORIZATION_HEADER, "Robbie Auth");

        return "createSession";
    }

    @GetMapping("/get-session")
    public String getSession(HttpServletRequest req) {
        // 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
        HttpSession session = req.getSession(false); // 가지고 오는데 또 생성할 필요가 없기 때문

        String value = (String) session.getAttribute(AUTHORIZATION_HEADER); // 가져온 세션에 저장된 Value 를 Name 을 사용하여 가져옵니다.
        System.out.println("value = " + value);

        return "getSession : " + value;
    }

    @GetMapping("/create-jwt")
    public String createJwt(HttpServletResponse res) {
        // JWT 생성
        String token = jwtUtil.createToken("Robbie", UserRoleEnum.USER);

        // JWT 쿠키 저장
        jwtUtil.addJwtToCookie(token, res);

        return "createJwt : " + token;
    }

    @GetMapping("/get-jwt")
    public String getJwt(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {
        // JWT 토큰 substring (앞의 Bearer+공백 을 제거)
        String token = jwtUtil.substringToken(tokenValue);

        // 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token Error");
        }

        // 토큰에서 사용자 정보 가져오기 (위에서 토큰 검증에 문제가 없다면)
        Claims info = jwtUtil.getUserInfoFromToken(token);
        // 사용자 Username
        String username = info.getSubject(); // 가져오는 방법
        System.out.println("username = " + username);
        // 사용자 권한
        String authority = (String) info.get(JwtUtil.AUTHORIZATION_KEY); // 권한을 가져오는 방법 (key로 가져오기)
        System.out.println("authority = " + authority);

        return "getJwt : " + username + ", " + authority;
    }





    // 쿠키를 저장하는 메소드 (쿠키의 Name과 Value) - 공백이 있으면 Cookie에서 저장할 수 없다고 오류가 발생함
    public static void addCookie(String cookieValue, HttpServletResponse res) {
        try {
            // Cookie Value 에는 공백이 불가능해서 encoding 진행
            cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue); // Name-Value
            cookie.setPath("/");         // set 메서드로 path
            cookie.setMaxAge(30 * 60);   // setMaxAge 만료 기한

            // Response 객체에 Cookie 추가
            // Servlet에서 만들어준 Response 객체에다 데이터를 담으면 클라이언트로 자연스럽게 반환됨
            // addCookie 메소드가 이미 존재함
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}