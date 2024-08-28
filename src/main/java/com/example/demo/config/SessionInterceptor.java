package com.example.demo.config;



import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Users user = (Users) session.getAttribute("user");
            if (user == null) {
            	// セッションが切れている場合にエラーメッセージをセッションに追加
                session.setAttribute("error", "セッションが切れました。再度ログインしてください。");
                response.sendRedirect("/login");
                return false;
            }
        } else {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

}
