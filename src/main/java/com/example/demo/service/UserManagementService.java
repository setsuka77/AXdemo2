package com.example.demo.service;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.form.UserManagementForm;
import com.example.demo.mapper.UsersMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementService {

	@Autowired
	private UsersMapper usersMapper;

	/**
	 * ユーザ管理画面 ユーザ名検索、取得
	 * 
	 * @param name
	 * @return ユーザ管理DTO
	 */
	@Transactional(readOnly = true)
	public UserManagementDto searchUserByName(String name) {
		UserManagementDto user = usersMapper.findByName(name);

		if (user == null) {
			return null;
		}
		UserManagementDto dto = new UserManagementDto();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setPassword(user.getPassword());
		dto.setRole(user.getRole());
		dto.setStartDate(user.getStartDate());
		return dto;
	}

	/**
	 * ユーザ管理画面 新規ユーザのユーザID生成
	 * 
	 * @return 新規ユーザID
	 */
	public Integer generateNewUserId() {
		Integer maxId = usersMapper.findMaxId();
		return (maxId == null) ? 1 : maxId + 1;
	}

	/**
	 * ユーザ管理画面 新規ユーザ情報登録 既存ユーザ情報更新
	 * 
	 * @param userForm ユーザ管理フォーム
	 * @param id       ユーザID
	 */
	public void registerOrUpdateUser(UserManagementForm userForm, Integer id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		// 入力内容取得
		UserManagementDto user = new UserManagementDto();
		user.setName(userForm.getName());
		user.setPassword(userForm.getPassword());
		user.setRole(userForm.getRole());

		// startDateをStringからLocalDateに変換
		if (userForm.getStartDate() != null && !userForm.getStartDate().isEmpty()) {
			LocalDate startDate = LocalDate.parse(userForm.getStartDate(), formatter);
			user.setStartDate(startDate);
		}

		// フォームのIDが存在するか確認
		Integer userId = userForm.getId();
		UserManagementDto existingUser = null;

		if (userId != null) {
			existingUser = usersMapper.identifyUserId(userId);
		}

		if (existingUser == null) {
			// 新規登録実行
			user.setId(userId);
			usersMapper.insertUser(user);
		} else {
			// 既存更新実行
			user.setId(userId);
			usersMapper.updateUser(user);
		}
	}

	/**
	 * ユーザ管理画面 既存ユーザ削除機能
	 * 
	 * @param id ユーザID
	 */
	public void deleteUser(Integer id) {
		if (id != null) {
			usersMapper.deleteUser(id);
		}
	}

	/**
	 * ユーザ管理画面 文字数制限・日付形式チェック
	 * 
	 * @param userForm ユーザ管理フォーム
	 * @return エラーメッセージ(エラーがない場合は null)
	 */
	public String validateUserForm(UserManagementForm userForm) {
		StringBuilder errorMessage = new StringBuilder("ユーザー登録/更新に失敗しました。<br>");
		boolean hasErrors = false;

		// 全角文字のみで20文字以内かどうかを確認する正規表現
		String fullWidthAndLengthRegex = "^[\\u3000-\\uFFFD]{1,20}$";

		// ユーザ名チェック
		String name = userForm.getName();
		if (name == null || !name.matches(fullWidthAndLengthRegex)) {
			errorMessage.append("ユーザ名 : 全角文字のみで20文字以内で入力してください。<br>");
			hasErrors = true;
		}

		// パスワードチェック
		String password = userForm.getPassword();
		if (password == null || password.length() > 16) {
			errorMessage.append("パスワード : 桁数は16桁以下で入力してください。<br>");
			hasErrors = true;
		} else {
			// 半角英数字のみかを確認する正規表現
			String halfWidthRegex = "^[a-zA-Z0-9]+$";
			if (!password.matches(halfWidthRegex)) {
				errorMessage.append("パスワード : 半角英数字のみで入力してください。<br>");
				hasErrors = true;
			}
		}

		// 権限チェック
		String role = userForm.getRole();
		if (role == null || role.isEmpty()) {
			errorMessage.append("権限 : 権限を選択してください。<br>");
			hasErrors = true;
		}

		// 利用開始日チェック
		String startDate = userForm.getStartDate();
		if (startDate == null || startDate.isEmpty()) {
			errorMessage.append("利用開始日 : 利用開始日を入力してください。<br>");
			hasErrors = true;
		} else {
			// 日付形式チェック
			Pattern datePattern = Pattern.compile("^\\d{4}/\\d{2}/\\d{2}$");
			if (!datePattern.matcher(startDate).matches()) {
				errorMessage.append("利用開始日 : yyyy/mm/dd のフォーマットで入力してください。<br>");
				hasErrors = true;
			}
		}

		return hasErrors ? errorMessage.toString() : null;
	}

}
