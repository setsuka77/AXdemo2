package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import com.example.demo.entity.Users;
import com.example.demo.service.UserManagementService;

@Controller
public class UserManagementController {

    @Autowired
    private UserManagementService userService;

    @RequestMapping("/userManagement/list")
    public String userManage(Model model) {
        model.addAttribute("userManagementDto", new Users());
        return "userManagement/list";
    }

    @GetMapping("/api/users/search")
    public ResponseEntity<Users> searchUser(@RequestParam String name) {
        Users user = userService.findByUsername(name);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(user);
        }
    }

//    @PostMapping("/api/users/register")
//    public String registerUser(@ModelAttribute Users user, Model model) {
//        Users existingUser = userService.findByUsername(user.getName());
//        if (existingUser != null) {
//            model.addAttribute("errorMessage", "既に同じユーザ名が登録されています。");
//            return "userManagement/list";
//        }
//
//        userService.registerUser(user);
//        model.addAttribute("successMessage", "ユーザを登録しました。");
//
//        return "redirect:/userManagement/list";
//    }
}
