package com.example.demo.service;

import com.example.demo.dto.UserManagamentDto;
import com.example.demo.entity.Users;
import com.example.demo.form.UserManagementForm;
import com.example.demo.mapper.UsersMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {

    @Autowired
    private UsersMapper usersMapper;

//    public List<UserManagamentDto> searchUser(String name) {
//        Users user = usersMapper.getUserListForSearch(name);
//        return user != null;
//    }

//    public void registerUser(UserManagementForm form) {
//        Users user = new Users();
//        user.setName(form.getName());
//        user.setPassword(form.getPassword()); // パスワードのハッシュ化は省略
//        user.setRole(form.getRole());
//        user.setStartDate(form.getStartDate());
//
//        usersMapper.insertUser(user);
//    }
//
//    public UserManagementForm findUserFormByUsername(String name) {
//        Users user = usersMapper.findByUserName(name);
//        if (user != null) {
//            UserManagementForm form = new UserManagementForm();
//            form.setName(user.getName());
//            form.setPassword(user.getPassword());
//            form.setRole(user.getRole());
//            form.setStartDate(user.getStartDate());
//            return form;
//        }
//        return null;
//    }
}
