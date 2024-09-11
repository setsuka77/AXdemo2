package com.example.demo.form;

import java.util.Date;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginForm {
	
	/** ユーザーID */
	@Pattern(regexp = "^[0-9]{1,16}$", message = "{pattern.loginForm.idAndPass}")
	private String id;
	/** パスワード */
	@Pattern(regexp = "^[a-zA-Z0-9]{1,16}$", message = "{pattern.loginForm.idAndPass}")
	private String password;
	/** 氏名 */
	private String name;
	/** 役職 */
	private String role;
	/** 有効開始日 */
	private Date startDate;

}
