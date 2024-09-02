package com.example.demo.service;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.form.UserManagementForm;
import com.example.demo.mapper.UsersMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

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
	 * ユーザ管理画面 検索ボタン押下後 ユーザ情報をフォームに設定
	 * 
	 * @param userDto  ユーザ管理DTO
	 * @param userForm ユーザ管理フォーム
	 * @return 更新後のユーザ管理フォーム
	 */
	public UserManagementForm setUserFormFromDto(UserManagementDto userDto, UserManagementForm userForm) {
		userForm.setId(userDto.getId());
		userForm.setPassword(userDto.getPassword());
		userForm.setRole(userDto.getRole());
		userForm.setDepartmentId(userDto.getDepartmentId());

		// startDate の型変換
		if (userDto.getStartDate() != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			String formattedDate = userDto.getStartDate().format(formatter);

			// 利用開始日が9999/12/31だった場合の処理
			if ("9999/12/31".equals(formattedDate)) {
				formattedDate = "9999/99/99";
			}

			userForm.setStartDate(formattedDate);
		}

		return userForm;
	}

	/**
	 * ユーザ管理画面 登録ボタン押下時 ユーザ名が既に登録されているかチェック
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
				LocalDate futureDate = LocalDate.of(9999, 12, 31);
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

	/**
	 * バインディング結果からエラーメッセージをフォーマット
	 * 
	 * @param bindingResult
	 * @return フォーマットされたエラーメッセージ
	 */
	public String formatErrors(BindingResult bindingResult) {
		// フィールド順を定義 (フォームの順番)
		List<String> fieldOrder = Arrays.asList("name", "id", "password", "role", "departmentId", "startDate");

		// フィールドごとのエラーメッセージを保持するMap
		Map<String, List<String>> errors = new LinkedHashMap<>();

		// フィールド順に従ってエラーメッセージをソート
		bindingResult.getFieldErrors().stream()
				.sorted(Comparator.comparing(error -> fieldOrder.indexOf(error.getField()))).forEach(error -> {
					String message = error.getDefaultMessage();

					// 同じフィールドに複数のエラーが発生する場合、同じメッセージを重複させない
					if (!errors.containsKey(error.getField()) || !errors.get(error.getField()).contains(message)) {
						errors.computeIfAbsent(error.getField(), k -> new ArrayList<>()).add(message);
					}
				});

		// エラーメッセージを HTML に合成
		if (!errors.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("ユーザ登録/更新に失敗しました。<br>");
			errors.forEach((field, messages) -> {
				messages.forEach(message -> errorMessage.append(message).append("<br>"));
			});
			return errorMessage.toString();
		}
		return "";
	}

}
