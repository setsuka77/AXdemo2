package com.example.demo.entity;

import java.sql.Time;
import java.util.Date;

import lombok.Data;

/*
 * 勤怠登録情報エンティティ
 * テーブル：attendance
 */
@Data
public class Attendance {
	
	/** 勤怠ID */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 勤務状況 */
	private Integer status;
	/**日付 */
	private Date date;
	/** 勤務開始時刻 */
	private Time startTime;
	/** 勤務開始時刻 */
	private Time endTime;
	/** 備考 */
	private String remarks;
}
