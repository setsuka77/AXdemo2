package com.example.demo.entity;

import java.util.Date;

import lombok.Data;

/*
 * 日報情報エンティティ
 * テーブル：daily_report_detail
 */
@Data
public class DailyReportDetail {
	/** シーケンス */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 日付 */
	private Date date;
	/** 時間 */
	private Integer time;
	/** 備考 */
	private String content;
}
