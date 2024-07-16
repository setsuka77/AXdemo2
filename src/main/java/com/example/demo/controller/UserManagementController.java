package com.example.demo.controller;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import com.example.demo.entity.Users;
//import com.example.demo.service.UserService;

@Controller
public class UserManagementController {

//	@Autowired
//	private UserService userService;

	/*
	 * ユーザ管理画面 初期表示
	 * 
	 */
	@RequestMapping("/userManagement/list")
	public String userManage() {
		return "userManagement/list";
	}

//	@GetMapping("/search")
//	public ResponseEntity<Users> searchUser(@RequestParam String name) {
//		Users user = userService.findByUsername(name);
//		if (user == null) {
//			return ResponseEntity.notFound().build();
//		} else {
//			return ResponseEntity.ok(user);
//		}
//	}
//
//	@PostMapping("/register")
//	public ResponseEntity<String> registerUser(@RequestBody Users user) {
//		// 重複チェック
//		Users existingUser = userService.findByUsername(user.getName());
//		if (existingUser != null) {
//			return ResponseEntity.badRequest().body("既に同じユーザ名が登録されています。");
//		}
//
//		// ユーザ登録
//		userService.registerUser(user);
//
//		return ResponseEntity.ok("ユーザを登録しました。");
//	}

}
