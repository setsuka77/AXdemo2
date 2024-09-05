package com.example.demo.dto;

import java.util.Date;

import lombok.Data;
@Data
public class DailyReportDto {
	/** 日報ID */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 日付 */
	private Date date;
	/** 申請状況 */
	private Integer status;
	/** ユーザーの名前*/
	private String UserName;
}
