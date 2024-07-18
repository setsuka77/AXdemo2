package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Users;
import com.example.demo.service.LoginService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class LoginController {

	@Autowired
	private LoginService loginService;

//	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/*
	 * ログイン画面初期表示
	 */
	@RequestMapping("/")
	public String login() {
		return "login/login";
	}

//	//SpringSecurityのログイン画面 初期表示
//	@GetMapping("/") // ルートURL ("/") に対するGETリクエストを処理します
//	public String redirectToIndex() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 現在のユーザーの認証情報を取得します
//		if (authentication != null && authentication.isAuthenticated()) { // ユーザーがログインしている場合
//			return "redirect:/attendance";
//		}
//		return "redirect:/login"; // ユーザーがログインしていない場合、"/login"にリダイレクトします
//	}

	//SpringSecurityを使わない場合
	@PostMapping("/login")
    public String login(@RequestParam("id") Integer id, 
                        @RequestParam("password") String password, 
                        Model model) {
        boolean isAuthenticated = loginService.authenticate(id, password);
        
        System.out.println(password);
        
        if (isAuthenticated) {
            return "redirect:/attendance";
        } else {
            model.addAttribute("error", "ユーザIDまたはパスワードが間違っています。");
            return "login/login";
        }
    }
}
