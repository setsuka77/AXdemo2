package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.AttendanceDto;
import com.example.demo.dto.CalendarDto;
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
		// Dateしか存在おらず、DBの情報はない。DtoListではなくDailyAttendanceFormが表示される
		System.out.println("困り中");
		// 勤怠フォームの生成
		AttendanceForm attendanceForm = attendanceService.setAttendanceForm(calendar, attendanceDtoList, loginUser);
		// userId、Dateあり
		System.out.println(attendanceForm);

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
		System.out.println(calendar);
		// dateとuserId以外はコンソールに表示済み
		System.out.println(attendanceForm);

		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");

		// バリデーションエラー表示
		String errorMessage = attendanceService.validateAttendanceForm(attendanceForm,true);
		if (errorMessage != null) {
			redirectAttributes.addFlashAttribute("registerError", errorMessage);
			redirectAttributes.addFlashAttribute("attendanceForm", attendanceForm);
			redirectAttributes.addFlashAttribute("calendar", calendar);
			redirectAttributes.addFlashAttribute("year", calendar.get(0).getDate().getYear());
			redirectAttributes.addFlashAttribute("month", calendar.get(0).getDate().getMonthValue());
			return "redirect:/attendance";
		}

		// 登録処理
		String message = attendanceService.registAttendance(attendanceForm, loginUser, calendar);
		model.addAttribute("mesaage", message);
		System.out.println(message);
		// 再度リストを設定する
		setYearMonthList(model);
		return "attendance/record";
	}

	/**
	 * 勤怠登録画面 承認申請機能 全ての日付が登録完了しているかどうかを確認
	 *
	 * @param attendanceForm
	 * @return 全て登録完了していれば true、そうでなければ false
	 */

	/**
	 * 勤怠登録画面 承認申請機能
	 * 
	 */
	@RequestMapping(path = "/attendance", params = "request", method = RequestMethod.POST)
	public String requestApproval(AttendanceForm attendanceForm, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		List<CalendarDto> calendar = (List<CalendarDto>) session.getAttribute("calendarList");
		Users loginUser = (Users) session.getAttribute("user");

		// バリデーションエラー表示 (承認申請時のみステータスチェック)
		String errorMessage = attendanceService.validateAttendanceForm(attendanceForm, true);
		if (errorMessage != null) {
			redirectAttributes.addFlashAttribute("registerError", errorMessage);
			redirectAttributes.addFlashAttribute("attendanceForm", attendanceForm);
			redirectAttributes.addFlashAttribute("calendar", calendar);
			redirectAttributes.addFlashAttribute("year", calendar.get(0).getDate().getYear());
			redirectAttributes.addFlashAttribute("month", calendar.get(0).getDate().getMonthValue());
			return "redirect:/attendance";
		}

		// 承認申請処理を実行するコードを追加
		// ...

		return "redirect:/attendance";
	}

}