package com.sparta.springauth.jwt;

import com.sparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
//    JwtUtil은 JWT 관련된 기능들을 가진 Class로 생각하기
//    이름을 꼭 Util으로 할 필요는 없다. JwtProvider로 짓기도 함 !

    // JWT 토큰을 그냥 Response Header에 달아서 내보낼 수도 있고,
    // Cookie 객체를 만들어서 cookie객체에 이 토큰을 담은 다음에 그 쿠키를 Response 객체에 담기
    // 서버에서 직접 쿠키를 만들면 직접 만료기한이나 다른 옵션을 줄 수 있음
    // Header에 Set-Cookie로 넘어가면서 자동으로 Cookie가 저장된다는 장점도 있음

    // 두 번째면 좀 더 코드 수도 줄어듦
    // 둘 중에서 어떤 것이 더 좋나요 ? - 좋고 나쁘고는 없음 (어떤 서비스냐, 어떤 상황이냐에 따라 다름)

    // 1) JWT 데이터
    // Header Key 값 - 위의 두 가지 방법 다 필요함
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 Key - 사용자마다 권한을 부여할 수 있는데, 권한에 대한 정보도 jwt에 담아서 보낼 수 있음. 그 권한을 가져오기 위한 key값
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자 - 우리가 만든 토큰 앞에 붙일 것 (규칙같은 것으로 붙이는 것이 좋음). 구분하기 위해서 한 칸 띈다
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료 시간 (혹은 expired time도 가능)
    private final long TOKEN_TIME = 60*60*1000L; // 60분

    // application.properties에 있는 값 가져오기 (Value 에노테이션 가져오기 - lombok이 아님 !)
    @Value("${jwt.secret.key}") //Base64 Encode한 SecretKey
    private String secretKey;
    private Key key;    // JWT를 관리하는 최신 방법
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // HS256 알고리즘 (enum으로 되어있음)

    // 로그 설정
    // 로깅: 어플리케이션 동작 중 프로젝트의 상태나 동작 정보를 시간순으로 기록하는 것 (하단의 이름으로 로그가 찍힘)
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    // 딱 한번만 받아오면 값을 사용할 때마다 요청을 새로 호출하는 실수를 방지하기 위해서 사용
    // jwtUtil 클래스 생성자를 호출한 뒤에 key field에 secretkey를 담을 것 (객체 생성 후에 실행되는 부분)
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey); // decoding 필요 (반환 타입은 byte)
        key = Keys.hmacShaKeyFor(bytes);  // byte값을 담아주면 변환이 일어난 다음에 key 값이 우리가 사용할 secret key가 담긴다
    }

    // 2) JWT를 (토큰을) 생성 (=다룬다)
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();
        // 무조건 사용자 권한이나 발급일을 넣어야 하는 것은 아님
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한 (앞에 key 뒤에 value)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }
    
    // 3) 생성된 JWT를 Cookie에 저장하는 기능
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            // Cookie Value 에는 공백이 불가능해서 encoding 진행
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }
    
    // Cookie에 들어 있던 JWT Token을 substring (자름)
    public String substringToken(String tokenValue) {
        // 공백인지, null인지 확인하기 && Bearer로 시작되는지 확인하기
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7); // Bearer+공백까지 해서 7부터 잘라냄
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }
    
    // JWT 토큰 검증 - 반환 타입 boolean
    public boolean validateToken(String token) {
        try {
            // parseClaimsJws(token) 으로 위증이나 만료 등등을 파악할 수 있음
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // JWT 토큰 에서 사용자 정보를 가져오기 - 반환 타입 claims
    public Claims getUserInfoFromToken(String token) {
        // key값과 분석할 token 넣어주고, getBody를 통해 body 부분에 들어있는 claims를 가져옴
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies(); // 여러 개 담겨있던 쿠키들을 전부 배열로 가지고 옴
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) { // 쿠키의 이름이 우리가 갖고오려고 하는 Authroization인지 아닌지 확인
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }


}
