package com.example.demo.form;

import java.util.Date;

import lombok.Data;

@Data
public class UserManagementForm {

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
