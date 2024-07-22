package com.example.demo.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AttendanceDto;
import com.example.demo.dto.CalendarDto;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.Users;
import com.example.demo.mapper.AttendanceMapper;

@Service
public class AttendanceService {

	@Autowired
	private AttendanceMapper attendanceMapper;
	

	// 日付リスト作成
	public List<CalendarDto> generateCalendar(int year, int month) {
		List<CalendarDto> calendar = new ArrayList<>();
		YearMonth yearMonth = YearMonth.of(year, month);
		int daysInMonth = yearMonth.lengthOfMonth();

		for (int day = 1; day <= daysInMonth; day++) {
			LocalDate date = LocalDate.of(year, month, day);
			String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.JAPANESE);
			CalendarDto calendarDto = new CalendarDto(date, dayOfWeek);
			calendar.add(calendarDto);
		}

		return calendar;
	}

	
	//勤怠情報取得
	public List<AttendanceDto> checkAttendance(List<CalendarDto> calendar, Users loginUser){
		LocalDate startDate = calendar.get(0).getDate();
        LocalDate endDate = calendar.get(calendar.size() - 1).getDate();

        // DBから勤怠情報を取得
        List<AttendanceDto> attendanceDtoList = attendanceMapper.findAttendanceByUserIdAndDateRange(
                loginUser.getId(), java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

        return attendanceDtoList;
	}

	
	/*//勤怠フォームの生成
	public AttendanceForm setAttendanceForm(List<AttendanceDto> attendanceDtoList) {
	        // 新しい AttendanceForm オブジェクトを作成
	        AttendanceForm attendanceForm = new AttendanceForm();
	        // AttendanceForm に必要な初期値や設定を行う
	        attendanceForm.setAttendanceList(new ArrayList<AttendanceForm>());
	
	        // AttendanceDto リストの各要素を処理
	        for (AttendanceDto attendanceDto : attendanceDtoList) {
	            // 新しい DailyAttendanceForm オブジェクトを作成
	            DailyAttendanceForm dailyAttendanceForm = new DailyAttendanceForm();
	            // AttendanceDto の各フィールドを DailyAttendanceForm に設定
	            dailyAttendanceForm.setStudentAttendanceId(attendanceDto.getId());
	            dailyAttendanceForm.setTrainingDate(attendanceDto.getDate().toString());
	            dailyAttendanceForm.setTrainingStartTime(attendanceDto.getStartTime());
	            dailyAttendanceForm.setTrainingEndTime(attendanceDto.getEndTime());
	
	        
	
	            // その他のフィールドを設定
	            dailyAttendanceForm.setStatus(String.valueOf(attendanceDto.getStatus()));
	            dailyAttendanceForm.setNote(attendanceDto.getRemarks());
	            dailyAttendanceForm.setSectionName(getSectionName(attendanceDto.getSectionId()));
	            dailyAttendanceForm.setIsToday(isToday(attendanceDto.getDate()));
	            dailyAttendanceForm.setDispTrainingDate(formatDate(attendanceDto.getDate()));
	            dailyAttendanceForm.setStatusDispName(getStatusDisplayName(attendanceDto.getStatus()));
	
	            // DailyAttendanceForm を AttendanceForm のリストに追加
	            attendanceForm.getAttendanceList().add(dailyAttendanceForm);
	        }
	
	        // 完成した AttendanceForm を返す
	        return attendanceForm;
	    }
	
	    // 日付をフォーマットするメソッド
	    private String formatDate(Date date) {
	        // 実装に応じて日付をフォーマット
	        return ""; // 仮の実装
	    }*/

	
	
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
