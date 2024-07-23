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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserManagementController {

	@Autowired
	private UserManagementService userManagementService;

	/*
	 * ユーザ管理画面 初期表示
	 */
	@RequestMapping("/userManagement/list")
	public String userManage(Model model) {
		if (!model.containsAttribute("userForm")) {
			model.addAttribute("userForm", new UserManagementForm());
		}
		return "userManagement/list";
	}

	/*
	 * ユーザ管理画面 検索ボタン押下時 検索機能
	 */
	@PostMapping("/search")
	public String searchUser(@ModelAttribute UserManagementForm userForm, Model model,
			RedirectAttributes redirectAttributes) {
		UserManagementDto user = userManagementService.searchUserByName(userForm.getName());
		System.out.println("フォームの名前：" + userForm.getName());

		// 新規か既存かを判断
		if (user != null) {
			// 既存、登録情報表示
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
			// 新規、ID生成
			userForm.setId(userManagementService.generateNewUserId());
			// バリデーションエラー表示
			redirectAttributes.addFlashAttribute("searchError", "存在しないユーザーです。");
			redirectAttributes.addFlashAttribute("userForm", userForm);
			return "redirect:/userManagement/list";
		}

		model.addAttribute("userForm", userForm);
		return "userManagement/list";
	}

	/*
	 * ユーザ管理画面 登録ボタン押下時 登録/更新機能
	 */
	@PostMapping("/register")
	public String registerOrUpdateUser(@ModelAttribute UserManagementForm userForm,Model model,
			RedirectAttributes redirectAttributes) throws ParseException {

		// フォームのデバッグ
		System.out.println("Received UserManagementForm: " + userForm);

		// バリデーションエラー表示
		String errorMessage = userManagementService.validateUserForm(userForm);
		if (errorMessage != null) {
			redirectAttributes.addFlashAttribute("registerError", errorMessage);
			redirectAttributes.addFlashAttribute("userForm", userForm);
			return "redirect:/userManagement/list";
		}

		// startDateが「9999/99/99」の場合にユーザーを削除する
		if ("9999/99/99".equals(userForm.getStartDate())) {
			userManagementService.deleteUser(userForm.getId());
			redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "を削除しました。");
		} else {
			// サービスメソッドを呼び出してユーザ情報を登録/更新
			userManagementService.registerOrUpdateUser(userForm, null);
			redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "を登録/更新しました。");
		}

		redirectAttributes.addFlashAttribute("userForm", new UserManagementForm());
		return "redirect:/userManagement/list";
	}

}
