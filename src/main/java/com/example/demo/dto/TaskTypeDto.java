package com.example.demo.dto;

import lombok.Data;

@Data
public class TaskTypeDto {
	
	/** 作業種ID */
	private Integer workTypeId;
	/** リストナンバー */
	private Integer listNumber;
	/** 作業種名 */
	private String workTypeName;
	/** ユーザーID */
	private Integer userId;
	

}
