package com.example.demo.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import com.example.demo.dto.NotificationsDto;
import com.example.demo.dto.UsersDto;
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
	@Autowired
	private NotificationsService notificationsService;

	/**
	 * ステータスが1(承認待ち)と4(訂正承認待ち)の申請一覧を取得する
	 *
	 * @return ステータスが1の月次勤怠申請のリスト
	 */
	public List<MonthlyAttendanceReqDto> findAllAttendance() {
		List<MonthlyAttendanceReqDto> attendanceList = monthlyAttendanceReqMapper.findAllWithStatus();
		// ステータスによってapplicationTypeを設定
		for (MonthlyAttendanceReqDto dto : attendanceList) {
			if (dto.getApproverName() != null && !dto.getApproverName().isEmpty()) {
				dto.setApplicationType("勤怠訂正");
			} else if (dto.getStatus() == 1) {
				dto.setApplicationType("勤怠申請");
			}
		}
		return attendanceList;
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
			dailyForm.setErrorFlag(false);

			attendanceForm.getDailyAttendanceList().add(dailyForm);
		}

		return attendanceForm;
	}

	/**
	 * 勤務合計時間の計算
	 * 
	 */
	public String totalAttendance(AttendanceForm attendanceForm) {
		long totalMinutes = 0;

		for (DailyAttendanceForm dailyAttendanceForm : attendanceForm.getDailyAttendanceList()) {
			String start = dailyAttendanceForm.getStartTime();
			String end = dailyAttendanceForm.getEndTime();

			// 空白またはnullの場合はスキップ
			if (start == null || start.isEmpty() || end == null || end.isEmpty()) {
				continue; // 処理をスキップして次のループへ
			}

			// 時刻をパース
			LocalTime startTime = LocalTime.parse(start);
			LocalTime endTime = LocalTime.parse(end);

			// 開始時刻と終了時刻の間の分を計算
			Duration duration = Duration.between(startTime, endTime);
			totalMinutes += duration.toMinutes(); // 総分数を加算
		}
		// 時間と分を計算
		long hours = totalMinutes / 60; // 総時間数
		long minutes = totalMinutes % 60; // 残りの分数

		// フォーマットして返す
		return String.format("%d時間%d分", hours, minutes);
	}

	/**
	 * 承認申請ボタンの活性非活性チェック
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
		return "却下".equals(status) || "未申請".equals(status) || "訂正承認済み".equals(status); //true
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

		// 先頭と最後の Date を取得
		java.sql.Date startDate = new java.sql.Date(dailyAttendanceList.get(0).getDate().getTime());
		java.sql.Date endDate = new java.sql.Date(
				dailyAttendanceList.get(dailyAttendanceList.size() - 1).getDate().getTime());

		// Date 範囲で勤怠情報を取得 (既存の情報)
		List<AttendanceDto> existingAttendances = attendanceMapper.findAttendanceByUserIdAndDateRange(userId, startDate,
				endDate);

		// dateをキー、AttendanceDtoを値とする
		Map<Date, AttendanceDto> existingAttendanceMap = existingAttendances.stream()
				.collect(Collectors.toMap(AttendanceDto::getDate, attendance -> attendance));

		List<Attendance> attendanceList = new ArrayList<>();

		for (DailyAttendanceForm dailyForm : dailyAttendanceList) {
			Date date = dailyForm.getDate();

			if (dailyForm.getStatus() != null) {
				// 既存の勤怠情報があるかどうか確認する
				AttendanceDto searchAttendance = existingAttendanceMap.get(date);

				// 新しい勤怠オブジェクトを作成
				Attendance attendance = new Attendance();
				attendance.setId(searchAttendance != null ? searchAttendance.getId() : null);
				attendance.setUserId(userId);
				attendance.setDate(dailyForm.getDate());
				attendance.setStatus(dailyForm.getStatus());
				attendance.setStartTime(dateUtil.stringToTime(dailyForm.getStartTime()));
				attendance.setEndTime(dateUtil.stringToTime(dailyForm.getEndTime()));
				attendance.setRemarks(dailyForm.getRemarks());

				attendanceList.add(attendance);

				// 既存の UserNotifications を userId と targetDate,notificationType で検索
				String notificationType = "勤怠未提出";
				notificationsService.checkNotifications(userId, date, notificationType);
			}
		}
		if (!attendanceList.isEmpty()) {
			attendanceMapper.upsert(attendanceList);
		}

		return "勤怠情報が登録されました。";
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
		Pattern timePattern = Pattern.compile("^[0-9]{2}:[0-9]{2}$");

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

			// ステータスに応じたチェック
			if (status != null) {
				boolean isTimeRequired = (status == 0 || status == 3 || status == 6 || status == 7 || status == 8
						|| status == 10);
				boolean isTimeNotAllowed = (status == 1 || status == 2 || status == 4 || status == 5 || status == 9
						|| status == 11);

				if (isTimeRequired
						&& (startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty())) {
					errorMessage.append(dailyForm.getFormattedDate()).append(" の出退勤時間を入力してください。<br>");
					hasErrors = true;
					dailyForm.setErrorFlag(true);
				}

				if (isTimeNotAllowed
						&& (startTime != null && !startTime.isEmpty() || endTime != null && !endTime.isEmpty())) {
					errorMessage.append(dailyForm.getFormattedDate()).append(" は出退勤時間が不要です。勤務状況か出退勤時間を変更してください。<br>");
					hasErrors = true;
					dailyForm.setErrorFlag(true);
				}
			}

			// 出勤時間チェック
			if (startTime != null && !startTime.isEmpty() && !timePattern.matcher(startTime).matches()) {
				errorMessage.append(dailyForm.getFormattedDate()).append(" の出勤時間 : hh:mm のフォーマットで入力してください。<br>");
				hasErrors = true;
				dailyForm.setErrorFlag(true);
			}

			// 退勤時間チェック
			if (endTime != null && !endTime.isEmpty() && !timePattern.matcher(endTime).matches()) {
				errorMessage.append(dailyForm.getFormattedDate()).append(" の退勤時間 : hh:mm のフォーマットで入力してください。<br>");
				hasErrors = true;
				dailyForm.setErrorFlag(true);
			}

			//出勤時間<退勤時間チェック
			if (startTime != null && !startTime.isEmpty() && timePattern.matcher(startTime).matches() 
					&& endTime != null && !endTime.isEmpty() && timePattern.matcher(endTime).matches()) {
				LocalTime startTimeDate = dateUtil.stringToLocalTime(startTime);
				LocalTime endTimeDate = dateUtil.stringToLocalTime(endTime);

				if (endTimeDate.isBefore(startTimeDate)) {
					errorMessage.append(dailyForm.getFormattedDate())
							.append(" の勤務時間 : 出勤時間より退勤時間の方が早いです<br>");
					hasErrors = true;
					dailyForm.setErrorFlag(true);
				}
			}

			// 備考欄文字種、文字数チェック
			if (remarks != null && !remarks.isEmpty()) {
				if (//remarks.matches(".*[\\x00-\\x7F].*") ||
				remarks.length() > 20) {
					errorMessage.append(dailyForm.getFormattedDate()).append(" の備考 : 20文字以内の全角文字のみで入力してください。<br>");
					hasErrors = true;
					dailyForm.setErrorFlag(true);
				}
			}

			// ステータス必須チェック
			if (status == null) {
				errorMessage.append(dailyForm.getFormattedDate()).append(" の勤務状況 : 選択してください。<br>");
				hasErrors = true;
				dailyForm.setErrorFlag(true);
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

		// 既存の UserNotifications を userId と targetDate,notificationType で検索
		String notificationType;
		if (existingReq != null) {
			notificationType = "訂正申請結果";
		} else {
			notificationType = "勤怠申請未提出";
		}
		notificationsService.checkNotifications(user.getId(), java.sql.Date.valueOf(startDate.withDayOfMonth(1)),
				notificationType);

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
	 * 訂正申請ボタン押下 承認申請更新
	 * 
	 * @param year
	 * @param month
	 * @param user
	 * @param comment
	 * @return 処理結果メッセージ
	 */
	public String correctMonthlyAttendanceReq(Integer year, Integer month, Users user, String comment) {
		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate startDate = yearMonth.atDay(1);

		MonthlyAttendanceReq existingReq = monthlyAttendanceReqMapper.findByUserAndYearMonth(user.getId(),
				java.sql.Date.valueOf(startDate));
		MonthlyAttendanceReq req = new MonthlyAttendanceReq();
		req.setId(existingReq.getId());
		req.setUserId(user.getId());
		req.setTargetYearMonth(java.sql.Date.valueOf(startDate.withDayOfMonth(1)));
		req.setDate(java.sql.Date.valueOf(LocalDate.now()));
		req.setStatus(4); // 訂正承認待ち
		req.setComment(comment);

		monthlyAttendanceReqMapper.update(req);
		return "訂正申請が完了しました。";
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
	public String approvalAttendance(Integer id, String name) {
		// 申請IDで申請内容を取得
		MonthlyAttendanceReq req = monthlyAttendanceReqMapper.findById(id);
		req.setDate(java.sql.Date.valueOf(LocalDate.now()));
		req.setComment(null);
		req.setApproverName(name);
		// ステータスを設定
		if (req.getStatus() == 4) {
			req.setStatus(5);
		} else {
			req.setStatus(2);
		}
		monthlyAttendanceReqMapper.updateStatus(req);
		// メッセージ追加
		String userName = req.getUserName();
		Date targetDate = req.getTargetYearMonth();
		LocalDate localDate = targetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM"));
		String message;
		if (req.getApproverName() != null && req.getStatus() == 5) {
			message = userName + "の" + formattedDate + "における訂正申請が承認されました。";
		} else {
			message = userName + "の" + formattedDate + "における承認申請が承認されました。";
		}

		//訂正申請時、通知作成
		if (req.getApproverName() != null && req.getStatus() == 5) {
			String notificationType = "訂正申請結果";
			java.sql.Date targetsqlDate = new java.sql.Date(targetDate.getTime());
			Integer userId = req.getUserId();
			String content = formattedDate + "における勤怠訂正申請が承認されました。";
			Long notificationId = notificationsService.createNotification(content, notificationType, targetsqlDate);
			notificationsService.linkNotificationToUser(userId, notificationId, notificationType);
		}

		return message;
	}

	/**
	 * 却下ボタン押下 ステータスを更新する
	 * 
	 * @param id 月次勤怠申請ID
	 * @param status 却下ステータス
	 * @return 却下結果メッセージ
	 */
	public String rejectAttendance(Integer id, String comment, String name) {
		// 申請IDで申請内容を取得
		MonthlyAttendanceReq req = monthlyAttendanceReqMapper.findById(id);
		req.setDate(java.sql.Date.valueOf(LocalDate.now()));
		req.setComment(comment == null || comment.trim().isEmpty() ? null : comment);
		req.setApproverName(name);
		// ステータスを設定
		if (req.getStatus() == 4) {
			req.setStatus(2);
		} else {
			req.setStatus(3);
		}
		monthlyAttendanceReqMapper.updateStatus(req);

		// メッセージ追加
		String userName = req.getUserName();
		Date targetDate = req.getTargetYearMonth();
		LocalDate localDate = targetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy/MM"));
		String message;
		if (req.getApproverName() != null && req.getStatus() == 2) {
			message = userName + "の" + formattedDate + "における訂正申請が却下されました。";
		} else {
			message = userName + "の" + formattedDate + "における承認申請が却下されました。";
		}

		//通知作成
		java.sql.Date targetsqlDate = new java.sql.Date(targetDate.getTime());
		Integer userId = req.getUserId();
		String notificationType;
		String content;
		if (req.getApproverName() != null) {
			notificationType = "訂正申請結果";
			content = formattedDate + "における勤怠訂正申請が却下されました。";
		} else {
			notificationType = "勤怠申請結果";
			content = formattedDate + "における勤怠申請が却下されました。";
		}
		Long notificationId = notificationsService.createNotification(content, notificationType, targetsqlDate);
		notificationsService.linkNotificationToUser(userId, notificationId, notificationType);

		return message;
	}

	/**
	 * 勤怠提出の有無確認
	 * 
	 */
	public void checkAttendance() {
		// 日付のフォーマット用フォーマッターを作成
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
		// 先月の年月を取得
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		LocalDate lastDate = lastMonth.atDay(1);
		// 前日の日付を取得
		LocalDate previousDay = LocalDate.now().minusDays(1);
		java.sql.Date date = java.sql.Date.valueOf(previousDay);

		//土日かどうかチェック
		Boolean weekEnd = notificationsService.notWeekend(previousDay);
		if (weekEnd) {
			String formattedDate = previousDay.format(formatter);
			// 前日の日報を提出していないユーザーを検索
			List<UsersDto> users = attendanceMapper.findUsersWithoutReport(date);

			// 日報未提出の通知を作成し、全ユーザーに通知を紐付け
			String notificationType = "勤怠未提出";
			String content = formattedDate + "の勤怠が提出されていません";
			notificationsService.createNotificationForUsers(users, previousDay, notificationType, content, date);
		}
		// 今日が月初の場合のみ通知作成
		LocalDate today = LocalDate.now();
		if (today.getDayOfMonth() == 1) {
			//先月の勤怠を提出していないユーザーを検索
			List<UsersDto> users = monthlyAttendanceReqMapper
					.findUsersWithoutAttendance(java.sql.Date.valueOf(lastDate));
			String content = "先月の勤怠が申請されていません。";
			String notificationType = "勤怠申請未提出";
			notificationsService.createNotificationForUsers(users, previousDay, notificationType, content,
					java.sql.Date.valueOf(lastDate));
		}
	}

	/**
	 * 勤怠提出の有無確認(マネージャー用)
	 * 
	 * @return お知らせ情報
	 */
	public List<NotificationsDto> checkManagerAttendance() {
		// 日付のフォーマット用フォーマッターを作成
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
		// 先月の年月を取得
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		LocalDate lastDate = lastMonth.atDay(1);
		// 前日の日付を取得
		LocalDate previousDay = LocalDate.now().minusDays(1);
		java.sql.Date date = java.sql.Date.valueOf(previousDay);

		List<NotificationsDto> notifications = new ArrayList<>();

		//土日かどうかチェック
		Boolean weekEnd = notificationsService.notWeekend(previousDay);
		if (weekEnd) {
			String formattedDate = previousDay.format(formatter);
			// 前日の日報を提出していないユーザーを検索
			List<UsersDto> users = attendanceMapper.findUsersWithoutReport(date);

			// マネージャーに未提出ユーザー数のお知らせ
			int missingAttendanceCount = users.size();
			if (missingAttendanceCount > 0) {
				String managerContent = formattedDate + "の勤怠が" + missingAttendanceCount + "人未提出です ("
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "時点" + ")";
				// マネージャーの勤怠未提出通知を作成
				NotificationsDto notificationsDto = new NotificationsDto();
				notificationsDto.setContent(managerContent);
				notificationsDto.setNotificationType("勤怠未提出");
				notificationsDto.setCreatedAt(LocalDateTime.now());

				// 未提出ユーザーのリストを設定
				notificationsDto.setUsers(users);

				notifications.add(notificationsDto);
			}
		}

		// 今日が月初の場合のみ通知作成
		LocalDate today = LocalDate.now();
		if (today.getDayOfMonth() == 1) {
			//先月の勤怠を提出していないユーザーを検索
			List<UsersDto> users = monthlyAttendanceReqMapper
					.findUsersWithoutAttendance(java.sql.Date.valueOf(lastDate));
			// 日付のフォーマット用フォーマッターを作成
			DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("yyyy年M月");
			String formattedDate = lastMonth.format(formatterMonth);
			int missingReportCount = users.size();
			String managerContent = formattedDate + "の勤怠が" + missingReportCount + "人申請されていません("
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "時点" + ")";
			// マネージャーの日報未提出通知を作成
			NotificationsDto notificationsDto = new NotificationsDto();
			notificationsDto.setContent(managerContent);
			notificationsDto.setNotificationType("勤怠申請未提出");
			notificationsDto.setCreatedAt(LocalDateTime.now());

			// 未提出ユーザーのリストを設定
			notificationsDto.setUsers(users);

			notifications.add(notificationsDto);
		}
		return notifications;
	}

}
