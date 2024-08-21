package com.example.demo.entity;

import lombok.Data;

/*
 * 部署情報エンティティ
 * テーブル：Department
 */
@Data
public class Department {
	/** 部署ID */
	private Integer departmentId;
	/** 部署名 */
	private String name;
	/** 有効フラグ */
	private Byte isActive;

}
