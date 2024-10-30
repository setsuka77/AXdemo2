package com.example.demo.dto;

import lombok.Data;

@Data
public class TasktypeDto {
	
	/** 作業種ID */
	private Integer workTypeId;
	/** リストナンバー */
	private Integer listNumber;
	/** 作業種名 */
	private String workTypeName;
	/** 活性フラグ(1が活性) */
	private Integer isActive;

}
