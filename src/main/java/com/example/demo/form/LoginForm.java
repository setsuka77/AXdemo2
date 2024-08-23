package com.example.demo.form;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginForm {
	
	/** ユーザーID */
	@NotNull(message = "ユーザIDを入力してください。")
	@Pattern(regexp = "^[0-9]{1,16}$", message = "ユーザーID、パスワードが不正、もしくはユーザーが無効です。")
	private String id;
	/** パスワード */
	@NotNull(message = "パスワードを入力してください。")
	@Pattern(regexp = "^[a-zA-Z0-9]{1,16}$", message = "ユーザーID、パスワードが不正、もしくはユーザーが無効です。")
	private String password;
	/** 氏名 */
	private String name;
	/** 役職 */
	private String role;
	/** 有効開始日 */
	private Date startDate;

}
