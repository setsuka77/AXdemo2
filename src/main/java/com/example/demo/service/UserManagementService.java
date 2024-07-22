package com.example.demo.service;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.form.UserManagementForm;
import com.example.demo.mapper.UsersMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementService {

	@Autowired
	private UsersMapper usersMapper;

	/*
	 * ユーザ管理画面 
	 * ユーザ名検索、取得
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

	/*
	 * ユーザ管理画面 
	 * 新規ユーザのユーザID生成
	 */
	public Integer generateNewUserId() {
		Integer maxId = usersMapper.findMaxId();
		return (maxId == null) ? 1 : maxId + 1;
	}

	/*
	 * ユーザ管理画面 
	 * 新規ユーザ情報登録 
	 * 既存ユーザ情報更新
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
            existingUser = usersMapper.identifyUserId(id);
        }

        if (existingUser == null) {
            // 新規登録
        	System.out.println("新規登録レーン");
            user.setId(userId);
            usersMapper.insertUser(user);
        } else {
            // 更新
        	System.out.println("既存更新レーン");
            user.setId(userId);
            usersMapper.updateUser(user);
        }
	}

}
