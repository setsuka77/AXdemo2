package com.example.demo.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.Users;

/*
 * ユーザテーブルマッパー
 * テーブル名：Users
 */
@Mapper
public interface UsersMapper {

    /*
     * ログインに必要な情報取得(id、パスワード参照用)
     */
    Users findByUserId(@Param("id") Integer id);
	
	Users findByUserIdAndPassword(@Param("id") Integer id, @Param("password") String password);
	
	/*
	 * ユーザ管理画面 検索
	 */
	Users findByUserName(@Param("name") String name);
	
	/*
	 * ユーザ管理画面 登録
	 */
	Boolean insertUser(Users user);
	
}
