package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AttendanceController {

	/*
	 * 勤怠登録画面　初期表示
	 * @param Id
	 * @param name
	 * @return 勤怠登録画面
	 */

	@RequestMapping("/attendance")
	public String login() {
		return "attendance/record";
	}
}
