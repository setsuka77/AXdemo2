package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class DepartmentController {

	/**
	 * 部署管理画面 初期表示
	 * 
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @return ユーザ管理画面
	 */
	@GetMapping("/department/manage")
	public String userManage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		
		model.addAttribute("loginUser", loginUser);
		return "department/manage";
	}
	
	/**
	 * 「メニュー」ボタン押下
	 * 
	 * @param redirectAttributes
	 * @return 処理メニュー画面
	 */
	@PostMapping(path = "/department/manage", params = "back")
	public String backMenu(RedirectAttributes redirectAttributes) {

		return "redirect:/index";
	}
	
	
}
