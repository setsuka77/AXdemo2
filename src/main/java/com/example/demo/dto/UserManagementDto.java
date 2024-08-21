package com.example.demo.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserManagementDto {
	
	/** ユーザ名 */
	private String name;
	/** ユーザーID */
	private Integer id;
	/** パスワード */
	private String password;
	/** 役職 */
	private String role;
	/** 所属部署ID */
	private Integer departmentId;
	/** 有効開始日 */
	private LocalDate startDate;

}
