package com.example.separate.server.controller;

import com.example.separate.server.entity.RestBean;
import com.example.separate.server.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.jetbrains.annotations.NotNull;
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
    private final static String USERNAME_REGEX = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
    @Resource
    AuthorizeService service;

    @PostMapping("/check-username")
    public RestBean<String> check_username(String username){
        boolean checked = service.check_username(username);
        if(!checked){
            return RestBean.success("用户名可以使用！");
        }else{
            return RestBean.failure(400,"用户名已存在！");
        }
    }
    @PostMapping("/valid-register-email")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX)  @RequestParam("email") String email, @NotNull HttpSession session){
        String message = service.sendValidateEmail(email,session.getId(),false);
        if(message == null)
            return RestBean.success("邮件已发送！请注意查收");
        else
            return RestBean.failure(400,message);
    }

    @PostMapping("/valid-reset-email")
    public RestBean<String> validateRestEmail(@Pattern(regexp = EMAIL_REGEX)  @RequestParam("email") String email, @NotNull HttpSession session){
        String message = service.sendValidateEmail(email,session.getId(),true);
        if(message == null)
            return RestBean.success("邮件已发送！请注意查收");
        else
            return RestBean.failure(400,message);
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min = 2,max = 8) @RequestParam("username") String username,
                                         @Length(min = 6,max = 16) @RequestParam("password") String password,
                                         @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                         @Length(min = 6,max = 6) @RequestParam("code") String code,
                                         @NotNull HttpSession session){
        String str = service.validateAndRegister(username, password, email, code, session.getId());
        if(str==null){
            return RestBean.success("注册成功！");
        }else
        {
            return RestBean.failure(400,str);
        }
    }

    @PostMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                       @Length(min = 6,max = 6) @RequestParam("code") String code,
                                       @NotNull HttpSession session){
        String validatedMessage = service.validateOnly(email, code, session.getId());
        if(validatedMessage == null){
            session.setAttribute("reset-password", email);
            return RestBean.success();
        }else{
            return RestBean.failure(400,validatedMessage);
        }
    }

    @PostMapping("do-reset")
    public  RestBean<String> resetPassword(@Length(min = 6,max = 16) @RequestParam("password") String password,
                                           @NotNull HttpSession session){
        String email = (String) session.getAttribute("reset-password");
        if (email == null){
            return RestBean.failure(401,"请先完成邮箱验证");
        }else if(service.resetPassword(password,email)){
            session.removeAttribute("reset-password");

            return RestBean.success("密码重置成功！");
        }else {
            return RestBean.failure(500,"内部错误，请联系管理员！");
        }
    }
}
