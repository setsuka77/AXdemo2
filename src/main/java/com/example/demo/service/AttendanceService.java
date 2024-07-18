package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AttendanceDto;
import com.example.demo.entity.Attendance;
import com.example.demo.mapper.AttendanceMapper;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    // 勤怠登録情報取得
    public List<AttendanceDto> getAttendanceByUserIdAndMonth(int userId, Integer year, Integer month) {
        return attendanceMapper.findAttendanceByUserIdAndMonth(userId, year, month);
    }

    // 勤怠登録情報登録
    public void registerAttendance(AttendanceDto attendanceDto) {
        Attendance attendance = new Attendance();
        attendance.setUserId(attendanceDto.getUserId());
        attendance.setStatus(attendanceDto.getStatus());
        attendance.setDate(attendanceDto.getDate());
        attendance.setStartTime(attendanceDto.getStartTime());
        attendance.setEndTime(attendanceDto.getEndTime());
        attendance.setRemarks(attendanceDto.getRemarks());

        attendanceMapper.insert(attendance);
    }
}

