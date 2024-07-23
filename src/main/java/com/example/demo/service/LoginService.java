package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;

import jakarta.servlet.http.HttpSession;

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
	
	
}
