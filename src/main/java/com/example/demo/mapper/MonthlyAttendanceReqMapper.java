package com.example.demo.mapper;

import java.util.List;

//import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Select;
//import org.apache.ibatis.annotations.Update;

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
     * 承認待ちの情報をすべて取得
     */
    List<MonthlyAttendanceReqDto> findAllWithStatus();
    
    /*
     * 申請IDから情報を取得
     */
    MonthlyAttendanceReq findById(Integer id);
    
    /*
     * ステータスの更新
     */
    void updateStatus(MonthlyAttendanceReq req);
    
    

}
