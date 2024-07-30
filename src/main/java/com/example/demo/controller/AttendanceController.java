package com.example.demo.controller;

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
		
		//roleが２の時、申請一覧を表示させる
		if (loginUser != null && "2".equals(loginUser.getRole())) {
			List<MonthlyAttendanceReqDto> monthlyAttendanceReq = attendanceService.findAllAttendance();
			model.addAttribute("monthlyAttendanceReq", monthlyAttendanceReq);
		}

		// プルダウンの設定
		setYearMonthList(model);
		model.addAttribute("loginUser", loginUser);
		return "attendance/record";
	}

	/**
	 * プルダウン用 年リストと月リストの設定
	 * 
	 * @param model
	 */
	private void setYearMonthList(Model model) {
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
	 * @param model
	 * @return 勤怠登録画面
	 */
	@RequestMapping(path = "/attendance", params = "display", method = RequestMethod.POST)
	public String showAttendance(@RequestParam("year") Integer year, @RequestParam("month") Integer month, Model model,
			HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		Integer userId = loginUser.getId();
		
		// 日付リスト作成
		List<CalendarDto> calendar = attendanceService.generateCalendar(year, month);
		// DBから勤怠情報取得
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, userId);
		// 勤怠フォームの生成
		AttendanceForm attendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList, userId);

		session.setAttribute("calendar", calendar);

		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("calendar", calendar);
		model.addAttribute("attendanceForm", attendanceForm);
		// 再度リストを設定する
		setYearMonthList(model);
		return "attendance/record";
	}

	/*
	 * 「登録」ボタン押下
	 * 
	 * @param model
	 * 
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
		
		// AttendanceForm に date を詰める
		attendanceService.fillDatesInAttendanceForm(attendanceForm, calendar);

		// バリデーションエラー表示
		String errorMessage = attendanceService.validateAttendanceForm(attendanceForm, false);
		if (errorMessage != null) {
			redirectAttributes.addFlashAttribute("registerError", errorMessage);
			redirectAttributes.addFlashAttribute("attendanceForm", attendanceForm);
			redirectAttributes.addFlashAttribute("calendar", calendar);
			redirectAttributes.addFlashAttribute("year", calendar.get(0).getDate().getYear());
			redirectAttributes.addFlashAttribute("month", calendar.get(0).getDate().getMonthValue());
			return "redirect:/attendance";
		}

		// 登録処理
		String message = attendanceService.registAttendance(attendanceForm, loginUser);
		model.addAttribute("message", message);
		// DBから一覧を再取得して、再度フォームに表示させる
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, userId);
		AttendanceForm dailyAttendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList,
				userId);

		model.addAttribute("attendanceForm", dailyAttendanceForm);
		model.addAttribute("calendar", calendar);
		model.addAttribute("loginUser", loginUser);
		// 再度リストを設定する
		setYearMonthList(model);
		return "attendance/record";
	}

	/**
	 * 勤怠管理画面 メンバー、UM権限
	 * 承認申請を登録 
	 * 承認申請ボタンを押下
	 */
	@PostMapping(path = "/attendance", params = "request")
	public String registerMonthlyAttendanceReq(@RequestParam("year") Integer year, @RequestParam("month") Integer month,
			HttpSession session, RedirectAttributes redirectAttributes) {
		Users loginUser = (Users) session.getAttribute("user");

		// 月次勤怠申請の登録処理
		String message = attendanceService.registerMonthlyAttendanceReq(year, month, loginUser);

		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/attendance";
	}
	
	/**
	 * 勤怠管理画面 マネージャ権限
	 * 月次勤怠申請の詳細表示
	 */
	@GetMapping(path = "/attendance/detail")
	public String getMonthlyAttendanceDetail(@RequestParam("id") Integer id, Model model,HttpSession session) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		// 申請IDで月次勤怠申請の情報を取得
	    MonthlyAttendanceReq monthlyAttendanceReq = attendanceService.getMonthlyAttendanceReqById(id);
	    
	    // 申請情報からユーザーIDとターゲット年月を取得
	    Integer userId = monthlyAttendanceReq.getUserId();
	    java.util.Date targetDate = monthlyAttendanceReq.getTargetYearMonth();
	    
	    // Calendarクラスを使用して年と月を取得
	    Calendar  generateCale = Calendar.getInstance();
	    generateCale.setTime(targetDate);
	    int year = generateCale.get(Calendar.YEAR);
	    int month = generateCale.get(Calendar.MONTH) + 1; // Calendar.MONTHは0から始まるため

		// ターゲット年月を取得して、日付リストと勤怠情報を生成
		List<CalendarDto> calendar = attendanceService.generateCalendar(year, month);
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, userId);
		AttendanceForm dailyAttendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList, userId);
		
		session.setAttribute("monthlyAttendanceReq", monthlyAttendanceReq);
		
		System.out.println(monthlyAttendanceReq);
		
		model.addAttribute("monthlyAttendanceReq", monthlyAttendanceReq);
		model.addAttribute("attendanceForm", dailyAttendanceForm);
		model.addAttribute("calendar", calendar);
		model.addAttribute("loginUser", loginUser);

	    return "attendance/record";
	}
	
	/**
	 * 承認申請の承認
	 */
	@PostMapping(path = "/attendance", params = "approval")
	public String approvalMontAttendance(MonthlyAttendanceReq monthlyAttendanceReq, Model model,HttpSession session) {
		// 申請情報の取得
		MonthlyAttendanceReq req = (MonthlyAttendanceReq) session.getAttribute("monthlyAttendanceReq");
		// 表示させている申請情報からIDを取得
		Integer id = req.getId();
		//ステータスを変更
		String message = attendanceService.approvalAttendance(id, 2); // 承認済みのステータス
		model.addAttribute("message", message);

	    return "redirect:/attendance";
	}

	/**
	 * 承認申請の却下
	 */
	@PostMapping(path = "/attendance", params = "rejected")
	public String rejectMontAttendance(AttendanceForm attendanceForm, Model model,HttpSession session) {
		// 申請情報の取得
		MonthlyAttendanceReq req = (MonthlyAttendanceReq) session.getAttribute("monthlyAttendanceReq");
		// 表示させている申請情報からIDを取得
		Integer id = req.getId();
		//ステータスを変更
		String message = attendanceService.rejectAttendance(id, 3); // 却下済みのステータス
		model.addAttribute("message", message);
		System.out.println(message);

	    return "redirect:/attendance";
	}


}