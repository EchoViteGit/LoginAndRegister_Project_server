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
    boolean sendValidateEmail(String email, String sessionId);
}
