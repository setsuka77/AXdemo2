package com.example.demo.controller;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.form.UserManagementForm;
import com.example.demo.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserManagementController {
	
	@Autowired
    private UserManagementService userManagementService;


	@RequestMapping("/userManagement/list")
	public String userManage(Model model) {
		model.addAttribute("userForm", new UserManagementForm());
		return "userManagement/list";
	}

	@PostMapping("/search")
    public String searchUser(@ModelAttribute UserManagementForm userForm, Model model) {
        UserManagementDto user = userManagementService.searchUserByName(userForm.getName());
        System.out.println("フォームの名前：" + userForm.getName());

        if (user != null) {
            userForm.setId(user.getId());
            userForm.setPassword(user.getPassword());
            userForm.setRole(user.getRole());
            userForm.setStartDate(user.getStartDate());
        } else {
            userForm.setId(userManagementService.generateUniqueUserId());
        }
        model.addAttribute("userForm", userForm);
        return "userManagement/list";
    }

//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute UserManagementForm userForm, Model model) {
//        if (userForm.getName().isEmpty() || userForm.getPassword().isEmpty() || userForm.getRole().isEmpty() || userForm.getStartDate().isEmpty()) {
//            model.addAttribute("errorMessage", "すべてのフィールドを入力してください。");
//            return "userManagement/list";
//        }
//        userManagementService.registerUser(userForm);
//        return "redirect:/userManagement/list";
//    }
	
}
