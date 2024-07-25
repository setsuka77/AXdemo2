package com.example.demo.form;

import java.util.List;

import lombok.Data;

@Data
public class AttendanceForm {
	
	/**日次勤怠フォームlist作成*/
	private List<DailyAttendanceForm> dailyAttendanceList;

}
