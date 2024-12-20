package com.example.demo.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;

import com.example.demo.dto.UserManagementDto;
import com.example.demo.dto.UsersDto;
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
	List<UserManagementDto> findByName(@Param("name") String name);
	
	/**
	 * ユーザ管理画面 ユーザ情報取得
	 * 
	 * @param name ユーザー名
	 * @param id ユーザID
	 * @return ユーザ情報
	 */
	UserManagementDto findByIdAndName(@Param("name") String name,@Param("id") Integer id);

	/**
	 * ユーザ管理画面 ユーザ情報取得(重複チェック,登録更新判別用)
	 * 
	 * @param id ユーザID
	 * @return ユーザ情報
	 */
	Users findById(@Param("id") Integer id);

	/**
	 * ユーザ管理画面 新規ユーザID生成用
	 * 
	 * @return DB内の最大ID
	 */
	Integer findMaxId();

	/**
	 * ユーザ管理画面 新規ユーザ情報登録
	 * 
	 * @param user ユーザ情報
	 */
	void insertUser(Users user);

	/**
	 * ユーザ管理画面 既存ユーザ情報更新
	 * 
	 * @param user 更新するユーザ情報
	 */
	void updateUser(Users user);
	
	/**
	 * バッチ処理 メール送信先情報取得
	 * 
	 */
	List<Users> findUsersByRole(String role);

	/**
	 * バッチ処理
	 * マネージャー権限のユーザー情報取得
	 */
	List<UsersDto> findManagers();
	
	/**
	 * 部署登録画面　ユーザー情報取得
	 */
	List<UserManagementDto> findWorker( Integer departmentId);
}
