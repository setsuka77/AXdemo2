package com.example.demo.form;

import com.example.demo.entity.Department;

import jakarta.validation.constraints.Size;
import lombok.Data;

/*
 * 部署情報フォーム
 * 
 */
@Data
public class DepartmentForm {
	/** 部署ID */
	private Integer departmentId;
	/** 部署名 */
	private String name;
	/** 有効フラグ */
	private Byte isActive;
	
	/** 新規部署名 */
	@Size(max = 20, message = "新しい部署名は20文字以内にしてください。")
    private String newDepartment;
    
    /** 登録済部署名 */
    private String currentDepartment;
	
    // フォームからエンティティへの変換メソッド
    public Department toEntity() {
        Department department = new Department();
        department.setName(this.newDepartment);
        department.setIsActive(this.isActive != null ? this.isActive : 1);  // isActiveのデフォルト値を1に
        return department;
    }
}
