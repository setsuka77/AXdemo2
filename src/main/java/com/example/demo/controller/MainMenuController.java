package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainMenuController {

	/**
	 * 処理メニュー画面 初期表示
	 *
	 * @param model
	 * @param session
	 * @return 処理メニュー画面
	 */
	@GetMapping("/index")
	public String userManage(HttpSession session, Model model) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		
		//ログインユーザの権限判定
		String role = loginUser.getRole();
		if ("1".equals(role)) {
			role = "admin";
		} else if ("2".equals(role)) {
			role = "manager";
		} else if ("3".equals(role) || "4".equals(role)) {
			role = "regular";
		}
		
		model.addAttribute("loginUser", loginUser);
		// ロールに基づいてModelにメニュー情報を追加
        model.addAttribute("role", role);

		return "menu/index";
	}

}
