package com.example.demo.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserManagementDto {
	
	/** ユーザ名 */
	private String name;
	/** ユーザーID */
	private String id;
	/** パスワード */
	private String password;
	/** 役職 */
	private String role;
	/** 有効開始日 */
	private String startDate;

}
