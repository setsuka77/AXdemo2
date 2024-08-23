package com.example.demo.form;

import java.util.Date;

import jakarta.validation.constraints.Size;
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
	@Size(max = 50, message = "50字以内で入力してください。")
	private String content;
	
	/** エラーFlag */
	private Boolean errorFlag;
}
