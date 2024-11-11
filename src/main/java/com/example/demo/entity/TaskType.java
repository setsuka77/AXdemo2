package com.example.demo.entity;

import lombok.Data;

@Data
public class TaskType {
	/** 作業種ID */
	private Integer workTypeId;
	/** リストナンバー */
	private Integer listNumber;
	/** 作業種名 */
	private String workTypeName;
	/** ユーザーID */
	private Integer userId;

}
