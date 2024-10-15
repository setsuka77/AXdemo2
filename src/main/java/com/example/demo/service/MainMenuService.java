package com.example.demo.service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Users;
import com.example.demo.mapper.AttendanceMapper;

@Service
public class MainMenuService {

	@Autowired
	AttendanceMapper attendanceMapper;

	/**
	 * ユーザーの出勤ステータスをチェック
	 *
	 * @param loginUser 
	 * @return ユーザーの出勤ステータス
	 */
	public Integer checkStatus(Users loginUser) {
		Attendance req = new Attendance();
		req.setUserId(loginUser.getId());

		// 現在の日付を取得
		LocalDate today = LocalDate.now();
		req.setDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant())); // Date型に変換

		// ステータスを検索
		req = attendanceMapper.findByDateAndUserId(req.getDate(), req.getUserId());
		return req != null ? req.getStatus() : null;
	}

	/**
	 * ユーザーの権限をチェック
	 *
	 * @param loginUser 
	 * @return ユーザーの権限
	 */
	public String checkRole(Users loginUser) {
		//ログインユーザの権限判定
		String role = loginUser.getRole();
		if ("1".equals(role)) {
			role = "admin";
		} else if ("2".equals(role)) {
			role = "manager";
		} else if ("3".equals(role) || "4".equals(role)) {
			role = "regular";
		}
		return role;
	}

	/**
	 * ユーザーの出勤を登録
	 *
	 * @param loginUser 
	 * @param status 出勤状態
	 * @return 処理結果メッセージ
	 */
	public String registStartAttendance(Users loginUser, Integer status) {
		Attendance attendance = new Attendance();
		attendance.setUserId(loginUser.getId());
		attendance.setStatus(status);

		// 現在の年月日と時刻を設定
		LocalDate today = LocalDate.now(); // 現在の日付
		LocalTime currentTime = LocalTime.now().withSecond(0); // 現在の時刻

		// Date型に変換して設定
		attendance.setDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant())); // 現在の日付をDate型に変換
		attendance.setStartTime(Time.valueOf(currentTime)); // 現在の時刻をTime型に設定

		Attendance searchAttendance = attendanceMapper.findByDateAndUserId(attendance.getDate(),
				attendance.getUserId());
		if (searchAttendance == null) {
			attendanceMapper.registStart(attendance);
			return "出勤しました";
		} else {
			return "出勤中です。勤怠登録画面から編集してください。";
		}
	}

	/**
	 * ユーザーの退勤を登録
	 *
	 * @param loginUser
	 * @return 処理結果メッセージ
	 */
	public String registEndAttendance(Users loginUser) {
		Attendance attendance = new Attendance();
		attendance.setUserId(loginUser.getId());

		// 現在の年月日と時刻を設定
		LocalDate today = LocalDate.now();
		LocalTime currentTime = LocalTime.now().withSecond(0);

		// Date型に変換して設定
		attendance.setDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		attendance.setEndTime(Time.valueOf(currentTime));

		attendanceMapper.registEnd(attendance);
		return "退勤しました";
	}

}
