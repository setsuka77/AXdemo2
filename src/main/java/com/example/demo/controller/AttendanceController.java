package com.example.demo.controller;
 
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.AttendanceDto;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.LoginService;
 
@Controller
public class AttendanceController {
	
	@Autowired
	private AttendanceMapper attendanceMapper;
	@Autowired
	private LoginService loginService;
	@Autowired
	private AttendanceService attendanceService;
		
	Integer userId = 2;
	
	
	/**
	 * 勤怠登録画面 初期表示
	 *
	 * @param model
	 * @return 勤怠登録画面
	 */
	@GetMapping("/attendance")
	public String showAttendanceForm(Model model) {
		//ユーザー情報の取得(name,ID,roleが渡される)
		//Users loginUser = UsersMapper.findByIdAndNameAndRole();
		
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
		//model.addAttribute("loginUser",loginUser);
		return "attendance/record"; 
	}
 

	/**
	 * 「表示」ボタン押下
	 * 
	 * @param model
	 * @return 勤怠登録画面
	 */
	 @PostMapping("/attendance")
	 public String showAttendance(@RequestParam("year") Integer year, @RequestParam("month") Integer month, Model model) {
	     // ① 年と月に必須チェックを入れる
	     if (year == null || month == null) {
	         model.addAttribute("error", "年と月を選択してください");
	         return "attendance/record";
	     }

	     // ② 年と月の情報を1つに合わせ、Date型date（日付）へと変換する
	     LocalDate localDate = LocalDate.of(year, month, 1);
	     Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

	     // ③ user情報を取得し、userIdを用いてデータベースに入力された年月の勤怠情報がないか確認する
	    // Users loginUser = loginService.getInfo();
	    // int userId = loginUser.getId();
	     List<AttendanceDto> attendanceList = attendanceService.getAttendanceByUserIdAndMonth(userId, year, month);

	     if (!attendanceList.isEmpty()) {
	         // ④ 存在した場合はその情報を表示させる
	         model.addAttribute("attendanceList", attendanceList);
	     } else {
	         // ④ 存在しない場合は新しくその年月の日付と勤怠IDを登録し、その情報を表示させる
	         int daysInMonth = localDate.lengthOfMonth();
	         List<LocalDate> dates = new ArrayList<>();
	         for (int day = 1; day <= daysInMonth; day++) {
	             dates.add(localDate.withDayOfMonth(day));
	         }

	         // 新しい勤怠情報を登録する
	         for (LocalDate dateItem : dates) {
	             AttendanceDto newAttendance = new AttendanceDto();
	             newAttendance.setUserId(userId);
	             newAttendance.setDate(Date.from(dateItem.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	             attendanceService.registerAttendance(newAttendance);
	         }

	         // 登録した勤怠情報を再取得して表示
	         attendanceList = attendanceService.getAttendanceByUserIdAndMonth(userId, year, month);
	         model.addAttribute("attendanceList", attendanceList);
	     }

	     model.addAttribute("selectedYear", year);
	     model.addAttribute("selectedMonth", month);
	    // model.addAttribute("loginUser", loginUser);

	     return "attendance/record";
	 }

	
	/*
	 * 「登録」ボタン押下
	 * @param model
	 * @return 勤怠登録画面
	 */
	
	/*@RequestMapping(path = "/attendance", params = "regist", method = RequestMethod.POST)
	public String registAttendance(AttendanceDto attendanceDto,Model model) {
		//登録処理
		attendanceService.registerAttendance(attendanceDto);
		//登録したものを再度表示させる
		Integer year = AttendanceDto 
		List<AttendanceDto> attendanceList = attendanceService.getAttendanceByUserIdAndMonth(userId, selectedYear, selectedMonth);
		model.addAttribute("attendanceList", attendanceList);
		return "attendance/record";
	}*/
	
	
	
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