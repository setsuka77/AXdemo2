package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class DailyReportController {
	
	/**
	 * 日報登録画面 初期表示
	 *
	 * @param model
	 * @param session
	 * @return 日報登録画面
	 */
	@GetMapping("/report/dailyReport")
	public String showAttendanceForm(Model model, HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		
		model.addAttribute("loginUser", loginUser);
		return "report/dailyReport";
	}
	
	
	
	/*
	 * 「メニュー」ボタン押下
	 */
	@PostMapping(path = "/report/dailyReport", params = "back")
	public String backMenu(RedirectAttributes redirectAttributes) {

		/*		//sessionを削除
				session.removeAttribute("calendar");*/
		
		return "redirect:/index";
	}
}
