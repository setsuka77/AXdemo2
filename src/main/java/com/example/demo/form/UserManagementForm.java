package com.example.demo.form;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class UserManagementForm {

	/** ユーザ名 */
	private String name;
	/** ユーザーID */
	private Integer id;
	/** パスワード */
	private String password;
	/** 役職 */
	private String role;
	/** 部署ID */
	private Integer departmentId;
	/** 有効開始日 */
	@DateTimeFormat(pattern = "yyyy/MM/dd") // 入力フォームのフォーマットに合わせる
	private String startDate;
	
}
