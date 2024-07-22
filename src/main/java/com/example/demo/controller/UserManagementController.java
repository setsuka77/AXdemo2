package com.example.demo.controller;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.form.UserManagementForm;
import com.example.demo.service.UserManagementService;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserManagementController {

	@Autowired
	private UserManagementService userManagementService;

	/*
	 * ユーザ管理画面 初期表示
	 */
	@RequestMapping("/userManagement/list")
	public String userManage(Model model) {
		model.addAttribute("userForm", new UserManagementForm());
		return "userManagement/list";
	}

	/*
	 * ユーザ管理画面 検索ボタン押下時 
	 * 検索機能
	 */
	@PostMapping("/search")
	public String searchUser(@ModelAttribute UserManagementForm userForm, Model model) {
		UserManagementDto user = userManagementService.searchUserByName(userForm.getName());
		System.out.println("フォームの名前：" + userForm.getName());

		// 新規か既存かを判断、新規の場合ID生成、既存の場合登録情報表示
		if (user != null) {
			//既存
			userForm.setId(user.getId());
			userForm.setPassword(user.getPassword());
			userForm.setRole(user.getRole());
			// startDate の型変換
            if (user.getStartDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String formattedDate = user.getStartDate().format(formatter);
                userForm.setStartDate(formattedDate); 
            }
            
		} else {
			//新規
			userForm.setId(userManagementService.generateNewUserId());
		}

		model.addAttribute("userForm", userForm);
		return "userManagement/list";
	}

	/*
	 * ユーザ管理画面 登録ボタン押下時
	 * 登録/更新機能
	 */
	@PostMapping("/register")
	public String registerOrUpdateUser(@ModelAttribute UserManagementForm userForm, Model model) throws ParseException {

		// フォームのデバッグ
		System.out.println("Received UserManagementForm: " + userForm);

		// サービスメソッドを呼び出してユーザ情報を登録/更新
		userManagementService.registerOrUpdateUser(userForm, null);

		model.addAttribute("userForm", new UserManagementForm());
		return "redirect:/userManagement/list";
	}

}
