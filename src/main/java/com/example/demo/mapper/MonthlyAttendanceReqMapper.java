package com.example.demo.mapper;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.MonthlyAttendanceReqDto;
import com.example.demo.dto.UsersDto;
import com.example.demo.entity.MonthlyAttendanceReq;

/*
 * 月次勤怠テーブルマッパー
 * テーブル名：monthlyAttendanceReq
 */

@Mapper
public interface MonthlyAttendanceReqMapper {

	/**
	 * 承認申請ボタン押下 承認申請登録
	 * 
	 * @param req 承認申請情報
	 */
	void insert(MonthlyAttendanceReq req);

	/**
	 * 承認申請ボタン押下 却下後承認申請更新
	 * 
	 * @param req 更新する申請情報
	 */
	void update(MonthlyAttendanceReq req);

	/**
	 * ユーザーと年月で申請IDを検索
	 * 
	 * @param userId          ユーザーID
	 * @param targetYearMonth 対象年月
	 * @return 月次勤怠申請情報
	 */
	MonthlyAttendanceReq findByUserAndYearMonth(@Param("userId") Integer userId,
			@Param("targetYearMonth") java.sql.Date targetYearMonth);

	/**
	 * 指定した年月のステータスを取得
	 * 
	 * @param userId          ユーザーID
	 * @param targetYearMonth 対象年月
	 * @return 月次勤怠申請ステータス一覧
	 */
	List<MonthlyAttendanceReqDto> findByYearMonth(@Param("userId") Integer userId,
			@Param("targetYearMonth") java.sql.Date targetYearMonth);

	/**
	 * 承認待ちの情報をすべて取得
	 * 
	 * @return 承認待ちの申請情報一覧
	 */
	List<MonthlyAttendanceReqDto> findAllWithStatus();

	/**
	 * 申請IDから情報を取得
	 * 
	 * @param id 申請ID
	 * @return 月次勤怠申請情報
	 */
	MonthlyAttendanceReq findById(Integer id);

	/**
	 * 却下、承認ボタン押下時 ステータスの更新
	 * 
	 * @param req 更新する申請情報
	 */
	void updateStatus(MonthlyAttendanceReq req);
	
	/**
	 * 勤怠未提出者情報　取得
	 */
	List<UsersDto> findUsersWithoutAttendance(@Param("date") Date date);

}
