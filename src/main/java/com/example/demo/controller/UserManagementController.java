package com.example.demo.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.dto.UserManagementDto;
import com.example.demo.entity.Users;
import com.example.demo.form.UserManagementForm;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.UserManagementService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserManagementController {

	@Autowired
	private UserManagementService userManagementService;
	@Autowired
	private DepartmentService departmentService;

	/**
	 * ユーザ管理画面 初期表示
	 * 
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @return ユーザ管理画面
	 */
	@GetMapping("/userManagement/manage")
	public String userManage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

		// アクセス権限確認(管理者、マネージャのみ)
		Users user = (Users) session.getAttribute("user");
		if (user == null || !"1".equals(user.getRole()) && !"2".equals(user.getRole())) {
			redirectAttributes.addFlashAttribute("error", "アクセス権限がありません。");
			return "redirect:/";
		}

		// 全ての部署情報を取得(プルダウン用)
		List<DepartmentDto> departments = departmentService.findAllDepartments();
		model.addAttribute("departments", departments);

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
	 * ユーザー管理画面　部署管理画面から遷移したときの初期表示
	 */
	@GetMapping("/userManagement/manage/{name}")
	public String userManageChange(@PathVariable String name, Model model) {
		System.out.println(name);
		// 全ての部署情報を取得(プルダウン用)
		List<DepartmentDto> departments = departmentService.findAllDepartments();
		model.addAttribute("departments", departments);
		
		// 入力ユーザ名でユーザ情報を検索
		UserManagementDto userDto = userManagementService.searchUserByName(name);
		System.out.println(userDto);
		// 既存ユーザの情報をフォームに設定
		UserManagementForm userForm = new UserManagementForm();
		userForm = userManagementService.setUserFormFromDto(userDto, userForm);
		model.addAttribute("userForm", userForm);

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
	@PostMapping(path = "/userManagement/manage", params = "search")
	public String searchUser(@ModelAttribute UserManagementForm userForm, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {
		// 入力ユーザ名でユーザ情報を検索
		UserManagementDto userDto = userManagementService.searchUserByName(userForm.getName());

		// 新規か既存かを判断
		if (userDto != null) {
			// 既存ユーザの情報をフォームに設定
			userForm = userManagementService.setUserFormFromDto(userDto, userForm);

			// 検索成功時は登録ボタンを活性に
			session.setAttribute("checkRegister", false);
		} else {
			// 新規ユーザのID生成とエラー表示
			userForm.setId(userManagementService.generateNewUserId());
			redirectAttributes.addFlashAttribute("searchError", "存在しないユーザです。");
			redirectAttributes.addFlashAttribute("userForm", userForm);

			// 検索エラー時は登録ボタンを活性に
			session.setAttribute("checkRegister", false);
			return "redirect:/userManagement/manage";
		}

		// 全ての部署情報を取得(プルダウン用)
		List<DepartmentDto> departments = departmentService.findAllDepartments();
		model.addAttribute("departments", departments);
		System.out.println("検索ボタン"+userForm);
		model.addAttribute("userForm", userForm);
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
	@PostMapping(path = "/userManagement/manage", params = "register")
	public String registerOrUpdateUser(@Valid @ModelAttribute UserManagementForm userForm, BindingResult bindingResult,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) throws ParseException {
		System.out.println("登録ボタン"+userForm);
		// 全ての部署情報を取得(プルダウン用)
		List<DepartmentDto> departments = departmentService.findAllDepartments();
		model.addAttribute("departments", departments);
	
		// ユーザ名が既に登録されているかチェック（IDが存在しない場合）
		String userNameError = userManagementService.checkUserNameConflict(userForm.getName(), userForm.getId());
		if (userNameError != null) {
			bindingResult.rejectValue("name", "error.userForm", userNameError);
		}

		// エラーメッセージがあれば表示
		if (bindingResult.hasErrors()) {
			String errorMessage = userManagementService.formatErrors(bindingResult);
			redirectAttributes.addFlashAttribute("registerError", errorMessage);
			redirectAttributes.addFlashAttribute("userForm", userForm);
			session.setAttribute("checkRegister", false);
			return "redirect:/userManagement/manage";
		}

		// 登録/更新を行う
		userManagementService.registerOrUpdateUser(userForm);
		redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "を登録/更新しました。");

		// 登録、更新が成功した場合は登録ボタンを活性に
		session.setAttribute("checkRegister", true);

		redirectAttributes.addFlashAttribute("userForm", new UserManagementForm());
		return "redirect:/userManagement/manage";
	}

	/**
	 * ユーザ管理画面 「利用停止」ボタン押下後
	 * 
	 * @param userForm ユーザ管理フォーム
	 * @return リダイレクト先のURL /userManagement/manage
	 */
	@PostMapping(path = "/userManagement/manage", params = "delete")
	public String deleteUser(UserManagementForm userForm, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {
		// 全ての部署情報を取得(プルダウン用)
		List<DepartmentDto> departments = departmentService.findAllDepartments();
		model.addAttribute("departments", departments);

		//ユーザー情報更新処理
		userManagementService.deleteUser(userForm);
		redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "は利用停止中です。");

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
