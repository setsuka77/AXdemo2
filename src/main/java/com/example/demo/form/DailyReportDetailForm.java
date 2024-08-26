package com.example.demo.form;

import java.util.Date;

import lombok.Data;
@Data
public class DailyReportDetailForm {
	/** シーケンス */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 日付 */
	private Date date;
	/** 作業時間 */
	private Integer time;
	/** 作業内容 */
	private String content;
	
	/** エラーFlag */
	private Boolean errorFlag = false;
}
