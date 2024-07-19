package com.example.demo.controller;

import com.example.demo.dto.UserManagamentDto;
import com.example.demo.form.UserManagementForm;
import com.example.demo.service.UserManagementService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/userManagement")
public class UserManagementController {

	@Autowired
	private UserManagementService userService;

	@GetMapping("/list")
	public String userManage(Model model) {
		model.addAttribute("userManagementForm", new UserManagementForm());
		return "userManagement/list";
	}

	/*
	 * 検索ボタン 押下
	 */
//	@GetMapping("/search")
//	@ResponseBody
//	public UserManagementForm searchUser(@RequestParam String name, Model model) {
//		// 検索処理を実装する
//		List<UserManagamentDto> UserListForSearch = userService.getUserListForSearch(name);
//
//		// 結果をモデルに追加
//		model.addAttribute("UserDetailDto", userDetailForSearch);
//		model.addAttribute("userName", userName);
//
//		return "userManagement/list";
//	}

//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute("userManagementForm") @Validated UserManagementForm form,
//                               BindingResult bindingResult,Model model) {
//        if (bindingResult.hasErrors()) {
//            return "userManagement/list";
//        }
//
//        // 重複チェック
//        if (userService.existsByUsername(form.getName())) {
//            model.addAttribute("errorMessage", "既に同じユーザ名が登録されています。");
//            return "userManagement/list";
//        }
//
//        // ユーザ登録
//        userService.registerUser(form);
//
//        model.addAttribute("successMessage", "ユーザを登録しました。");
//        return "redirect:/userManagement/list";
//    }

}
