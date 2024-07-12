package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	/*
	 * ログイン画面初期表示
	 *
	 *
	 */
	@RequestMapping("/")
	public String login() {
		return "login/index";
	}

	/*
	 * ログイン画面『ログイン』ボタン押下
	 *
	 */

	@PostMapping("/login")
    public ModelAndView login(
        @RequestParam("userId") String userId,
        @RequestParam("password") String password) {

        // ログインロジックをここに記述
        // 仮にログインが成功した場合
        if ("validUserId".equals(userId) && "validPassword".equals(password)) {
            return new ModelAndView("redirect:/attendance/attendance.html");
        } else {
            ModelAndView modelAndView = new ModelAndView("login/index");
            modelAndView.addObject("error", "Invalid credentials");
            return modelAndView;
        }
    }

}
