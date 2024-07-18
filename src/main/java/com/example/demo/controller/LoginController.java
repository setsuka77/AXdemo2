package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.LoginService;

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
	public String login(@ModelAttribute @RequestParam Integer id, @RequestParam String password, Model model) {
		if (loginService.login(id, password)) {
			return "redirect:/attendance";
		} else {
			model.addAttribute("error", "ユーザIDまたはパスワードが間違っています。");
			return "login/login";
		}
	}
	
	
}
