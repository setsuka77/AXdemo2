package com.example.demo.service;

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
	public Boolean validateLogin(LoginForm loginForm) {
	    boolean hasErrors = false;
	    
	    String id = String.valueOf(loginForm.getId());
	    String password = loginForm.getPassword();
	    
	    if (id != null && id.length() > 16) {
	        hasErrors = true;
	    }
	    if (password != null && password.length() > 16) {
	        hasErrors = true;
	    }
	    
	    System.out.println(hasErrors);

	    return hasErrors;
	}
	
	
}
