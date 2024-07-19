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

//	public boolean login(Integer id, String password) {
//		Users user = usersMapper.findByUserId(id);
//		return user != null && user.getPassword().equals(password);
//	}
	
	public Users login(Integer id, String password) {
        Users user = usersMapper.findByUserId(id);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
	
	
}
