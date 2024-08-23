package com.example.demo.controller;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.entity.Users;
import com.example.demo.form.UserManagementForm;
import com.example.demo.service.UserManagementService;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/userManagement")
public class UserManagementController {

	@Autowired
	private UserManagementService userManagementService;

	/**
	 * ユーザ管理画面 初期表示
	 * 
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @return ユーザ管理画面
	 */
	@GetMapping("/manage")
	public String userManage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		// アクセス権限確認(管理者、マネージャのみ)
		Users user = (Users) session.getAttribute("user");
		if (user == null || !"1".equals(user.getRole()) && !"2".equals(user.getRole())) {
			redirectAttributes.addFlashAttribute("error", "アクセス権限がありません。");
			return "redirect:/";
		}

		// 登録ボタンを初期表示時に非活性に設定
		model.addAttribute("checkRegister",
				session.getAttribute("checkRegister") != null ? session.getAttribute("checkRegister") : true);

		// バリデーションエラー時入力保持
		if (!model.containsAttribute("userForm")) {
			model.addAttribute("userForm", new UserManagementForm());
		}
		return "userManagement/manage";
	}

	/**
	 * ユーザ管理画面 検索ボタン押下時 検索機能
	 * 
	 * @param userForm           ユーザ管理フォーム
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @return ユーザ管理画面
	 */
	@PostMapping("/search")
	public String searchUser(@ModelAttribute UserManagementForm userForm, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {

		UserManagementDto userDto = userManagementService.searchUserByName(userForm.getName());

		// 新規か既存かを判断
		if (userDto != null) {
			// 既存、登録情報表示
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
			// 検索成功時は登録ボタンを活性に
			session.setAttribute("checkRegister", false);
		} else {
			// 新規、ID生成
			userForm.setId(userManagementService.generateNewUserId());
			// バリデーションエラー表示
			redirectAttributes.addFlashAttribute("searchError", "存在しないユーザです。");
			redirectAttributes.addFlashAttribute("userForm", userForm);
			// 検索エラー時は登録ボタンを活性に
			session.setAttribute("checkRegister", false);
			return "redirect:/userManagement/manage";
		}

		model.addAttribute("userForm", userForm);
		// 検索成功時は登録ボタンを活性に
		session.setAttribute("checkRegister", false);
		return "userManagement/manage";
	}

	/**
	 * ユーザ管理画面 登録ボタン押下時 登録/更新機能
	 * 
	 * @param userForm           ユーザ管理フォーム
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @return リダイレクト先のURL /userManagement/manage
	 * @throws ParseException
	 */
	@PostMapping("/register")
	public String registerOrUpdateUser(@ModelAttribute @Validated UserManagementForm userForm,
			BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes)
			throws ParseException {

		// ユーザ名が既に登録されているかチェック（IDが存在しない場合）
		String userNameError = userManagementService.checkUserNameConflict(userForm.getName(), userForm.getId());
		if (userNameError != null) {
			bindingResult.rejectValue("name", "error.userForm", userNameError);
		}

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
			StringBuilder errorMessage = new StringBuilder("ユーザー登録/更新に失敗しました。<br>");
			errors.forEach((field, messages) -> {
				messages.forEach(message -> errorMessage.append(message).append("<br>"));
			});

			redirectAttributes.addFlashAttribute("registerError", errorMessage.toString());
			redirectAttributes.addFlashAttribute("userForm", userForm);
			session.setAttribute("checkRegister", false);
			return "redirect:/userManagement/manage";
		}

		// 登録/更新、論理削除処理を行う
		boolean isDeleted = userManagementService.registerOrUpdateUser(userForm, null);
		if (isDeleted) {
			redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "は削除されました。");
		} else {
			redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "を登録/更新しました。");
		}
		// 登録、更新、削除が成功した場合は登録ボタンを活性に
		session.setAttribute("checkRegister", true);

		redirectAttributes.addFlashAttribute("userForm", new UserManagementForm());
		return "redirect:/userManagement/manage";
	}

	/*
	 * ユーザ管理画面 「メニュー」ボタン押下時 セッション切断処理
	 */
	@PostMapping(path = "/manage", params = "back")
	public String backMenu(HttpSession session, RedirectAttributes redirectAttributes) {

		// sessionを削除
		session.removeAttribute("checkRegister");

		return "redirect:/index";
	}
}
