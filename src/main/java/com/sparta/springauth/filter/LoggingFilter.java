package com.sparta.springauth.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "LoggingFilter")
//@Component
@Order(1)  // 체인 형식으로 되어있는 필터에서의 순서를 지정할 수 있음
public class LoggingFilter implements Filter { // 필터 인터페이스를 implements 받아옴
    // servletrequest, servletResponse, filterchain(필터이동을 위해) 를 매개변수로 받을 수 있음
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 전처리
        HttpServletRequest httpServletRequest = (HttpServletRequest) request; // 캐스팅 하여 사용
        String url = httpServletRequest.getRequestURI(); // URI 정보를 가져옴
        log.info(url); // 로그를 찍을 수 있음 (warn도 있고 error도 있음)

        chain.doFilter(request, response); // 다음 Filter 로 이동

        // 후처리
        log.info("비즈니스 로직 완료");

        // 어떤 요청인지에 대한 로그를 먼저 찍음
        // 그런 다음 doFilter를 통해서 다음 Filter로 이동시킴
        // 내부 Filter를 다 타고, DispatcherServlet 타고 들어와서
        // Handler Mapping을 통해 Controller를 찾음
        // 수행이 되고 나서 그 응답을 다시 DispatcherServlet으로 넘어오고 다시 Filter단으로 보내줌
        // 다 끝나면은 다시 log.info로 넘어와서 "비즈니스 로직 완료"가 찍힘
    }
}