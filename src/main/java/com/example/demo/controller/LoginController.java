package com.example.demo.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Users;
import com.example.demo.form.LoginForm;
import com.example.demo.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private LoginService loginService;

	/**
	 * ログイン画面初期表示
	 * 
	 * @return ログイン画面
	 */
	@RequestMapping("/")
	public String login() {
		return "login/login";
	}

	/**
	 * ログインボタン押下
	 * 
	 * @param loginForm ログインフォーム
	 * @param session
	 * @param redirectAttributes
	 * @return 遷移先のURL
	 */
	@PostMapping("/login")
	public String login(@Validated @ModelAttribute("loginForm")LoginForm loginForm, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes) {
		
//		if (result.hasErrors()) {
//			redirectAttributes.addFlashAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
//			return "redirect:/";
//		}
		
		Integer id = loginForm.getId();
		String password = loginForm.getPassword();

		Users user = loginService.login(id, password);
		session.setAttribute("user", user);

		if (user == null) {
			redirectAttributes.addFlashAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "redirect:/";
		}

		// 利用開始日が現在の日付より前かチェック
		// 現在の日付を取得
		Date currentDate = new Date();
		Date startDate = user.getStartDate();
		// 利用開始日前の場合
		if (startDate != null && startDate.after(currentDate)) {
			redirectAttributes.addFlashAttribute("error", "ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
			return "redirect:/";
		}

		// 権限に応じた画面遷移
		String role = user.getRole();
		if ("1".equals(role) || "2".equals(role) || "3".equals(role) || "4".equals(role)) {
			return "redirect:/index";
		} else {
			redirectAttributes.addFlashAttribute("error", "不明なロールです。");
			return "redirect:/";
		}
	}
	
	/**
     * ログアウト処理
     * 
     * @param session
     * @return ログイン画面へのリダイレクト
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // セッションを無効化してログアウト
        session.invalidate();
        return "redirect:/"; // ログイン画面にリダイレクト
    }
    
    /**
     * 各リクエスト前にセッションをチェック
     * 
     * @param session
     * @param redirectAttributes
     * @return セッションが無効ならログイン画面にリダイレクト
     */
    @ModelAttribute
    public String checkSession(HttpSession session, RedirectAttributes redirectAttributes) {
        Users loginUser = (Users) session.getAttribute("user");
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("error", "セッションが切れました。再度ログインしてください。");
            return "redirect:/";
        }
        return null; // セッションが有効な場合はリダイレクトなし
    }

}
