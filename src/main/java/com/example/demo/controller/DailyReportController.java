package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.DailyReport;
import com.example.demo.entity.DailyReportDetail;
import com.example.demo.entity.Users;
import com.example.demo.form.DailyReportDetailForm;
import com.example.demo.form.DailyReportForm;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.service.DailyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DailyReportController {

	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private DailyReportMapper dailyReportMapper;

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
	    String role = loginUser.getRole();

	    // 初期表示用に10行の空のフォームを準備する
	    List<DailyReportDetailForm> dailyReportDetailFormList = new ArrayList<>();
	    if (!"2".equals(role)) {
	        for (int i = 0; i < 3; i++) {
	            dailyReportDetailFormList.add(new DailyReportDetailForm());
	        }
	    }else {
	    	//roleが2の時、申請一覧を表示
			model.addAttribute("dailyReport", dailyReportService.findAllReport());
			//却下と承認ボタンを非活性に設定
			model.addAttribute("checkReject", false);
			model.addAttribute("checkApproval", false);
		}

	    // DailyReportForm を初期化してモデルに追加
	    DailyReportForm dailyReportForm = new DailyReportForm();
	    dailyReportForm.setDailyReportDetailFormList(dailyReportDetailFormList);

	    model.addAttribute("loginUser", loginUser);
	    model.addAttribute("dailyReportForm", dailyReportForm);
		return "report/dailyReport";
	}

	/**
	 * 日付が選択されたときに日報情報を取得して返す
	 *
	 * @param session
	 * @param selectDate
	 * @return 選択された日付の日報情報
	 */
	@ResponseBody
	@RequestMapping(value = "/report/dailyReport", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, Object>> searchReport(HttpSession session,
			@RequestBody Map<String, String> requestBody) {
		// セッションからユーザー情報を取得
		Users loginUser = (Users) session.getAttribute("user");
		// リクエストボディから日付を取得
		String selectDate = requestBody.get("selectDate");

		// 日報情報を取得
		List<DailyReportDetail> reportDetail = dailyReportService.searchReport(loginUser, selectDate);

		// ステータスを取得
		Integer status = dailyReportService.searchReportStatus(loginUser, selectDate);
		String statusText = getStatusText(status);
		session.setAttribute("statusText", statusText);

		Map<String, Object> response = new HashMap<>();
		response.put("reportDetails", reportDetail);
		response.put("statusText", statusText);

		return ResponseEntity.ok(response);
	}

	/**
	 * 上部ステータス表示の設定
	 */
	public String getStatusText(Integer status) {
	    if (status == null) {
	        return "未申請";
	    }

	    switch (status) {
	    case 1:
	        return "提出済承認前";
	    case 2:
	        return "承認済み";
	    default:
	        return "未提出";
	    }
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
	public String submitReport(HttpSession session,DailyReportForm dailyReportForm, BindingResult result,
			Model model, String selectDate, RedirectAttributes redirectAttributes) {
		System.out.println("提出ボタン押下:"+dailyReportForm);
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");

		// 入力チェック
		String validationErrors = dailyReportService.validateDailyReport(dailyReportForm, selectDate);

		// エラーが存在する場合
		if (!validationErrors.isEmpty()) {
			// ステータスを取得
			String statusText = (String) session.getAttribute("statusText");
			
			model.addAttribute("registerError", validationErrors);
			model.addAttribute("loginUser", loginUser);
			model.addAttribute("dailyReportForm", dailyReportForm);
			model.addAttribute("statusText", statusText);
			model.addAttribute("selectDate", selectDate);
			return "report/dailyReport";
		}

		// 登録処理
		String message = dailyReportService.submitDailyReport(dailyReportForm, loginUser, selectDate);
		redirectAttributes.addFlashAttribute("message", message);

		// status変更
		dailyReportService.changeStatus(loginUser, selectDate, 1);

		return "redirect:/report/dailyReport";
	}
	
	/**
	 * 日報登録画面 マネージャ権限 日報申請の詳細表示
	 * 
	 * @param id      日報ID
	 * @param model
	 * @param session
	 * @return 日報登録画面
	 */
	@GetMapping(path = "/report/dailyReport/detail")
	public String getDailyReport(@RequestParam("id") Integer id, Model model, HttpSession session) {
	    // ユーザー情報の取得
	    Users loginUser = (Users) session.getAttribute("user");

	    // 申請Idで日報情報を取得
	    DailyReport dailyReport = dailyReportMapper.findById(id);
	    // 日報情報から日付とユーザーIDを取得
	    Integer userId = dailyReport.getUserId();
	    java.util.Date utilDate = dailyReport.getDate();
	    java.sql.Date date = new java.sql.Date(utilDate.getTime());

	    // 日報情報を取得
		DailyReportForm dailyReportForm = dailyReportService.setForm(userId,date);  

	    // モデルに属性を追加
		model.addAttribute("dailyReport",dailyReport);
	    model.addAttribute("dailyReportForm", dailyReportForm);
	    model.addAttribute("loginUser", loginUser);

	    return "report/dailyReport";
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
