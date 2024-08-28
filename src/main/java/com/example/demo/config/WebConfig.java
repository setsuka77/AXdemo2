package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Webアプリケーションの全体的な設定を行う
 * 主にインターセプターの設定を担当
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionInterceptor sessionInterceptor;

    /**
     * インターセプターの登録
     *
     * @param registry インターセプターを管理するレジストリ
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // セッションインターセプターを登録し、指定したパスパターンを除外
        registry.addInterceptor(sessionInterceptor)
                .excludePathPatterns("/", "/login", "/css/**", "/js/**", "/images/**"); // ログイン画面と静的リソースには適用しない
    }
    
}