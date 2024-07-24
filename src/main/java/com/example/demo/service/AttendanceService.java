package com.example.demo.service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AttendanceDto;
import com.example.demo.dto.CalendarDto;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.Users;
import com.example.demo.form.AttendanceForm;
import com.example.demo.form.DailyAttendanceForm;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.util.DateUtil;

@Service
public class AttendanceService {

	@Autowired
	private AttendanceMapper attendanceMapper;
	@Autowired
    private DateUtil dateUtil;

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

	
	//勤怠フォームの生成
	public AttendanceForm setAttendanceForm(List<CalendarDto> calendarList, List<AttendanceDto> attendanceDtoList) {
	        
	    AttendanceForm attendanceForm = new AttendanceForm();
	    attendanceForm.setDailyAttendanceList(new ArrayList<>());
	    
	 // カレンダーの日付に対応する勤怠情報を Map に変換
        Map<LocalDate, AttendanceDto> attendanceMap = attendanceDtoList.stream()
                .collect(Collectors.toMap(dto -> dateUtil.dateToLocalDate(dto.getDate()), dto -> dto));
        // 各日付に対応するフォームを生成
        for (CalendarDto calendarDto : calendarList) {
            DailyAttendanceForm dailyForm = new DailyAttendanceForm();
            LocalDate date = calendarDto.getDate();
            dailyForm.setDate(dateUtil.localDateToDate(date)); // DateUtil を利用して変換
            
            System.out.println(dailyForm);
            
            AttendanceDto attendanceDto = attendanceMap.getOrDefault(date, new AttendanceDto());
            dailyForm.setId(attendanceDto.getId());
            dailyForm.setUserId(attendanceDto.getUserId());
            dailyForm.setStatus(attendanceDto.getStatus());
            dailyForm.setStartTime(dateUtil.localTimeToString(dateUtil.stringToLocalTime(attendanceDto.getStartTime())));
            dailyForm.setEndTime(dateUtil.localTimeToString(dateUtil.stringToLocalTime(attendanceDto.getEndTime())));
            dailyForm.setRemarks(attendanceDto.getRemarks());
            
            attendanceForm.getDailyAttendanceList().add(dailyForm);
        }
        System.out.println("テスト1");
	    
        return attendanceForm;
	}

	//登録処理
	public String registAttendance(AttendanceForm attendanceForm,Users loginUser) {
	        List<DailyAttendanceForm> dailyAttendanceList = attendanceForm.getDailyAttendanceList();
	        if (dailyAttendanceList == null) {
	            dailyAttendanceList = new ArrayList<>(); // 空のリストで初期化
	            System.out.println("テスト4");
	        }
	        
	        System.out.println("dailyAttendanceList size: " + dailyAttendanceList.size());
	        System.out.println("Login User: " + loginUser);
			for (DailyAttendanceForm dailyForm : dailyAttendanceList) {
				Attendance attendance = new Attendance();
				attendance.setId(dailyForm.getId());
				attendance.setUserId(loginUser.getId());
				attendance.setStatus(dailyForm.getStatus());
				attendance.setDate(dailyForm.getDate());
				attendance.setStartTime(Time.valueOf(dateUtil.stringToLocalTime(dailyForm.getStartTime())));
				attendance.setEndTime(Time.valueOf(dateUtil.stringToLocalTime(dailyForm.getEndTime())));
				attendance.setRemarks(dailyForm.getRemarks());

				System.out.println("テスト5");
				System.out.println(attendance);
				// 勤怠情報を更新
				attendanceMapper.update(attendance);
				System.out.println("テスト6");
			}

	        return "勤怠情報が登録されました";
	 }

	

}
