package com.example.demo.dto;

import java.util.Date;

import lombok.Data;
@Data
public class DailyReportDetailDto {
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
	/** 作業種別 */
	private Integer workTypeId;
	/**　作業種別名 */
	private String workTypeName;
}
