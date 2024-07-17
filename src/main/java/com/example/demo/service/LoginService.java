package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;

@Service
public class LoginService {

	@Autowired
    private UsersMapper usersMapper;

    public Users login(Integer id, String password) {
        Users user = usersMapper.findByUserId(id);
        if (user != null && password.equals(user.getPassword())) { // 簡易的なパスワードチェック（ハッシュ化されていない場合）
            return user;
        }
        return null;
    }

}
