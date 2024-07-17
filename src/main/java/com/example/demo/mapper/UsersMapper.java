package com.example.demo.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Users;


@Mapper
public interface UsersMapper {

    /*
     * ログインに必要な情報取得(id、パスワード参照用)
     */
	
    Users findByUserId(Integer id);
	
	Users findByUserIdAndPassword(@Param("id") Integer id, @Param("password") String password);
	
}
