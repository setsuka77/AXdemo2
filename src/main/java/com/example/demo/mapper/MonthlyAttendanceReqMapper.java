package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.MonthlyAttendanceReqDto;
import com.example.demo.entity.MonthlyAttendanceReq;

/*
 * 月次勤怠テーブルマッパー
 * テーブル名：monthlyAttendanceReq
 */

@Mapper
public interface MonthlyAttendanceReqMapper {

	/*
	 * 承認申請ボタン押下
	 * 承認申請登録
	 */
	void insert(MonthlyAttendanceReq req);
	
	
	/*
	 * 承認申請ボタン押下
	 * 却下後承認申請更新
	 */
    void update(MonthlyAttendanceReq req);
	
	// ユーザーと年月で申請を検索
    MonthlyAttendanceReq findByUserAndYearMonth(@Param("userId") Integer userId, @Param("targetYearMonth") java.sql.Date targetYearMonth);
    
    /**
     * 上部ステータス表示
     * 指定した年月のステータスを取得
     */
    List<MonthlyAttendanceReqDto> findByYearMonth(@Param("userId") Integer userId, @Param("targetYearMonth") java.sql.Date targetYearMonth);
    
    /*
     * 承認待ちの情報をすべて取得
     */
    List<MonthlyAttendanceReqDto> findAllWithStatus();
    
    /*
     * 申請IDから情報を取得
     */
    MonthlyAttendanceReq findById(Integer id);
    
    /*
     * 却下、承認ボタン押下時
     * ステータスの更新
     */
    void updateStatus(MonthlyAttendanceReq req);
    
}
