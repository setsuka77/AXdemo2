package com.example.demo.form;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserManagementForm {

	/** ユーザ名 */
	@NotBlank(message = "ユーザ名 : 20文字以内の全角文字のみで入力してください。")
	@Pattern(regexp = "^[\\u3000-\\uFFFD]{1,20}$", message = "ユーザ名 : 20文字以内の全角文字のみで入力してください。")
	private String name;
	/** ユーザーID */
	private Integer id;
	/** パスワード */
	@NotBlank(message = "パスワード : 16桁以下の半角英数字で入力してください。")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,16}$", message = "パスワード : 16桁以下の半角英数字で入力してください。")
	private String password;
	/** 役職 */
	@NotBlank(message = "権限 : 権限を選択してください。")
	private String role;
	/** 部署ID */
	@NotNull(message = "所属部署 : 所属部署を選択してください。")
	private Integer departmentId;
	/** 有効開始日 */
	@NotBlank(message = "利用開始日 : yyyy/MM/dd のフォーマットで入力してください。")
	@Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "利用開始日 : yyyy/MM/dd のフォーマットで入力してください。")
	@DateTimeFormat(pattern = "yyyy/MM/dd") // 入力フォームのフォーマットに合わせる
	private String startDate;
	
}
