package com.example.demo.form;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserManagementForm {

	/** ユーザ名 */
	@NotBlank(message = "{validation.userManagementForm.name}")
	@Pattern(regexp = "^[\\u3000-\\uFFFD]{1,20}$", message = "{validation.userManagementForm.name}")
	private String name;
	/** ユーザーID */
	private Integer id;
	/** パスワード */
	@NotBlank(message = "{validation.userManagementForm.password}")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,16}$", message = "{validation.userManagementForm.password}")
	private String password;
	/** 役職 */
	@NotBlank(message = "{notBlank.userManagementForm.role}")
	private String role;
	/** 部署ID */
	@NotNull(message = "{notNull.userManagementForm.departmentId}")
	private Integer departmentId;
	/** 有効開始日 */
	@NotBlank(message = "{validation.userManagementForm.startDate}")
	@Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "{validation.userManagementForm.startDate}")
	@DateTimeFormat(pattern = "YYYY/MM/DD") // 入力フォームのフォーマットに合わせる
	private String startDate;
	
}
