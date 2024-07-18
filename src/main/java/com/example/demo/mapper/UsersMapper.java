package com.example.demo.mapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
	
    @Select("SELECT * FROM users WHERE id = #{id} AND password = #{password}")
	Users findByUserIdAndPassword(@Param("id") Integer id, @Param("password") String password);
    
    /*
     * 勤怠登録画面 ユーザ情報取得
     */
    Users findByIdAndNameAndRole(@Param("id") Integer id, @Param("name") String name, @Param("role") String role);
	
	/*
	 * ユーザ管理画面 検索
	 */
	Users findByUserName(@Param("name") String name);
	
	/*
	 * ユーザ管理画面 登録
	 */
	Boolean insertUser(Users user);
	
}
