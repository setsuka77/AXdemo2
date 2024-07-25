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

import com.example.demo.dto.AttendanceDto;
import com.example.demo.dto.CalendarDto;
import com.example.demo.entity.Users;
import com.example.demo.form.AttendanceForm;
import com.example.demo.service.AttendanceService;
import com.example.demo.util.DateUtil;

import jakarta.servlet.http.HttpSession;
@Controller
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;
	@Autowired
    private DateUtil dateUtil;

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
	@RequestMapping(path = "/attendance", params = "display", method = RequestMethod.POST)
	 public String showAttendance(@RequestParam("year") Integer year, @RequestParam("month") Integer month, Model model,HttpSession session) {
		//ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
 
	     //日付リスト作成
	     List<CalendarDto> calendarList = attendanceService.generateCalendar(year, month);
	     //DBから勤怠情報取得
	     List<AttendanceDto> attendanceDtoList = attendanceService.checkAttendance(calendarList,loginUser);
	     //Dateしか存在おらず、DBの情報はない。DtoListではなくDailyAttendanceFormが表示される
	     System.out.println("困り中");
	     //勤怠フォームの生成
	     AttendanceForm attendanceForm= attendanceService.setAttendanceForm(calendarList,attendanceDtoList,loginUser);
	     //userId、Dateあり
	     System.out.println(attendanceForm);
	     
	     session.setAttribute("calendarList", calendarList);
	     
	     model.addAttribute("year",year);
	     model.addAttribute("month",month);
	     model.addAttribute("loginUser",loginUser);
	     model.addAttribute("calendarList", calendarList);
	     model.addAttribute("attendanceForm",attendanceForm);
	  // 再度リストを設定する
	        setYearMonthList(model);
	     return "attendance/record";
	 }
	/**
     * 年リストと月リストの設定
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
 
 
	
	/*
	 * 「登録」ボタン押下
	 * @param model
	 * @return 勤怠登録画面
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/attendance", params = "regist", method = RequestMethod.POST)
	public String registAttendance(AttendanceForm attendanceForm,Model model,HttpSession session) {
		
		List<CalendarDto> calendarList = (List<CalendarDto>) session.getAttribute("calendarList");
		System.out.println(calendarList);
		//dateとuserId以外はコンソールに表示済み
		System.out.println(attendanceForm);
		
		//ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		//登録処理
		/*	String message = attendanceService.registAttendance(attendanceForm,loginUser,calendarList);
			model.addAttribute("mesaage",message);
			System.out.println(message);*/
		//一覧の再取得
		model.addAttribute("loginUser",loginUser);
		// 再度リストを設定する
        setYearMonthList(model);
		return "attendance/record";
	}
}