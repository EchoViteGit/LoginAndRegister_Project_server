package com.example.separate.server.controller;

import com.example.separate.server.entity.RestBean;
import com.example.separate.server.entity.user.AccountUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/7/6 17:49
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("account") AccountUser accountUser){
        return RestBean.success(accountUser);
    }
}
