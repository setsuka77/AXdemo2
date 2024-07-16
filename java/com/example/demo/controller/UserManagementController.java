package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserManagementController {

	@RequestMapping("/userManagement/list")
	public String userManage() {
		return "userManagement/list";
	}

}
