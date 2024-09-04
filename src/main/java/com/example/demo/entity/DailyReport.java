package com.example.demo.entity;

import java.util.Date;

import lombok.Data;

/*
 * 日報申請情報エンティティ
 * テーブル：daily_report
 */
@Data
public class DailyReport {
	/** 日報ID */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 日付 */
	private Date date;
	/** 申請状況 */
	private Integer status;
}
