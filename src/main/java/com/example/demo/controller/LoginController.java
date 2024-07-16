package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class LoginController {

	/*
	 * ログイン画面初期表示
	 *
	 *
	 */
	@RequestMapping("/")
	public String login() {
		return "login/login";
	}

	@GetMapping("/") // ルートURL ("/") に対するGETリクエストを処理します
    public String redirectToIndex() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 現在のユーザーの認証情報を取得します
        if (authentication != null && authentication.isAuthenticated()) { // ユーザーがログインしている場合
            return "redirect:/attendance";
        }
        return "redirect:/login"; // ユーザーがログインしていない場合、"/login"にリダイレクトします
    }

//	// ログイン成功時の勤怠管理画面への遷移
//
//	@GetMapping("/attendance")
//    public String index() {
//        return "attendance/record";
//    }

}
