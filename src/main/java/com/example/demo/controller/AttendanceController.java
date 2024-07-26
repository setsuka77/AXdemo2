package com.example.demo.controller;

import java.sql.Date;
import java.util.ArrayList;
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
import com.example.demo.entity.Attendance;
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

		// 日付リスト作成
		List<CalendarDto> calendar = attendanceService.generateCalendar(year, month);
		// DBから勤怠情報取得
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, loginUser);
		// 勤怠フォームの生成
		AttendanceForm attendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList, loginUser);

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

		// AttendanceForm に date を詰める(エラーメッセージ表示用)
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
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendar, loginUser);
		AttendanceForm dailyAttendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList,
				loginUser);

		model.addAttribute("attendanceForm", dailyAttendanceForm);
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
//	@PostMapping(path = "/attendance", params = "detail")
//	public String getMonthlyAttendanceReqDetail(@RequestParam("id") Integer id, Model model) {
//		MonthlyAttendanceReq req = attendanceService.getMonthlyAttendanceReqById(id);
//		model.addAttribute("monthlyAttendanceReq", req);
//
//		// java.util.Dateをjava.sql.Dateに変換
//		Date sqlDate = new Date(req.getTargetYearMonth().getTime());
//
//		List<Attendance> attendances = attendanceService.getAttendancesForMonth((Date) req.getTargetYearMonth(),
//				req.getUserId());
//		model.addAttribute("attendances", attendances);
//		return "attendance/detail";
//	}


	/**
	 * 承認申請の承認
	 */
//	@PostMapping(path = "/attendance", params = "approve")
//	public String approveMonthlyAttendanceReq(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
//		attendanceService.updateMonthlyAttendanceReqStatus(id, 2); // 承認済みのステータス
//		return "redirect:/attendance";
//	}

	/**
	 * 承認申請の却下
	 */
//	@PostMapping(path = "/attendance", params = "reject")
//	public String rejectMonthlyAttendanceReq(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
//		attendanceService.updateMonthlyAttendanceReqStatus(id, 3); // 却下済みのステータス
//		return "redirect:/attendance";
//	}

}