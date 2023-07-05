package com.example.separate.server.service.Impl;

import com.example.separate.server.entity.Account;
import com.example.separate.server.mapper.UserMapper;
import com.example.separate.server.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/7/3 22:01
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    String from;
    @Resource
    UserMapper userMapper;
    @Resource
    MailSender mailSender;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null){
            throw new UsernameNotFoundException("用户名不能为空！");
        }
        Account account = userMapper.findAccountByNameOrEmail(username);
        if(account == null){
            throw new UsernameNotFoundException("用户名或密码错误！");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    @Override
    public boolean sendValidateEmail(String email, String sessionId) {
        String key = "email:"+ sessionId + ":" +email;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            Long expire = Optional.ofNullable( redisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120)
                return false;
        }
        Random random = new Random();
        int code = random.nextInt(899999)+100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("欢迎使用VUE平台");
        message.setText("你的注册验证码为："+code);
        try {
            mailSender.send(message);
            redisTemplate.opsForValue().set(key,String.valueOf(code),3, TimeUnit.MINUTES);
            return true;
        }catch (MailException e){
            e.printStackTrace();
            return false;
        }
    }
}

