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
		dto.setDepartmentId(user.getDepartmentId());
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
	 *ユーザ名が既に登録されているかチェック
	 * 
	 * @param name
	 * @param id
	 * @return
	 */
	
    @Transactional(readOnly = true)
    public String checkUserNameConflict(String name, Integer id) {
        UserManagementDto user = usersMapper.findByName(name);
        if (user != null && !user.getId().equals(id)) {
            return "ユーザ名 : このユーザ名は既に登録されています。別のユーザ名を入力してください。";
        }
        return null;
    }

	/**
	 * ユーザ管理画面 新規ユーザ情報登録 既存ユーザ情報更新 論理削除
	 * 
	 * @param userForm ユーザ管理フォーム
	 * @param id       ユーザID
	 */
	public boolean registerOrUpdateUser(UserManagementForm userForm, Integer id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		boolean isDeleted = false;

		// 入力内容取得
		UserManagementDto user = new UserManagementDto();
		user.setName(userForm.getName());
		user.setPassword(userForm.getPassword());
		user.setRole(userForm.getRole());
		user.setDepartmentId(userForm.getDepartmentId());

		// 9999/99/99が入力された時の論理削除
		// startDateをStringからLocalDateに変換
		if (userForm.getStartDate() != null && !userForm.getStartDate().isEmpty()) {
			if ("9999/99/99".equals(userForm.getStartDate())) {
				// 特殊な未来日として設定
				LocalDate futureDate = LocalDate.of(9999, 12, 31); // 9999年12月31日
				user.setStartDate(futureDate); // LocalDateとして設定
				isDeleted = true; // 削除フラグを立てる
			} else {
				// 通常の日付として処理
				LocalDate startDate = LocalDate.parse(userForm.getStartDate(), formatter);
				user.setStartDate(startDate); // LocalDateとして設定
			}
		} else {
			// startDateがnullまたは空の場合はnullに設定
			user.setStartDate(null);
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

		return isDeleted;
	}

}
