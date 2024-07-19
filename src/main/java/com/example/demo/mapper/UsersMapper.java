package com.example.demo.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.dto.UserManagamentDto;
import com.example.demo.entity.Users;
import com.example.demo.form.LoginForm;

/*
 * ユーザテーブルマッパー
 * テーブル名：Users
 */
@Mapper
public interface UsersMapper {

    /*
     * ログインに必要な情報取得(id参照用)
     */
	@Select(value= "SELECT * FROM users WHERE id = #{id}")
    Users findByUserId(@Param("id") Integer id);
    
    /*
     * 勤怠登録画面 ユーザ情報取得
     */
    Users findByIdAndNameAndRole(@Param("id") Integer id, @Param("name") String name, @Param("role") String role);
	
	/*
	 * ユーザ管理画面
	 * ユーザ情報取得(検索用)リスト
	 */
	List<UserManagamentDto> getUserListForSearch(@Param("name") String name);
	
	/*
	 * ユーザ管理画面 登録
	 */
	Boolean insertUser(Users user);
	
}
