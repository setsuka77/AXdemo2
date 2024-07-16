package com.example.demo.entity;

import java.util.Date;

import lombok.Data;

@Data
public class Users {

	/** ユーザーID */
	private Integer id;
	/** パスワード */
	private String password;
	/** 氏名 */
	private String name;
	/** 役職 */
	private String role;
	/** 所属部署(部署コード) */
	private Integer departmentId;
	/** 有効開始日 */
	private Date startDate;
}

