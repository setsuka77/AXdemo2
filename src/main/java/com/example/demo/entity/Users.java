package com.example.demo.entity;

import java.util.Date;
import java.time.LocalDate;

import lombok.Data;

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
	/** 有効開始日 */
	private Date startDate;
}

