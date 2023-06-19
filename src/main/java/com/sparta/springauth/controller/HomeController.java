package com.sparta.springauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
// html 파일을 반환할 것이기 때문에 RestController가 아닌 Controller를 사용
// html 파일이 없으면 실행 시 오류 발생
public class HomeController {
    // 메인 페이지로 가기 위해서 만들어놓은 Controller

    @GetMapping("/")
    public String home(Model model) { // 파라미터의 모델을 받아와서
        // addAttribute를 사용해서 username이라는 name값을 주고(구분가능한 key값), 실제 넣어줄 value값 
        model.addAttribute("username", "username");
        return "index"; // index.html 페이지를 반환함
    }
}