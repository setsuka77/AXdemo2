package com.example.demo.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
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
import com.example.demo.entity.MonthlyAttendanceReq;
import com.example.demo.entity.Users;
import com.example.demo.form.AttendanceForm;
import com.example.demo.form.DailyAttendanceForm;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.util.DateUtil;

@Service
public class AttendanceService {

	@Autowired
	private AttendanceMapper attendanceMapper;
	@Autowired
	private DateUtil dateUtil;
	@Autowired
    private MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;

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
//			System.out.println("とても眠い");

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
	 * 入力チェックエラーメッセージ表示用
	 * 日付をFormに詰める
	 */
	public void fillDatesInAttendanceForm(AttendanceForm attendanceForm, List<CalendarDto> calendar) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
	    
	    for (int i = 0; i < calendar.size(); i++) {
	        // LocalDate を Date に変換
	        LocalDate localDate = calendar.get(i).getDate();
	        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	        
	        // 日付をフォーマットしてセット
	        String formattedDate = localDate.format(formatter);
	        
	        // 各 CalendarDto の日付を AttendanceForm にセット
	        attendanceForm.getDailyAttendanceList().get(i).setDate(date);
	        attendanceForm.getDailyAttendanceList().get(i).setFormattedDate(formattedDate);
	    }
	}

	/*
	 * 勤怠管理画面 勤怠情報登録処理
	 */
	/*
	 * 勤怠管理画面 勤怠情報登録処理
	 */
	public String registAttendance(AttendanceForm attendanceForm, Users loginUser) {
		System.out.println("はなきん");
		List<DailyAttendanceForm> dailyAttendanceList = attendanceForm.getDailyAttendanceList();
		Integer userId = loginUser.getId(); 
		
		System.out.println("出てこなさ過ぎてむかつく");
		System.out.println(userId);
		
		for (DailyAttendanceForm dailyForm : dailyAttendanceList) {
	        // 出勤簿から日付とユーザーIDを取得
	        Date date = dailyForm.getDate();
	        
	        // 勤怠情報を検索してIDを取得
	        Attendance searchAttendance = attendanceMapper.findByDateAndUserId(date, userId);
	        
	        System.out.println(searchAttendance);
	        System.out.println(dailyForm.getStatus());
	        
	        // 新しい勤怠オブジェクトを作成
	        Attendance attendance = new Attendance();
	        attendance.setId(searchAttendance != null ? searchAttendance.getId() : null);
	        attendance.setUserId(userId);
	        attendance.setDate(dailyForm.getDate()); 
	        attendance.setStatus(dailyForm.getStatus());
	        attendance.setStartTime(dateUtil.stringToTime(dailyForm.getStartTime()));
	        attendance.setEndTime(dateUtil.stringToTime(dailyForm.getEndTime()));
	        attendance.setRemarks(dailyForm.getRemarks());
	        
	        // IDが存在しない場合は新規登録、それ以外は更新
	        if (attendance.getId() == null) {
	            // 勤怠情報を登録
	            attendanceMapper.insert(attendance);
	        } else {
	            // 勤怠情報を更新
	            attendanceMapper.update(attendance);
	        }
	    }
	    
	    return "勤怠情報が登録されました";
	}

	/*
	 * ユーザ管理画面 文字数制限・日付形式チェック
	 */
	public String validateAttendanceForm(AttendanceForm attendanceForm, boolean isApprovalRequest) {
	    
	    StringBuilder errorMessage = new StringBuilder("勤怠登録に失敗しました。<br>");

	    boolean hasErrors = false;

	    // 出退勤時間形式チェック
	    Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}$");

	    for (DailyAttendanceForm dailyForm : attendanceForm.getDailyAttendanceList()) {
	        // 出勤時間チェック
	        String startTime = dailyForm.getStartTime();
	        if (startTime != null && !startTime.isEmpty() && !timePattern.matcher(startTime).matches()) {
	            errorMessage.append(dailyForm.getFormattedDate()).append(" の出勤時間 : hh:mm のフォーマットで入力してください。<br>");
	            hasErrors = true;
	        }

	        // 退勤時間チェック
	        String endTime = dailyForm.getEndTime();
	        if (endTime != null && !endTime.isEmpty() && !timePattern.matcher(endTime).matches()) {
	            errorMessage.append(dailyForm.getFormattedDate()).append(" の退勤時間 : hh:mm のフォーマットで入力してください。<br>");
	            hasErrors = true;
	        }

	        // 備考欄文字数チェック
	        String remarks = dailyForm.getRemarks();
	        if (remarks != null && !remarks.isEmpty() && remarks.length() > 20) {
	            errorMessage.append(dailyForm.getFormattedDate()).append(" の備考 : 全角20文字以内で入力してください。<br>");
	            hasErrors = true;
	        }

	        // ステータス必須チェック (承認申請時のみ)
	        if (isApprovalRequest) {
	            Integer status = dailyForm.getStatus();
	            if (status == null) {
	                errorMessage.append(dailyForm.getFormattedDate()).append(" のステータス : 必ず選択してください。<br>");
	                hasErrors = true;
	            }
	        }
	    }

	    return hasErrors ? errorMessage.toString() : null;
	}

	/*
	 * 勤怠管理画面 承認申請ボタン 活性非活性確認フラグ用
	 *
	 */
	
	/*
	 * 勤怠管理画面 承認申請一覧表示用
	 */
    public List<MonthlyAttendanceReq> getMonthlyAttendanceReqsWithUser(Integer status) {
        return monthlyAttendanceReqMapper.getMonthlyAttendanceReqWithUser(status);
    }
    

}
