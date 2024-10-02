package com.example.demo.entity;

import java.util.Date;

import lombok.Data;

/*
 * 月次勤怠申請エンティティ
 * テーブル：monthly_attendance_req
 */
@Data
public class MonthlyAttendanceReq {
	
	/** 申請ID */
	private Integer id;
	/** 申請者のユーザーID */
	private Integer userId;
	/** 申請対象年月 */
	private Date targetYearMonth;
	/** 申請日 */
	private Date date;
	/** ステータス */
	private Integer status;
	/** ユーザーの名前*/
	private String userName;
	/** 却下理由 */
	private String comment;
	/** 承認者 */
	private String approverName;
	/** 申請の種類 */
	private String applicationType;
	
}
