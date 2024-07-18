package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;

@Service
public class LoginService {

	@Autowired
    private UsersMapper usersMapper;

	public boolean authenticate(Integer id, String password) {
        return usersMapper.findByUserIdAndPassword(id, password) != null;
    }
}
