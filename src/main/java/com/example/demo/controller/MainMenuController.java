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
		
		model.addAttribute("loginUser", loginUser);

		return "menu/index";
	}

}
