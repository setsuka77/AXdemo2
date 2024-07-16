package com.example.demo.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Securityの設定を行うクラス。
 * 1,ConfigurationとEnableWebSecurityアノテーションを付ける
 * 2,SecurityFilterChainを返すメソッドにBeanアノテーションを付ける
 * の2つが必要。
 *
 * パスワードをハッシュ化する場合は
 * 3.PasswordEncoderを返すメソッドにBeanアノテーションを付ける
 * も行う必要あり。
 *
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .formLogin()
        .and()
        .logout()
        .and()
        .authorizeHttpRequests(authz -> authz
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .anyRequest().authenticated()
        );
        return http.build();
    }
}