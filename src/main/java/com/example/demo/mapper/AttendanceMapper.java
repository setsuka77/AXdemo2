package com.example.demo.mapper;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.AttendanceDto;
import com.example.demo.dto.UsersDto;
import com.example.demo.entity.Attendance;

@Mapper
public interface AttendanceMapper {
	
	/*
	 * 勤怠登録情報取得(ユーザーID)
	 * 
	 * @param userId
	 * @return 勤怠登録情報エンティティ
	 */
	List<Attendance> findByUserId(@Param("userId") Integer userId);

	/*
     * 勤怠登録情報用DTOリスト取得
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @return 勤怠登録情報用DTOリスト
     */
	List<AttendanceDto> findAttendanceByUserIdAndDateRange(@Param("userId") Integer userId, @Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	/**
	 * 勤怠未提出者情報　取得
	 */
	List<UsersDto> findUsersWithoutReport(@Param("date") Date date);
	
	/**
	 * 勤怠情報（受講生入力）登録/更新
	 * 
	 * @param Attendance
	 * @return 更新結果
	 */
	 void upsert(List<Attendance> attendances);

	
	/**
	 * 勤怠情報（受講生入力）更新
	 * 
	 * @param Attendance
	 * @return 勤怠ID取得
	 */
	Attendance findByDateAndUserId(@Param("date") java.util.Date date,@Param("userId") Integer UserId);
	
	/**
	 * 出勤ボタン押下時　登録
	 * 
	 * @param Attendance
	 */
	void registStart(Attendance attendance);
	
	/**
	 * 退勤ボタン押下時　更新
	 * 
	 * @param Attendance
	 */
	void registEnd(Attendance attendance);
	
}
