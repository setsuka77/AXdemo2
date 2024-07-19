package com.example.demo.service;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;
import com.example.demo.form.UserManagementForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.Random;

@Service
public class UserManagementService {

    @Autowired
    private UsersMapper usersMapper;

    /*
     * ユーザ管理画面 ユーザ名検索、取得
     */
    @Transactional(readOnly = true)
    public UserManagementDto searchUserByName(String name) {
        Users user = usersMapper.findByName(name);
        if (user == null) {
            return null;
        }
        UserManagementDto dto = new UserManagementDto();
        dto.setId(user.getId().toString());
        dto.setName(user.getName());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setStartDate(user.getStartDate().toString());
        return dto;
    }

    /*
     * 未登録ユーザのユーザID生成(16桁)
     */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 16;
    private static final Random RANDOM = new SecureRandom();

    public String generateUniqueUserId() {
        StringBuilder id = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            id.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return id.toString();
    }

//    @Transactional
//    public void registerUser(UserManagementForm form) {
//        Users user = usersMapper.findByName(form.getName());
//        if (user == null) {
//            user = new Users();
//            user.setId(Integer.parseInt(form.getId()));
//        }
//        user.setName(form.getName());
//        user.setPassword(form.getPassword());
//        user.setRole(form.getRole());
//        user.setStartDate(java.sql.Date.valueOf(form.getStartDate()));
//        if (usersMapper.findByName(form.getName()) == null) {
//            usersMapper.insertUser(user);
//        } else {
//            usersMapper.updateUser(user);
//        }
//    }
}
