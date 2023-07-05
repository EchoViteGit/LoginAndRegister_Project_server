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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

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
    public String sendValidateEmail(String email, String sessionId,boolean hasAccount) {
        String key = "email:"+ sessionId + ":" +email + ":" + hasAccount;
        System.out.println("sendValidateEmail:"+key);
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            Long expire = Optional.ofNullable( redisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120)
                return "请求频繁！";
        }
        Account account = userMapper.findAccountByNameOrEmail(email);
        if(hasAccount && account == null){
            return "没有绑定此邮箱的账户";
        }
        if(!hasAccount && account != null){
            return "此邮箱已注册！";
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
            return null;
        }catch (MailException e){
            e.printStackTrace();
            return "邮件发送失败，请检查邮件地址！";
        }
    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code,String sessionId) {
        String key = "email:"+ sessionId + ":" +email + ":false";
        System.out.println("validateAndRegister:"+key);
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            String s = redisTemplate.opsForValue().get(key);
            if (s == null)
                return "验证码失效，请重新登录";
            if(s.equals(code)){
                password = bCryptPasswordEncoder.encode(password);
                redisTemplate.delete(key);
                Account account = userMapper.findAccountByNameOrEmail(username);
                if(account!=null)
                    return "用户名已被占用！";
                if(userMapper.CreateAccount(username, password, email)>0){
                    return null;
                }else {
                    return "内部错误，请联系管理员！";
                }
            }else {
                return "验证码错误";
            }
        }else {
            return "请先获取验证码！";
        }
    }

    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email:"+ sessionId + ":" +email + ":true";
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            String s = redisTemplate.opsForValue().get(key);
            if (s == null)
                return "验证码失效，请重新登录";
            if(s.equals(code)){
                redisTemplate.delete(key);
                return null;
            }
            else
                return "验证码错误";
        }else {
            return "请先获取验证码！";
        }
    }

    @Override
    public boolean resetPassword(String password, String email) {
        password = bCryptPasswordEncoder.encode(password);
        return userMapper.resetPasswordByEmail(password, email)>0;
    }
}

