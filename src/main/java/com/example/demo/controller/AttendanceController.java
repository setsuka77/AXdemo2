package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.AttendanceDto;
import com.example.demo.dto.CalendarDto;
import com.example.demo.dto.MonthlyAttendanceReqDto;
import com.example.demo.entity.MonthlyAttendanceReq;
import com.example.demo.entity.Users;
import com.example.demo.form.AttendanceForm;
import com.example.demo.service.AttendanceService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;

	/**
	 * 勤怠登録画面 初期表示
	 *
	 * @param model
	 * @param session
	 * @return 勤怠登録画面
	 */
	@GetMapping("/attendance")
	public String showAttendanceForm(Model model, HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");

		// フラッシュ属性からエラーメッセージとフォームデータを取得
		if (model.containsAttribute("registerError")) {
			model.addAttribute("registerError", model.getAttribute("registerError"));
		}
		if (model.containsAttribute("attendanceForm")) {
			model.addAttribute("attendanceForm", model.getAttribute("attendanceForm"));
		}
		if (model.containsAttribute("calendar")) {
			model.addAttribute("calendar", model.getAttribute("calendar"));
		}
		if (model.containsAttribute("year")) {
			model.addAttribute("year", model.getAttribute("year"));
		}
		if (model.containsAttribute("month")) {
			model.addAttribute("month", model.getAttribute("month"));
		}

		// roleが2の時、申請一覧を表示
		if (loginUser != null && "2".equals(loginUser.getRole())) {
			List<MonthlyAttendanceReqDto> monthlyAttendanceReq = attendanceService.findAllAttendance();
			model.addAttribute("monthlyAttendanceReq", monthlyAttendanceReq);
			//却下と承認ボタンを非活性に設定
			boolean checkReject = false;
			model.addAttribute("checkReject", checkReject);
			boolean checkApproval = false;
			model.addAttribute("checkApproval", checkApproval);
		}

		// 承認申請ボタンと登録ボタンを非活性に設定
		boolean checkAllStatus = false;
		model.addAttribute("checkAllStatus", checkAllStatus);
		boolean checkRegister = false;
		model.addAttribute("checkRegister", checkRegister);
		
		// 表示年月に現在の年月を表示
		LocalDateTime nowDate = LocalDateTime.now();
		Integer year = nowDate.getYear();
		Integer month = nowDate.getMonthValue(); 
		model.addAttribute("selectYear", year);
		model.addAttribute("selectMonth", month);

		// 年月プルダウンリストを設定
		setYearMonthList(model);
		model.addAttribute("loginUser", loginUser);
		return "attendance/record";
	}

	/**
	 * プルダウン用 年リストと月リストの設定
	 * 
	 * @param model
	 */
	public void setYearMonthList(Model model) {
		List<Integer> yearList = new ArrayList<>();
		for (int year = 2000; year <= 2100; year++) {
			yearList.add(year);
		}
		List<Integer> monthList = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			monthList.add(month);
		}
		model.addAttribute("yearList", yearList);
		model.addAttribute("monthList", monthList);
	}

	/**
	 * 「表示」ボタン押下
	 * 
	 * @param year
	 * @param month
	 * @param model
	 * @param session
	 * @return 勤怠登録画面
	 */
	@RequestMapping(path = "/attendance", params = "display", method = RequestMethod.POST)
	public String showAttendance(@RequestParam("year") Integer year, @RequestParam("month") Integer month, Model model,
			HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		Integer userId = loginUser.getId();

		// カレンダーの日付リストを作成
		List<CalendarDto> calendar = attendanceService.generateCalendar(year, month);
		// DBから勤怠情報取得
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, userId);
		// 勤怠フォームの生成
		AttendanceForm attendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList, userId);

		// 年月をDate型に変換
		java.sql.Date targetYearMonth = java.sql.Date.valueOf(year + "-" + month + "-01");

		// ステータス表示用のテキストを設定
		List<MonthlyAttendanceReqDto> monthlyAttendanceReq = attendanceService.findByYearMonth(userId, targetYearMonth);
		String status = getStatusText(monthlyAttendanceReq);
		model.addAttribute("statusText", status);

		// ステータスに応じて入力可否を設定する(trueだと非活性化する)
		boolean isFormEditable = "承認待ち".equals(status) || "承認済み".equals(status);
		model.addAttribute("isFormEditable", isFormEditable);

		// 承認申請ボタンは非活性に
		boolean checkAllStatus = false;
		//登録ボタンの表示チェック (True時:活性化)
		boolean checkRegister = attendanceService.checkRegister(status);
		
		//本日の日付を渡す
		LocalDate nowDate = LocalDate.now();
		model.addAttribute("today", nowDate);

		session.setAttribute("calendar", calendar);
		session.setAttribute("selectYear", year);
		session.setAttribute("selectMonth", month);

		model.addAttribute("selectYear", year);
		model.addAttribute("selectMonth", month);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("calendar", calendar);
		model.addAttribute("attendanceForm", attendanceForm);
		model.addAttribute("checkAllStatus", checkAllStatus);
		model.addAttribute("checkRegister", checkRegister);

		// 年月リストを再度設定
		setYearMonthList(model);
		return "attendance/record";
	}

	/*
	 * 上部ステータス表示の設定
	 */
	public String getStatusText(List<MonthlyAttendanceReqDto> monthlyAttendanceReq) {
		if (monthlyAttendanceReq.isEmpty()) {
			return "未申請";
		}

		MonthlyAttendanceReqDto reqDto = monthlyAttendanceReq.get(0);
		Integer status = reqDto.getStatus();

		switch (status) {
		case 1:
			return "承認待ち";
		case 2:
			return "承認済み";
		case 3:
			return "却下";
		default:
			return "未申請";
		}
	}

	/**
	 * 「登録」ボタン押下
	 * 
	 * @param attendanceForm     勤怠フォーム
	 * @param model
	 * @param session
	 * @param redirectAttributes
	 * @return 勤怠登録画面
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/attendance", params = "regist", method = RequestMethod.POST)
	public String registAttendance(AttendanceForm attendanceForm, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		List<CalendarDto> calendar = (List<CalendarDto>) session.getAttribute("calendar");
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		Integer userId = loginUser.getId();

		// AttendanceForm に日付を設定
		attendanceService.fillDatesInAttendanceForm(attendanceForm, calendar);

		// バリデーションエラー表示
		String errorMessage = attendanceService.validateAttendanceForm(attendanceForm, false);
		if (errorMessage != null) {
			model.addAttribute("registerError", errorMessage);
			model.addAttribute("attendanceForm", attendanceForm);
			model.addAttribute("calendar", calendar);
			model.addAttribute("year", calendar.get(0).getDate().getYear());
			model.addAttribute("month", calendar.get(0).getDate().getMonthValue());
			model.addAttribute("loginUser", loginUser);
			model.addAttribute("checkAllStatus", false);
			
			//本日の日付を渡す
			LocalDate nowDate = LocalDate.now();
			model.addAttribute("today", nowDate);

			// 再度リストを設定する
			setYearMonthList(model);
			return "attendance/record";
		}

		// 登録処理
		String message = attendanceService.registAttendance(attendanceForm, loginUser);
		model.addAttribute("message", message);

		// DBから勤怠情報を再取得し、フォームに表示
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, userId);
		AttendanceForm dailyAttendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList, userId);

		// 月初めの日にちを取得
		LocalDate firstDayOfMonth = calendar.get(0).getDate();
		java.sql.Date targetYearMonth = java.sql.Date.valueOf(firstDayOfMonth);

		// ステータス表示用のテキストを設定
		List<MonthlyAttendanceReqDto> monthlyAttendanceReq = attendanceService.findByYearMonth(userId, targetYearMonth);
		String status = getStatusText(monthlyAttendanceReq);
		model.addAttribute("statusText", status);

		// ステータスに応じて入力可否を設定する(trueだと非活性化する)
		boolean isFormEditable = "承認待ち".equals(status) || "承認済み".equals(status);
		model.addAttribute("isFormEditable", isFormEditable);

		// 承認申請ボタンと登録ボタンの表示チェック (True時:活性化)
		boolean checkAllStatus = attendanceService.checkAllStatus(attendanceForm, status);
		boolean checkRegister = attendanceService.checkRegister(status);

		model.addAttribute("attendanceForm", dailyAttendanceForm);
		model.addAttribute("calendar", calendar);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("checkAllStatus", checkAllStatus);
		model.addAttribute("checkRegister", checkRegister);
		
		Integer year = (Integer) session.getAttribute("selectYear");
		Integer month = (Integer) session.getAttribute("selectMonth");
		model.addAttribute("selectYear", year);
		model.addAttribute("selectMonth", month);
		
		//本日の日付を渡す
		LocalDate nowDate = LocalDate.now();
		model.addAttribute("today", nowDate);

		// 再度リストを設定する
		setYearMonthList(model);
		return "attendance/record";
	}

	/**
	 * 勤怠管理画面 メンバー、UM権限 承認申請を登録 承認申請ボタンを押下
	 * 
	 * @param year
	 * @param month
	 * @param session
	 * @param redirectAttributes
	 * @return 勤怠管理画面のリダイレクトURL
	 */
	@PostMapping(path = "/attendance", params = "request")
	public String registerMonthlyAttendanceReq(@RequestParam("year") Integer year, @RequestParam("month") Integer month,
			HttpSession session, RedirectAttributes redirectAttributes) {
		Users loginUser = (Users) session.getAttribute("user");

		// 月次勤怠申請の登録処理
		String message = attendanceService.registerOrUpdateMonthlyAttendanceReq(year, month, loginUser);

		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/attendance";
	}

	/**
	 * 勤怠管理画面 マネージャ権限 月次勤怠申請の詳細表示
	 * 
	 * @param id      申請ID
	 * @param model
	 * @param session
	 * @return 勤怠管理画面
	 */
	@GetMapping(path = "/attendance/detail")
	public String getMonthlyAttendanceDetail(@RequestParam("id") Integer id, Model model, HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		// 申請IDで月次勤怠申請の情報を取得
		MonthlyAttendanceReq monthlyAttendanceReq = attendanceService.getMonthlyAttendanceReqById(id);

		// 申請情報からユーザーIDとターゲット年月を取得
		Integer userId = monthlyAttendanceReq.getUserId();
		java.util.Date targetDate = monthlyAttendanceReq.getTargetYearMonth();

		// Calendarクラスを使用して年と月を取得
		Calendar generateCale = Calendar.getInstance();
		generateCale.setTime(targetDate);
		int year = generateCale.get(Calendar.YEAR);
		int month = generateCale.get(Calendar.MONTH) + 1; // Calendar.MONTHは0から始まるため

		// ターゲット年月を取得して、日付リストと勤怠情報を生成
		List<CalendarDto> calendar = attendanceService.generateCalendar(year, month);
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, userId);
		AttendanceForm dailyAttendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList, userId);

		session.setAttribute("monthlyAttendanceReq", monthlyAttendanceReq);

		model.addAttribute("monthlyAttendanceReq", monthlyAttendanceReq);
		model.addAttribute("attendanceForm", dailyAttendanceForm);
		model.addAttribute("calendar", calendar);
		model.addAttribute("loginUser", loginUser);
		//本日の日付を渡す
		LocalDate nowDate = LocalDate.now();
		model.addAttribute("today", nowDate);

		return "attendance/record";
	}

	/**
	 * 承認申請の承認
	 * 
	 * @param monthlyAttendanceReq 月次勤怠申請情報
	 * @param session
	 * @param redirectAttributes
	 * @return 勤怠管理画面のリダイレクトURL
	 */
	@PostMapping(path = "/attendance", params = "approval")
	public String approvalMonthAttendance(MonthlyAttendanceReq monthlyAttendanceReq, HttpSession session,
			RedirectAttributes redirectAttributes) {
		// 申請情報の取得
		MonthlyAttendanceReq req = (MonthlyAttendanceReq) session.getAttribute("monthlyAttendanceReq");
		// 表示させている申請情報からIDを取得
		Integer id = req.getId();
		// ステータスを変更
		String message = attendanceService.approvalAttendance(id, 2); // 承認済みのステータス
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/attendance";
	}

	/**
	 * 承認申請の却下
	 * 
	 * @param attendanceForm     勤怠フォーム
	 * @param session
	 * @param redirectAttributes
	 * @return 勤怠管理画面のリダイレクトURL
	 */
	@PostMapping(path = "/attendance", params = "rejected")
	public String rejectMonthAttendance(AttendanceForm attendanceForm, HttpSession session,
			RedirectAttributes redirectAttributes) {
		// 申請情報の取得
		MonthlyAttendanceReq req = (MonthlyAttendanceReq) session.getAttribute("monthlyAttendanceReq");
		// 表示させている申請情報からIDを取得
		Integer id = req.getId();
		// ステータスを変更
		String message = attendanceService.rejectAttendance(id, 3); // 却下済みのステータス
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/attendance";
	}
}