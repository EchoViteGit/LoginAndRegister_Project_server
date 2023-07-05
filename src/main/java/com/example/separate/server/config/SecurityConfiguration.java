package com.example.separate.server.config;

import com.alibaba.fastjson.JSONObject;
import com.example.separate.server.entity.RestBean;
import com.example.separate.server.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/7/3 20:34
 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;

    @Resource
    DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   PersistentTokenRepository repository) throws Exception {
            return httpSecurity
                    .authorizeHttpRequests()
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginProcessingUrl("/api/auth/login")
                    .successHandler(this::onAuthenticationSuccess)
                    .failureHandler(this::onAuthenticationFailure)
                    .and()
                    .logout()
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler(this::onAuthenticationSuccess)
                    .and()
                    .rememberMe()
                    .rememberMeParameter("remember")
                    .tokenRepository(repository)
                    .tokenValiditySeconds(3600)
                    .and()
                    //.userDetailsService(authorizeService)
                    .csrf().disable()
                    .cors()
                    .configurationSource(this.corsConfigurationSource())
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(this::onAuthenticationFailure)
                    .and()
                    .build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

    private CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");//域间限制
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
        return security.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authorizeService)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        if (request.getRequestURI().endsWith("/login"))
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功！")));
        else if (request.getRequestURI().endsWith("/logout")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("退出登录成功！")));
        }
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401,exception.getMessage())));
    }
}

