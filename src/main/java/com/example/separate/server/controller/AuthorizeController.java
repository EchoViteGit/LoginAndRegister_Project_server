package com.example.separate.server.controller;

import com.example.separate.server.entity.RestBean;
import com.example.separate.server.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/7/5 12:18
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final static String EMAIL_REGEX = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
    @Resource
    AuthorizeService service;
    @PostMapping("/valid-email")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX)  @RequestParam("email") String email,HttpSession session){

        if(service.sendValidateEmail(email,session.getId()))
            return RestBean.success("邮件已发送！请注意查收");
        else
            return RestBean.failure(400,"邮件发送失败，请联系管理员！");
    }
}
