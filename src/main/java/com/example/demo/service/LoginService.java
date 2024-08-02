package com.example.demo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.form.LoginForm;
import com.example.demo.mapper.UsersMapper;

@Service
public class LoginService {

	@Autowired
	private UsersMapper usersMapper;
	
	/*
	 * ログイン画面
	 * ユーザ検索、パスワード判定ログイン機能
	 */
	public Users login(Integer id, String password) {
        Users user = usersMapper.findByUserId(id);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
	
	/*
	 * 入力エラーチェック機能
	 */
	public String validateLogin(LoginForm loginForm) {
		System.out.println("メソッドいくかこれ");
		StringBuilder errorMessage = new StringBuilder("ユーザーID、パスワードが不正、もしくはユーザーが無効です。");
	    boolean hasErrors = false;
	    
	    System.out.println("ここまで？");
	    String id = String.valueOf(loginForm.getId());
	    String password = loginForm.getPassword();
	    Date startDate = loginForm.getStartDate();
	    
	    System.out.println("値は変換できてる");
	    if (id != null && id.length() > 16) {
	        hasErrors = true;
	        //errorMessage.append("IDは16文字以内で入力してください。");
	    }

	    if (password != null && password.length() > 16) {
	        hasErrors = true;
	        //errorMessage.append("パスワードは16文字以内で入力してください。");
	    }

	    // 現在の日付を取得
	    Date currentDate = new Date();

	    // startDateがnullでない場合
	    if (startDate != null && startDate.before(currentDate)) {
	            hasErrors = true;
	        }

	    return hasErrors ? errorMessage.toString() : null;
	}
	
	
}
