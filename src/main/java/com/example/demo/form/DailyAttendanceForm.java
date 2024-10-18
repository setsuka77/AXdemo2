package com.example.demo.form;

import java.util.Date;

import lombok.Data;

@Data
public class DailyAttendanceForm {
	/** 勤怠ID */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 勤務状況 */
	private Integer status;
	/** 日付 */
	private Date date;
	/** 日付形式 m/d */
	private String formattedDate;  // フォーマットされた日付を保持
	/** 勤務開始時刻 */
	private String startTime;
	/** 勤務終了時刻 */
	private String endTime;
	/** 備考 */
	private String remarks;
	
	/** エラーFlag */
	private Boolean errorFlag;

}
