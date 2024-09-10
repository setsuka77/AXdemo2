package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Department;
import com.example.demo.form.DepartmentForm;
import com.example.demo.mapper.DepartmentMapper;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentMapper departmentMapper;

	/**
	 * 部署登録画面 プルダウン用 全ての部署を取得
	 * 
	 * @return 部署情報のリスト
	 */
	public List<DepartmentDto> findAllDepartments() {
		return departmentMapper.findAll();
	}

	/**
	 * 部署登録画面 部署名 前方一致検索
	 * 
	 * @param name 検索する部署名の部分文字列
	 * @return 部署名が一致する部署のリスト
	 */
	public List<DepartmentDto> searchDepartmentsByName(String name) {
		return departmentMapper.findByNameLike(name + "%");
	}

	/**
	 * 部署登録画面 新規部署登録処理
	 * 
	 * @param departmentForm 登録する部署の情報を含むフォーム
	 * @return エラーメッセージがある場合はそのメッセージ、なければ null
	 */
	public String registerDepartment(DepartmentForm departmentForm) {
		// 入力された部署名が既に登録されているかチェック
		Department existingDepartment = departmentMapper.findByName(departmentForm.getNewDepartment());
		if (existingDepartment != null) {
			return "この部署名は既に登録されています。新たな部署名を入力してください。";
		}
		// 新規部署登録処理
		departmentMapper.insert(departmentForm.toEntity());
		return null;
	}

	/**
	 * 部署登録画面 既存部署名更新処理
	 * 
	 * @param departmentForm 更新する部署の情報を含むフォーム
	 * @return エラーメッセージがある場合はそのメッセージ、なければ null
	 */
	public String updateDepartment(DepartmentForm departmentForm) {
		String newDepartment = departmentForm.getNewDepartment();
		String currentDepartment = departmentForm.getCurrentDepartment();

		// 現在の部署名と新しい部署名が同じ場合はエラーメッセージを返す
		if (currentDepartment.equals(newDepartment)) {
			return "この部署名は既に登録されています。新たな部署名を入力してください。";
		}

		// 入力された部署名が既に登録されているかチェック
		Department existingDepartment = departmentMapper.findByName(departmentForm.getNewDepartment());
		if (existingDepartment != null) {
			return "この部署名は既に登録されています。新たな部署名を入力してください。";
		}

		// プルダウン選択した部署のIDを取得
		Department searchDepartment = departmentMapper.findByName(currentDepartment);
		// 部署情報を更新
		Department department = new Department();
		department.setDepartmentId(searchDepartment.getDepartmentId());
		department.setName(newDepartment);
		department.setIsActive((byte) 1);

		departmentMapper.update(department);
		return null;
	}

	/**
	 * 部署登録画面 選択した部署を停止（論理削除）
	 * 
	 * @param currentDepartment 停止する部署の名前
	 */
	public void deactivateDepartment(String currentDepartment) {
		Department searchDepartment = departmentMapper.findByName(currentDepartment);

		if (searchDepartment != null) {
			// 部署情報を停止状態に更新
			Department department = new Department();
			department.setDepartmentId(searchDepartment.getDepartmentId());
			department.setName(currentDepartment + " [停止中]");
			department.setIsActive((byte) 0);

			departmentMapper.update(department);
		}
	}

	/**
	 * 部署登録画面 選択した停止中の部署を再開(更新処理)
	 * 
	 * @param currentDepartment 再開する部署の名前
	 */
	public String restartDepartment(String currentDepartment) {
		Department searchDepartment = departmentMapper.findByName(currentDepartment);

		if (searchDepartment != null) {
			// 部署情報を再開状態に更新
			Department department = new Department();
			department.setDepartmentId(searchDepartment.getDepartmentId());
			String newName = currentDepartment.replace(" [停止中]", "");
			department.setName(newName);
			department.setIsActive((byte) 1);

			departmentMapper.update(department);
			// 再開後の部署名を返す
			return newName;
		}
		// 部署が見つからない場合は null を返す
		return null;
	}
}
