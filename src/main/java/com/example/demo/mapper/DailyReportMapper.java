package com.example.demo.mapper;

import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.DailyReport;

@Mapper
public interface DailyReportMapper {
	
	/**
	 * 日報申請情報 取得
	 *
	 * @param userId 
	 * @param date 
	 * @return 指定されたユーザーIDと日付に一致する日報申請情報のリスト
	 */
	DailyReport findByUserIdAndDate(@Param("userId") Integer userId, @Param("date") Date date);
	
	/**
	 * 日報申請情報 登録
	 *
	 * @param dailyReport 
	 */
	void insert(DailyReport dailyReport);
	
	/**
	 * 日報申請情報 更新
	 *
	 * @param dailyReport 
	 */
	void update(DailyReport dailyReport);
}