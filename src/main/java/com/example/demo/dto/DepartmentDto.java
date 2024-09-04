package com.example.demo.dto;

import lombok.Data;

@Data
public class DepartmentDto {
	/** 部署ID */
	private Integer departmentId;
	/** 部署名 */
	private String name;
	/** 有効フラグ */
	private Byte isActive;

}
