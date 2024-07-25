package com.example.demo.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
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

	/*
	 * 勤怠管理画面 日付リスト作成
	 */
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

	/*
	 * 勤怠管理画面 DBから勤怠情報取得
	 */
	public List<AttendanceDto> checkAttendance(List<CalendarDto> calendar, Users loginUser) {
		LocalDate startDate = calendar.get(0).getDate();
		LocalDate endDate = calendar.get(calendar.size() - 1).getDate();

		// DBから勤怠情報を取得
		List<AttendanceDto> attendanceDtoList = attendanceMapper.findAttendanceByUserIdAndDateRange(loginUser.getId(),
				java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

		return attendanceDtoList;
	}

	/*
	 * 勤怠管理画面 勤怠フォームの生成
	 */
	public AttendanceForm setAttendanceForm(List<CalendarDto> calendarList, List<AttendanceDto> attendanceDtoList,
			Users loginUser) {

		AttendanceForm attendanceForm = new AttendanceForm();
		attendanceForm.setDailyAttendanceList(new ArrayList<>());

		// カレンダーの日付に対応する勤怠情報を Map に変換
		Map<LocalDate, AttendanceDto> attendanceMap = attendanceDtoList.stream()
				.collect(Collectors.toMap(dto -> dateUtil.dateToLocalDate(dto.getDate()), dto -> dto));
		// 各日付に対応するフォームを生成
		for (CalendarDto calendarDto : calendarList) {
			DailyAttendanceForm dailyForm = new DailyAttendanceForm();
			LocalDate date = calendarDto.getDate();
			dailyForm.setDate(dateUtil.localDateToDate(date)); // 日付をDateに変換して設定

			// この時点で日付の数だけリストが出てる
			System.out.println("とても眠い");

			AttendanceDto attendanceDto = attendanceMap.getOrDefault(date, new AttendanceDto());
			dailyForm.setId(attendanceDto.getId());
			dailyForm.setUserId(loginUser.getId());
			dailyForm.setStatus(attendanceDto.getStatus());
			dailyForm
					.setStartTime(dateUtil.localTimeToString(dateUtil.stringToLocalTime(attendanceDto.getStartTime())));
			dailyForm.setEndTime(dateUtil.localTimeToString(dateUtil.stringToLocalTime(attendanceDto.getEndTime())));
			dailyForm.setRemarks(attendanceDto.getRemarks());

			attendanceForm.getDailyAttendanceList().add(dailyForm);
		}

		return attendanceForm;
	}

	/*
	 * 勤怠管理画面 勤怠情報登録処理
	 */
	public String registAttendance(AttendanceForm attendanceForm, Users loginUser, List<CalendarDto> calendar) {
        List<DailyAttendanceForm> dailyAttendanceList = attendanceForm.getDailyAttendanceList();
        
        for (int i = 0; i < dailyAttendanceList.size(); i++) {
            DailyAttendanceForm dailyForm = dailyAttendanceList.get(i);
            CalendarDto calendarDto = calendar.get(i);

            // CalendarDto から日付を取得して DailyAttendanceForm に設定
            if (calendarDto != null) {
                LocalDate date = calendarDto.getDate();
                dailyForm.setDate(dateUtil.localDateToDate(date)); 
                
                Attendance attendance = new Attendance();
                attendance.setId(dailyForm.getId());
                attendance.setUserId(loginUser.getId());
                attendance.setDate(dailyForm.getDate()); 
                attendance.setStatus(dailyForm.getStatus());
                attendance.setStartTime(dateUtil.stringToTime(dailyForm.getStartTime()));
                attendance.setEndTime(dateUtil.stringToTime(dailyForm.getEndTime()));
                attendance.setRemarks(dailyForm.getRemarks());

                if (attendance.getId() == null) {
                    // 勤怠情報を登録
                    attendanceMapper.insert(attendance);
                } else {
                    // 勤怠情報を更新
                    attendanceMapper.update(attendance);
                }
            } else {
                System.out.println("No matching CalendarDto found for index: " + i);
            }
        }
        return "勤怠情報が登録されました";
    }

	/*
	 * ユーザ管理画面 文字数制限・日付形式チェック
	 */
	public String validateAttendanceForm(AttendanceForm attendanceForm) {
	    StringBuilder errorMessage = new StringBuilder("勤怠登録に失敗しました。<br>");

	    boolean hasErrors = false;

	    // 出退勤時間形式チェック
	    Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}$");

	    for (DailyAttendanceForm dailyForm : attendanceForm.getDailyAttendanceList()) {
	        // 出勤時間チェック
	        String startTime = dailyForm.getStartTime();
	        if (startTime != null && !startTime.isEmpty() && !timePattern.matcher(startTime).matches()) {
	            errorMessage.append(dailyForm.getDate()).append(" の出勤時間 : hh:mm のフォーマットで入力してください。<br>");
	            hasErrors = true;
	        }

	        // 退勤時間チェック
	        String endTime = dailyForm.getEndTime();
	        if (endTime != null && !endTime.isEmpty() && !timePattern.matcher(endTime).matches()) {
	            errorMessage.append(dailyForm.getDate()).append(" の退勤時間 : hh:mm のフォーマットで入力してください。<br>");
	            hasErrors = true;
	        }

	        // 備考欄文字数チェック
	        String remarks = dailyForm.getRemarks();
	        if (remarks != null && !remarks.isEmpty() && remarks.length() > 20) {
	            errorMessage.append(dailyForm.getDate()).append(" の備考 : 全角20文字以内で入力してください。<br>");
	            hasErrors = true;
	        }
	    }

	    return hasErrors ? errorMessage.toString() : null;
	}

}
