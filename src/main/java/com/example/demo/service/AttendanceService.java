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
import com.example.demo.dto.MonthlyAttendanceReqDto;
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

	/**
	 * ステータスが1の申請一覧を取得する
	 *
	 * @return ステータスが1の月次勤怠申請のリスト
	 */
	public List<MonthlyAttendanceReqDto> findAllAttendance() {
		return monthlyAttendanceReqMapper.findAllWithStatus();
	}

	/**
	 * 勤怠管理画面 日付リスト作成
	 * 
	 * @param year
	 * @param month
	 * @return 指定された年月の日付リスト
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

	/**
	 * 勤怠管理画面 DBから勤怠情報取得
	 * 
	 * @param calendar カレンダーの日付リスト
	 * @param userId
	 * @return 指定されたユーザーと日付範囲の勤怠情報リスト
	 */
	public List<AttendanceDto> checkAttendance(List<CalendarDto> calendar, Integer userId) {
		LocalDate startDate = calendar.get(0).getDate();
		LocalDate endDate = calendar.get(calendar.size() - 1).getDate();

		// DBから勤怠情報を取得
		List<AttendanceDto> attendanceDtoList = attendanceMapper.findAttendanceByUserIdAndDateRange(userId,
				java.sql.Date.valueOf(startDate), java.sql.Date.valueOf(endDate));

		return attendanceDtoList;
	}

	/**
	 * 勤怠管理画面 勤怠フォームの生成
	 * 
	 * @param calendarList      カレンダーの日付リスト
	 * @param attendanceDtoList 勤怠情報リスト
	 * @param userId
	 * @return 勤怠フォーム
	 */
	public AttendanceForm setAttendanceForm(List<CalendarDto> calendarList, List<AttendanceDto> attendanceDtoList,
			Integer userId) {

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

			AttendanceDto attendanceDto = attendanceMap.getOrDefault(date, new AttendanceDto());
			dailyForm.setId(attendanceDto.getId());
			dailyForm.setUserId(userId);
			dailyForm.setStatus(attendanceDto.getStatus());
			dailyForm
					.setStartTime(dateUtil.localTimeToString(dateUtil.stringToLocalTime(attendanceDto.getStartTime())));
			dailyForm.setEndTime(dateUtil.localTimeToString(dateUtil.stringToLocalTime(attendanceDto.getEndTime())));
			dailyForm.setRemarks(attendanceDto.getRemarks());

			attendanceForm.getDailyAttendanceList().add(dailyForm);
		}

		return attendanceForm;
	}

	/**
	 * 勤務状況がすべて埋まっているかチェック
	 * 
	 * @param attendanceForm 勤怠フォーム
	 * @param status
	 * @return 勤怠状況がすべて埋まっている場合は true、それ以外は false
	 */
	public boolean checkAllStatus(AttendanceForm attendanceForm, String status) {
		// statusが却下以外の場合はfalseを返す
		if ("承認待ち".equals(status) || "承認済み".equals(status)) {
			return false;
		}

		// dailyAttendanceList内のすべてのDailyAttendanceFormのstatusがnullでないかチェック
		return attendanceForm.getDailyAttendanceList().stream().allMatch(dailyForm -> dailyForm.getStatus() != null);
	}

	/**
	 * 登録ボタンの活性非活性チェック
	 * 
	 * @param status
	 * @return ステータスが「却下」または「未申請」の場合は true、それ以外は false
	 */
	public boolean checkRegister(String status) {
		// statusが「却下」または「未申請」の場合はtrueを返す
		if ("却下".equals(status) || "未申請".equals(status)) {
			return true;
		}

		return false;
	}

	/**
	 * 指定した年月のステータスを取得
	 * 
	 * @param userId
	 * @param targetYearMonth
	 * @return 指定された年月の月次勤怠申請のリスト
	 */
	public List<MonthlyAttendanceReqDto> findByYearMonth(Integer userId, java.sql.Date targetYearMonth) {
		return monthlyAttendanceReqMapper.findByYearMonth(userId, targetYearMonth);
	}

	/**
	 * 入力チェックエラーメッセージ表示用 日付をFormに詰める
	 * 
	 * @param attendanceForm 勤怠フォーム
	 * @param calendar       カレンダーの日付リスト
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

	/**
	 * 勤怠管理画面 勤怠情報登録処理
	 * 
	 * @param attendanceForm 勤怠フォーム
	 * @param loginUser
	 * @return 登録結果のメッセージ
	 */
	public String registAttendance(AttendanceForm attendanceForm, Users loginUser) {
		List<DailyAttendanceForm> dailyAttendanceList = attendanceForm.getDailyAttendanceList();
		Integer userId = loginUser.getId();

		for (DailyAttendanceForm dailyForm : dailyAttendanceList) {
			// 出勤簿から日付とユーザーIDを取得
			Date date = dailyForm.getDate();

			if (dailyForm.getStatus() != null) {

				// 勤怠情報を検索してIDを取得
				Attendance searchAttendance = attendanceMapper.findByDateAndUserId(date, userId);

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
		}
		return "勤怠情報が登録されました";
	}

	/**
	 * 勤怠管理画面 文字数制限・日付形式チェック
	 * 
	 * @param attendanceForm    勤怠フォーム
	 * @param isApprovalRequest 承認申請かどうかを示すフラグ
	 * @return バリデーションエラーメッセージ,エラーがなければ null を返す
	 */
	public String validateAttendanceForm(AttendanceForm attendanceForm, boolean isApprovalRequest) {

		StringBuilder errorMessage = new StringBuilder("勤怠登録に失敗しました。<br>");

		boolean hasErrors = false;

		// 出退勤時間形式チェック
		Pattern timePattern = Pattern.compile("^\\d{2}:\\d{2}$");

		for (DailyAttendanceForm dailyForm : attendanceForm.getDailyAttendanceList()) {
			// すべての項目が未入力の場合はスキップ
			String startTime = dailyForm.getStartTime();
			String endTime = dailyForm.getEndTime();
			String remarks = dailyForm.getRemarks();
			Integer status = dailyForm.getStatus();

			if ((startTime == null || startTime.isEmpty()) && (endTime == null || endTime.isEmpty())
					&& (remarks == null || remarks.isEmpty()) && (status == null)) {
				continue;
			}

			// 出勤時間チェック
			if (startTime != null && !startTime.isEmpty() && !timePattern.matcher(startTime).matches()) {
				errorMessage.append(dailyForm.getFormattedDate()).append(" の出勤時間 : hh:mm のフォーマットで入力してください。<br>");
				hasErrors = true;
			}

			// 退勤時間チェック
			if (endTime != null && !endTime.isEmpty() && !timePattern.matcher(endTime).matches()) {
				errorMessage.append(dailyForm.getFormattedDate()).append(" の退勤時間 : hh:mm のフォーマットで入力してください。<br>");
				hasErrors = true;
			}

			// 備考欄文字数チェック
			if (remarks != null && !remarks.isEmpty()) {
				if (remarks.matches(".*[\\x00-\\x7F].*")) {
					errorMessage.append(dailyForm.getFormattedDate()).append(" の備考 : 全角文字で入力してください。<br>");
					hasErrors = true;
				}
				if (remarks.length() > 20) {
					errorMessage.append(dailyForm.getFormattedDate()).append(" の備考 : 全角20文字以内で入力してください。<br>");
					hasErrors = true;
				}
			}

			// ステータス必須チェック
			if (status == null) {
				errorMessage.append(dailyForm.getFormattedDate()).append(" のステータス : 選択してください。<br>");
				hasErrors = true;
			}
		}
		return hasErrors ? errorMessage.toString() : null;
	}

	/**
	 * 承認申請ボタン押下 月次勤怠申請を登録する 却下後承認申請更新
	 * 
	 * @param year
	 * @param month
	 * @param user
	 * @return 処理結果メッセージ
	 */
	public String registerOrUpdateMonthlyAttendanceReq(Integer year, Integer month, Users user) {
		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate startDate = yearMonth.atDay(1);

		MonthlyAttendanceReq existingReq = monthlyAttendanceReqMapper.findByUserAndYearMonth(user.getId(),
				java.sql.Date.valueOf(startDate));
		MonthlyAttendanceReq req = new MonthlyAttendanceReq();
		req.setUserId(user.getId());
		req.setTargetYearMonth(java.sql.Date.valueOf(startDate.withDayOfMonth(1)));
		req.setDate(java.sql.Date.valueOf(LocalDate.now()));
		req.setStatus(1); // 承認待ち

		if (existingReq != null) {
			req.setId(existingReq.getId()); // 更新するためにIDを設定
			monthlyAttendanceReqMapper.update(req); // 却下後申請更新処理
			return "承認申請が更新されました。";
		} else {
			monthlyAttendanceReqMapper.insert(req); // 新規申請登録処理
			return "承認申請が完了しました。";
		}

	}

	/**
	 * 月次勤怠申請のIDで取得
	 * 
	 * @param id 月次勤怠申請ID
	 * @return 指定されたIDの月次勤怠申請
	 */
	public MonthlyAttendanceReq getMonthlyAttendanceReqById(Integer id) {
		return monthlyAttendanceReqMapper.findById(id);
	}

	/**
	 * 承認ボタン押下 ステータスを更新する
	 * 
	 * @param id     月次勤怠申請ID
	 * @param status 承認ステータス
	 * @return 承認結果メッセージ
	 */
	public String approvalAttendance(Integer id, int status) {
		// 申請IDで申請内容を取得
		MonthlyAttendanceReq req = monthlyAttendanceReqMapper.findById(id);
		// ステータスを承認済みに設定
		req.setStatus(status);
		monthlyAttendanceReqMapper.updateStatus(req);
		// メッセージ追加
		String userName = req.getUserName();
		Date targetDate = req.getTargetYearMonth();
		LocalDate localDate = targetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM"));

		return userName + "の" + formattedDate + "における承認申請が承認されました。";
	}

	/**
	 * 却下ボタン押下 ステータスを更新する
	 * 
	 * @param id 月次勤怠申請ID
	 * @param status 却下ステータス
	 * @return 却下結果メッセージ
	 */
	public String rejectAttendance(Integer id, int status) {
		// 申請IDで申請内容を取得
		MonthlyAttendanceReq req = monthlyAttendanceReqMapper.findById(id);
		// ステータスを却下済みに設定
		req.setStatus(status);
		monthlyAttendanceReqMapper.updateStatus(req);
		// メッセージ追加
		String userName = req.getUserName();
		Date targetDate = req.getTargetYearMonth();
		LocalDate localDate = targetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM"));

		return userName + "の" + formattedDate + "における承認申請が却下されました。";
	}

}
