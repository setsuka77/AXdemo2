package com.example.demo.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AttendanceDto {

	/** 勤怠ID */
	private Integer id;
	/** ユーザーID */
	private Integer userId;
	/** 勤務状況 */
	private Integer status;
	/**日付 */
	private Date date;
	/** 勤務開始時刻 */
	private String startTime;
	/** 勤務開始時刻 */
	private String endTime;
	/** 備考 */
	private String remarks;
	
	/** 勤怠登録情報DTOリスト */
	private List<AttendanceDto> attendanceDto;
	/**月の初日 */
	private Date startDate;
	/**月の最終日 */
	private Date EndDate;
}
