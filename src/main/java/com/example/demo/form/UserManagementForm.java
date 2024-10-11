package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
	//@NotBlank(message = "{validation.userManagementForm.password}")
    @Pattern(regexp = "^[a-zA-Z0-9]{0,16}$", message = "{validation.userManagementForm.password}")
	private String password;
	/** 役職 */
	@NotBlank(message = "{notBlank.userManagementForm.role}")
	private String role;
	/** 部署ID */
	@NotNull(message = "{notNull.userManagementForm.departmentId}")
	private Integer departmentId;
	/** 有効開始日 */
	private String startDate;
	/** メールアドレス */
	private String email;
	/** よみ */
	private String nameKana;
	/** 勤務地 */
	private String workPlace;
	
}
