package com.example.separate.server.entity.auth;

import lombok.Data;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/7/3 22:03
 */
@Data
public class Account {
    int id;
    String username;
    String password;
    String email;
}
