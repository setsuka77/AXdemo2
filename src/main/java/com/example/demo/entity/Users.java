package com.example.demo.entity;

import java.util.Date;

import lombok.Data;

/*
 * ユーザ情報エンティティ
 * テーブル：Users
 */
@Data
public class Users {

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
	private Date startDate;
}

