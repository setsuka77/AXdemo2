package com.example.demo.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserManagamentDto {
	
	/** 氏名 */
	private String name;
	/** ユーザーID */
	private Integer id;
	/** パスワード */
	private String password;
	/** 役職 */
	private String role;
	/** 有効開始日 */
	private Date startDate;
	
	/** ユーザ情報取得Dto(検索用) */
	private List<UserManagamentDto> getUserListForSearch;

}
