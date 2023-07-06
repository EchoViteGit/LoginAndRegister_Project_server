package com.example.separate.server.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/7/5 12:11
 */
public interface AuthorizeService extends UserDetailsService {
    String sendValidateEmail(String email, String sessionId,boolean hasAccount);
    String validateAndRegister(String username, String password, String email, String code, String sessionID);
    String validateOnly(String email,String code,String sessionId);
    boolean resetPassword(String password,String email);
    boolean check_username(String username);
}
