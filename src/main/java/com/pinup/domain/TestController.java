package com.pinup.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class TestController {

    @Value("${oauth2.provider.kakao.client-id}")
    private String kakaoApiKey;

    @Value("${oauth2.provider.naver.client-id}")
    private String naverApiKey;

    @Value("${oauth2.provider.google.client-id}")
    private String googleApiKey;

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        model.addAttribute("naverApiKey", naverApiKey);
        model.addAttribute("googleApiKey", googleApiKey);
        model.addAttribute("redirectUri", redirectUri);
        return "login";
    }

    @GetMapping("/login/oauth2/code")
    @ResponseBody
    public String getCode(@RequestParam("code") String code) {
        return code;
    }
}
