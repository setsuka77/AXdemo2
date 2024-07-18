package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;

@Service
public class UserManagementService {

    @Autowired
    private UsersMapper usersMapper;

    public Users findByUsername(String name) {
        return usersMapper.findByUserName(name);
    }

    public void registerUser(Users user) {
        usersMapper.insertUser(user);
    }
}
