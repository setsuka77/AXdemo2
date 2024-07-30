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
//	@Insert("INSERT INTO monthly_attendance_req (user_id, target_year_month, date, status) VALUES (#{userId}, #{targetYearMonth}, #{date}, #{status})")
	void insert(MonthlyAttendanceReq req);
    
    /*
     * 承認待ちの情報をすべて取得
     */
//    @Select("SELECT u.name AS userName, u.id AS userId, mar.* " +
//            "FROM monthly_attendance_req mar " +
//            "JOIN users u ON mar.user_id = u.id " +
//            "WHERE mar.status = 1")
    List<MonthlyAttendanceReqDto> findAllWithStatus();
    
    /*
     * 申請IDから情報を取得
     */
//    @Select("SELECT u.name AS userName, u.id AS userId, mar.* " +
//            "FROM monthly_attendance_req mar " +
//            "JOIN users u ON mar.user_id = u.id " +
//            "WHERE mar.id = #{id}")
    MonthlyAttendanceReq findById(Integer id);
    
    /*
     * ステータスの更新
     */
//    @Update("UPDATE monthly_attendance_req SET status = #{status} WHERE id = #{id}")
    void updateStatus(MonthlyAttendanceReq req);
    
    

}
