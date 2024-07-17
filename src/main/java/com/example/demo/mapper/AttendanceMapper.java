package com.example.demo.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.AttendanceDto;
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
	 * @param userId
	 * @param date
	 * @return 勤怠登録情報用DTOリスト
	 */
	List<AttendanceDto> getAttedance(@Param("userId") Integer userId,@Param("date") Date date);
	
	/*
	 * 勤怠登録情報登録
	 * @return 登録結果
	 */
	Boolean insert(Attendance attendance);
	
	/*
	 * 勤怠登録情報　承認申請
	 * @return 申請状況
	 */
	
	
}
