package com.example.demo.form;

import java.util.Date;

import lombok.Data;

@Data
public class LoginForm {
	
	/** ユーザーID */
	private Integer id;
	/** パスワード */
	private String password;
	/** 氏名 */
	private String name;
	/** 役職 */
	private String role;
	/** 有効開始日 */
	private Date startDate;

}
