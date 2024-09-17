package com.example.demo.dto;

import java.util.Date;

import lombok.Data;

/*
 * ユーザ情報Dto
 * テーブル：Users
 */
@Data
public class UsersDto {

	/** ユーザ名 */
	private String name;
	/** ユーザーID */
	private Integer id;
	/** パスワード */
	// private String password;
	/** 役職 */
	private String role;
	/** 所属部署ID */
	private Integer departmentId;
	/** 有効開始日 */
	private Date startDate;
	/** メールアドレス */
	private String email;
	/** 電話番号 */
	private String phoneNumber;
	/** よみ */
	private String nameKana;

}
