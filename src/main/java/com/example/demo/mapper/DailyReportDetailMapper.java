package com.example.demo.mapper;

import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.entity.DailyReportDetail;

@Mapper
public interface DailyReportDetailMapper {
	
	/*
	 * 提出ボタン押下　日報登録
	 */
	void insert(DailyReportDetail dailyReportDetail);
	
	/*
	 * 提出ボタン押下　日報更新
	 */
	void update(DailyReportDetail dailyReportDetail);
	
	/**
	 * 日報申請情報 取得
	 *
	 * @param userId 
	 * @param date 
	 * @return 指定されたユーザーIDと日付に一致する日報申請情報のリスト
	 */
	DailyReportDetail findByUserIdAndDate(@Param("userId") Integer userId, @Param("date") Date date);
	
}
