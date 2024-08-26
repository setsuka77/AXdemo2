package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Users;
import com.example.demo.form.DailyReportDetailForm;
import com.example.demo.form.DailyReportForm;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
	    
	    //上部にステータスを表示
	    //dailyReportService.getStatusText(loginUser);
	    model.addAttribute("statusText", "未提出");
	    
	    model.addAttribute("loginUser", loginUser);
	    model.addAttribute("dailyReportForm", dailyReportForm);
	    return "report/dailyReport";
	}
	
	
	
	
	/**
	 * 「提出」ボタン押下
	 * 
	 * @param session 
	 * @param dailyReportForm 
	 * @param model 
	 * @param selectDate 
	 * @return 日報登録画面
	 */
	@PostMapping(path = "/report/dailyReport", params = "submit")
	public String submitReport(HttpSession session,@Valid DailyReportForm dailyReportForm,BindingResult result,
			Model model,String selectDate, RedirectAttributes redirectAttributes) {
		//ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		
		// ユーザー情報が取得できない場合の処理
        if (loginUser == null) {
            return "redirect:/login"; // ログイン画面に戻る
        }
		
		//入力チェック
	    String validationErrors = dailyReportService.validateDailyReport(dailyReportForm, selectDate);

	    // エラーが存在する場合
	    if (!validationErrors.isEmpty()) {
	        model.addAttribute("registerError", validationErrors);
	        model.addAttribute("loginUser", loginUser);
	        model.addAttribute("dailyReportForm", dailyReportForm);
	        model.addAttribute("statusText", "未提出");
	        return "report/dailyReport";
	    }
	    
		//登録処理 
		String message = dailyReportService.submitDailyReport(dailyReportForm,loginUser,selectDate);
		redirectAttributes.addFlashAttribute("message", message);
		
		//status変更
		dailyReportService.changeStatus(loginUser,selectDate,1);
		
		return "redirect:/report/dailyReport";
	}
	
	
	/**
	 * 「メニュー」ボタン押下
	 * 
	 * @param redirectAttributes
	 * @return 処理メニュー画面
	 */
	@PostMapping(path = "/report/dailyReport", params = "back")
	public String backMenu(RedirectAttributes redirectAttributes) {
		
		return "redirect:/index";
	}
}
