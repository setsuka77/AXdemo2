package com.example.demo.controller;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.entity.Users;
import com.example.demo.form.UserManagementForm;
import com.example.demo.service.UserManagementService;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		
		// 登録ボタンを非活性に設定
		boolean checkRegister = false;
		model.addAttribute("checkRegister", checkRegister);

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
	    } else {
	        // 新規、ID生成
	        userForm.setId(userManagementService.generateNewUserId());
	        // バリデーションエラー表示
	        redirectAttributes.addFlashAttribute("searchError", "存在しないユーザーです。");
	        redirectAttributes.addFlashAttribute("userForm", userForm);
	        return "redirect:/userManagement/manage";
	    }

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
	@PostMapping("/register")
	public String registerOrUpdateUser(@ModelAttribute UserManagementForm userForm, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) throws ParseException {

		// バリデーションエラー表示
		String errorMessage = userManagementService.validateUserForm(userForm);
		if (errorMessage != null) {
			redirectAttributes.addFlashAttribute("registerError", errorMessage);
			redirectAttributes.addFlashAttribute("userForm", userForm);
			return "redirect:/userManagement/manage";
		}

		// 通常の登録/更新、論理削除処理を行う
        boolean isDeleted = userManagementService.registerOrUpdateUser(userForm, null);
        if (isDeleted) {
            redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "は削除されました。");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", userForm.getName() + "を登録/更新しました。");
        }
	    
		redirectAttributes.addFlashAttribute("userForm", new UserManagementForm());
		return "redirect:/userManagement/manage";
	}
}
