//package com.example.demo.repository;
//
//import org.apache.ibatis.annotations.Insert;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//import org.apache.ibatis.annotations.Select;
//import com.example.demo.entity.Users;
//
//@Mapper
//public interface UserRepository {
//
//    @Select("SELECT * FROM users WHERE username = #{username}")
//    Users findByUsername(@Param("name") String name);
//
//    @Insert("INSERT INTO users (name, password, role, departmentId, startDate) " +
//            "VALUES (#{username}, #{password}, #{role}, #{departmentId}, #{startDate})")
//    void insertUser(Users user);
//}
