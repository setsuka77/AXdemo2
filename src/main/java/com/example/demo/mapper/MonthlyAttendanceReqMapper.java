package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.entity.MonthlyAttendanceReq;

/*
 * 月次勤怠テーブルマッパー
 * テーブル名：monthlyAttendanceReq
 */
@Mapper
public interface MonthlyAttendanceReqMapper {

	/*
	 * 承認申請一覧表示用
	 */
	@Select("SELECT " + "mar.id as reqId, " + "mar.user_id as userId, " + "mar.target_year_month as targetYearMonth, "
			+ "mar.date as reqDate, " + "mar.status as reqStatus, " + "u.name as userName "
			+ "FROM monthly_attendance_req mar " + "INNER JOIN users u ON mar.user_id = u.id "
			+ "WHERE mar.status = #{status}")
	List<MonthlyAttendanceReq> getMonthlyAttendanceReqWithUser(@Param("status") Integer status);

}
