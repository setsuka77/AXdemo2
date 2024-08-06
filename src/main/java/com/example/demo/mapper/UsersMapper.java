package com.example.demo.mapper;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.entity.Users;

/*
 * ユーザテーブルマッパー
 * テーブル名：Users
 */
@Mapper
public interface UsersMapper {

	/**
	 * ログイン画面 ログインに必要な情報取得(id参照用)
	 * 
	 * @param id ユーザID
	 * @return ユーザ情報
	 */
	@Select(value = "SELECT * FROM users WHERE id = #{id}")
	Users findByUserId(@Param("id") Integer id);

	/**
	 * 勤怠登録画面 ユーザ情報取得用
	 * 
	 * @param id   ユーザID
	 * @param name ユーザ名
	 * @param role ユーザ役割
	 * @return ユーザ情報
	 */
	Users findByIdAndNameAndRole(@Param("id") Integer id, @Param("name") String name, @Param("role") String role);

	/**
	 * ユーザ管理画面 ユーザ情報取得(検索用)
	 * 
	 * @param name ユーザ名
	 * @return ユーザ情報
	 */

	@Results({
			@Result(property = "startDate", column = "start_date", javaType = LocalDate.class, jdbcType = JdbcType.DATE, typeHandler = LocalDateTypeHandler.class) })
	@Select("SELECT * FROM users WHERE name = #{name}")
	UserManagementDto findByName(@Param("name") String name);

	/**
	 * ユーザ管理画面 新規ユーザID生成用
	 * 
	 * @return DB内の最大ID
	 */
	@Select("SELECT MAX(id) FROM users")
	Integer findMaxId();

	/**
	 * ユーザ管理画面 登録更新判別用
	 * 
	 * @param id ユーザID
	 * @return ユーザ情報
	 */
	@Select("SELECT * FROM users WHERE id = #{id}")
	UserManagementDto identifyUserId(@Param("id") Integer id);

	/**
	 * ユーザ管理画面 新規ユーザ情報登録
	 * 
	 * @param user ユーザ情報
	 */
	@Insert("INSERT INTO users (id, name, password, role, start_date) VALUES (#{id}, #{name}, #{password}, #{role}, #{startDate})")
	void insertUser(UserManagementDto user);

	/**
	 * ユーザ管理画面 既存ユーザ情報更新
	 * 
	 * @param user 更新するユーザ情報
	 */
	@Update("UPDATE users SET password = #{password}, role = #{role}, start_date = #{startDate} WHERE id = #{id}")
	void updateUser(UserManagementDto user);

	/**
	 * ユーザ管理画面 既存ユーザ情報削除
	 * 
	 * @param id 削除するユーザのID
	 */
    @Delete("DELETE FROM users WHERE id = #{id}")
	void deleteUser(@Param("id") Integer id);

}
