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

	void insert(MonthlyAttendanceReq req);

    MonthlyAttendanceReq findById(Integer id);

    void update(MonthlyAttendanceReq req);

    List<MonthlyAttendanceReq> findAllWithStatus(@Param("status") Integer status);

}
