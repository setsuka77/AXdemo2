package com.example.demo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.entity.Users;
import com.example.demo.form.UserManagementForm;
import com.example.demo.mapper.UsersMapper;
import com.example.demo.util.DateUtil;

@Service
public class UserManagementService {

	@Autowired
	private UsersMapper usersMapper;
	@Autowired
	private DateUtil dateUtil;

	/**
	 * ユーザ管理画面 ユーザ名検索、取得
	 * 
	 * @param name
	 * @return ユーザ管理DTO
	 */
	@Transactional(readOnly = true)
	public List<UserManagementDto> searchUserByName(String name) {
	    List<UserManagementDto> users = usersMapper.findByName(name);

	    // 検索結果がなければ空のリストを返す
	    if (users == null || users.isEmpty()) {
	        return Collections.emptyList();
	    }

	    return users;
	}
	
	/**
	 * ユーザー情報取得
	 * 
	 * @param name
	 * @param id
	 * @return ユーザ情報
	 */
	public UserManagementDto searchUsers(String name,Integer id) {
		return usersMapper.findByIdAndName(name,id);
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
		userForm.setName(userDto.getName());
		userForm.setEmail(userDto.getEmail());
		userForm.setNameKana(userDto.getNameKana());
		userForm.setWorkPlace(userDto.getWorkPlace());

		// startDate の型変換
		if (userDto.getStartDate() != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String formattedDate = userDto.getStartDate().format(formatter);

			// 利用開始日が9999/12/31だった場合の処理
			if ("9999/12/31".equals(formattedDate)) {
				formattedDate = "9999-99-99";
			}

			userForm.setStartDate(formattedDate);
		}

		return userForm;
	}

	/**
	 * ユーザ管理画面 登録ボタン押下時 ユーザIDが既に登録されているかチェック
	 * 
	 * @param name
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Boolean checkUserNameConflict(Integer id, String name) {
	    // IDでユーザー情報を取得
	    Users existingUser = usersMapper.findById(id);
	    
	    // ユーザーが存在しない場合（重複なし）
	    if (existingUser == null) {
	        return false;
	    } else {
	        // 名前が同じ場合（更新処理）
	        if (existingUser.getName().equals(name)) {
	            return false;
	        }
	        // 名前が違う場合（IDが重複するのでNG）
	        return true; 
	    }
	}


	/**
	 * ユーザ管理画面 新規ユーザ情報登録 既存ユーザ情報更新 
	 * 
	 * @param userForm ユーザ管理フォーム
	 * @param id       ユーザID
	 */
	public Users registerOrUpdateUser(UserManagementForm userForm) {
		// 入力内容取得
		Users user = new Users();
		user.setName(userForm.getName());
		user.setPassword(userForm.getPassword());
		user.setRole(userForm.getRole());
		user.setDepartmentId(userForm.getDepartmentId());
		user.setEmail(userForm.getEmail());
		user.setNameKana(userForm.getNameKana());
		user.setWorkPlace(userForm.getWorkPlace());

		// startDateをStringからLocalDateに変換し、DateUtilを使用してDateに変換
		if (userForm.getStartDate() != null && !userForm.getStartDate().isEmpty()) {
			LocalDate localDate = LocalDate.parse(userForm.getStartDate());
			// DateUtilを使用してLocalDateをDateに変換
			user.setStartDate(dateUtil.localDateToDate(localDate));
		} else {
			// startDateがnullまたは空の場合はnullに設定
			user.setStartDate(null);
		}

		// フォームのIDが存在するか確認
		Integer userId = userForm.getId();
		Users existingUser = null;

		if (userId != null) {
			existingUser = usersMapper.findById(userId);
		}

		if (existingUser == null) {
			// 新規登録実行
			user.setId(userId);
			usersMapper.insertUser(user);
			System.out.println("登録");
		} else {
			//パスワード設定
			if (userForm.getPassword() == null || userForm.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}
			// 既存更新実行
			user.setId(userId);
			usersMapper.updateUser(user);
			System.out.println("更新");
		}
		return user;
	}

	/**
	 * ユーザ管理画面 「利用停止」ボタン押下後
	 * 
	 * @param userForm ユーザ管理フォーム
	 * @param id       ユーザID
	 */
	public void deleteUser(UserManagementForm userForm) {
		// 入力内容取得
		Users user = new Users();
		user.setName(userForm.getName());
		user.setPassword(userForm.getPassword());
		user.setRole(userForm.getRole());
		user.setDepartmentId(userForm.getDepartmentId());
		user.setId(userForm.getId());
		user.setEmail(userForm.getEmail());
		user.setNameKana(userForm.getNameKana());
		user.setWorkPlace(userForm.getWorkPlace());

		// 特殊な未来日として設定
		LocalDate futureDate = LocalDate.of(9999, 12, 31);
		user.setStartDate(dateUtil.localDateToDate(futureDate));

		usersMapper.updateUser(user);
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
