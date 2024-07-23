package com.example.demo.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		//ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		
		//年リストと月リスト作成
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
		model.addAttribute("loginUser",loginUser);
		return "attendance/record"; 
	}
 

	/**
	 * 「表示」ボタン押下
	 * 
	 * @param model
	 * @return 勤怠登録画面
	 */
	 @PostMapping("/attendance")
	 public String showAttendance(@RequestParam("year") Integer year, @RequestParam("month") Integer month, Model model,HttpSession session) {
		//ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		 //  年と月に必須チェックを入れる
	     if (year == null || month == null) {
	         model.addAttribute("error", "年と月を選択してください");
	         return "attendance/record";
	     }
	     //日付リスト作成
	     List<CalendarDto> calendarList = attendanceService.generateCalendar(year, month);
	     //DBから勤怠情報取得
	     List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendarList,loginUser);
	     System.out.println(attendanceDtoList);
	     //勤怠フォームの生成
	     AttendanceForm attendanceForm= attendanceService.setAttendanceForm(calendarList,attendanceDtoList);
	     System.out.println(attendanceForm);

	     model.addAttribute("loginUser",loginUser);
	     model.addAttribute("calendarList", calendarList);
	     model.addAttribute("attendanceForm",attendanceForm);
	     return "attendance/record";
	 }


	
	/*
	 * 「登録」ボタン押下
	 * @param model
	 * @return 勤怠登録画面
	 */
	/*@RequestMapping(path = "/attendance", params = "regist", method = RequestMethod.POST)
	public String registAttendance(AttendanceForm attendanceForm,Model model) {
		//登録処理
		attendanceService.registerAttendance();
		//一覧の再取得
		List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance();
		
		return "attendance/record";
	}*/
	
	
	
	/*
	 * 「登録申請」ボタン押下
	 * @param model
	 * @return 勤怠登録画面
	 */
	/*	@RequestMapping(path = "/attendance", params = "request", method = RequestMethod.POST)
		public String request(Model model) {
			
			
			return "attendance/record";
		}*/
	}