package com.example.demo.service;

import com.example.demo.entity.Users;
import com.example.demo.form.LoginForm;
import com.example.demo.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

	@Autowired
	private UsersMapper usersMapper;

	public boolean login(Integer id, String password) {
		Users user = usersMapper.findByUserId(id);
		return user != null && user.getPassword().equals(password);
	}
}
