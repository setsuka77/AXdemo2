package com.example.demo.form;

import java.util.List;

import lombok.Data;

@Data
public class AttendanceForm {

	/** 勤怠ID */
	/*
	private Integer id;
	/** ユーザーID */
	//private Integer userId;
	//** 勤務状況 */
	/*
	private Integer status;
	*//**日付 */
	/*
	private Date date;
	*//** 勤務開始時刻 */
	/*
	private String startTime;
	*//** 勤務開始時刻 */
	/*
	private String endTime;
	*//** 備考 *//*
				private String remarks;*/
	
	/**日次勤怠フォームlist作成*/
	private List<DailyAttendanceForm> dailyAttendanceList;

}
