package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Users;
import com.example.demo.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    /*
     * ログイン画面初期表示
     */
    @RequestMapping("/")
    public String login() {
        return "login/login";
    }

    /*
     * ログインボタン押下
     */
    @PostMapping("/login")
    public String login(@RequestParam Integer id, @RequestParam String password, RedirectAttributes redirectAttributes,HttpSession session) {
        Users user = loginService.login(id, password);
        session.setAttribute("user",user);
        if (user != null) {
            String role = user.getRole();
            if ("1".equals(role)) {
                return "redirect:/userManagement/list";
            } else if ("2".equals(role) || "3".equals(role) || "4".equals(role)) {
                return "redirect:/attendance";
            } else {
                redirectAttributes.addFlashAttribute("error", "不明なロールです。");
                return "redirect:/";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
            return "redirect:/";
        }
    }
}
