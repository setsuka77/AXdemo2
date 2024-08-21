package com.example.demo.form;

import java.util.Date;
import java.util.List;

import lombok.Data;
@Data
public class DailyReportForm {
	/** 日報ID */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 日付 */
	private Date date;
	/** 申請状況 */
	private Integer status;
	
	
	/**日次日報フォームlist作成*/
	private List<DailyReportDetailForm> dailyReportDetailFormList;

}