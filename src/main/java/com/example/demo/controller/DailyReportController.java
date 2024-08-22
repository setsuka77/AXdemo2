package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Users;
import com.example.demo.form.DailyReportDetailForm;
import com.example.demo.form.DailyReportForm;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DailyReportController {
	
	@Autowired
	private DailyReportService dailyReportService;
	
	/**
	 * 日報登録画面 初期表示
	 *
	 * @param model
	 * @param session
	 * @return 日報登録画面
	 */
	@GetMapping("/report/dailyReport")
	public String showDailyReport(Model model, HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		
		// 初期表示用に10行の空のフォームを準備する
	    List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
	    for (int i = 0; i < 10; i++) {
	        dailyReportDetailFormList.add(new DailyReportDetailForm());
	    }

	    // DailyReportForm を初期化してモデルに追加
	    DailyReportForm dailyReportForm = new DailyReportForm();
	    dailyReportForm.setDailyReportDetailFormList(dailyReportDetailFormList);
	    
	    model.addAttribute("loginUser", loginUser);
	    model.addAttribute("dailyReportForm", dailyReportForm);
	    return "report/dailyReport";
	}
	
	/*
	 * 「提出」ボタン押下
	 * 
	 * @param session 
	 * @param dailyReportForm 
	 * @param model 
	 * @param selectDate 
	 * @return 日報登録画面
	 */
	@PostMapping(path = "/report/dailyReport", params = "submit")
	public String submitReport(HttpSession session,DailyReportForm dailyReportForm,Model model,String selectDate) {
		//ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");

		//登録処理 
		String message = dailyReportService.submitDailyReport(dailyReportForm, loginUser,selectDate);
		model.addAttribute("message", message);
		System.out.println(message);
		
		return "redirect:/report/dailyReport";
	}
	
	
	/*
	 * 「メニュー」ボタン押下
	 * 
	 * @param redirectAttributes
	 * @return 処理メニュー画面
	 */
	@PostMapping(path = "/report/dailyReport", params = "back")
	public String backMenu(RedirectAttributes redirectAttributes) {

		/*		//sessionを削除
				session.removeAttribute("calendar");*/
		
		return "redirect:/index";
	}
}
