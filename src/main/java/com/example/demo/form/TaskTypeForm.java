package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskTypeForm {
	/** 作業種ID */
	private Integer workTypeId;
	/** 表示順番 */
	private Integer listNumber;
	/** 作業種名 */
	@NotBlank(message = "作業種名は必須です")
	private String workTypeName;

}
