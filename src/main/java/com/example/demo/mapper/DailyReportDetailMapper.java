package com.example.demo.mapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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
	 * 日報情報 取得
	 *
	 * @param userId 
	 * @param date 
	 * @return 指定されたユーザーIDと日付に一致する日報申請情報のリスト
	 */
	List<DailyReportDetail> findByUserIdAndDate(@Param("userId") Integer userId, @Param("date") Date date);

	/**
	 * 日報情報 削除
	 * @param userId 
	 * @param date 
	 * @return 指定されたユーザーIDと日付に一致する日報情報
	 */
	void delete(DailyReportDetail dailyReportDetail);
	
	
	/**
     * 日報情報を取得
     * 
     * @param integer ユーザーID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 指定されたユーザーIDと日付範囲に一致する日報申請情報のリスト
     */
    List<DailyReportDetail> findByUserIdAndRange(@Param("userId") Integer integer,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
	
	
	
}
