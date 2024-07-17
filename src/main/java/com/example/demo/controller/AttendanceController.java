package com.example.demo.controller;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
 
@Controller
public class AttendanceController {
 
	/**
	 * 勤怠登録画面 初期表示
	 *
	 * @param lmsUserId
	 * @param courseId
	 * @param model
	 * @return 勤怠登録画面
	 */
	@GetMapping("/attendance")
	public String showAttendanceForm(Model model) {
 
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
		return "attendance/record"; // 初期画面のテンプレート名
	}
 
	/**
	 * 「表示」ボタン押下
	 * @param model
	 * @return 勤怠登録画面
	 */
	@PostMapping("/attendance")
	public String showAttendance(@RequestParam("year") int year,
			@RequestParam("month") int month,
			Model model) {
		LocalDate date = LocalDate.of(year, month, 1);
		int daysInMonth = date.getMonth().length(date.isLeapYear());
		List<LocalDate> dates = new ArrayList<>();
		for (int day = 1; day <= daysInMonth; day++) {
			
			dates.add(date.withDayOfMonth(day));
			
		}
 
		model.addAttribute("dates", dates);
		model.addAttribute("selectedMonth", year + "年" + String.format("%02d", month) + "月");
		
		return "attendance/record"; // 表示するテンプレート名
	}
	
	/*
	 * 「登録」ボタン押下
	 * @param model
	 * @return 勤怠登録画面
	 */
	
	@RequestMapping(path = "/attendance", params = "regist", method = RequestMethod.POST)
	public String regist(Model model) {
		
		
		return "attendance/record";
	}
	
	
	
	/*
	 * 「登録申請」ボタン押下
	 * @param model
	 * @return 勤怠登録画面
	 */
	@RequestMapping(path = "/attendance", params = "request", method = RequestMethod.POST)
	public String request(Model model) {
		
		
		return "attendance/record";
	}
}