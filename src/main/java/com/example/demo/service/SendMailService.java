package com.example.demo.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDto;
import com.example.demo.entity.Users;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.mapper.UsersMapper;

@Service
public class SendMailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UsersMapper usersMapper;

	@Autowired
	private DailyReportMapper dailyReportMapper;

	@Autowired
	private AttendanceMapper attendanceMapper;

	@Autowired
	private MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;

	/**
	 * テストメール送信(Controller経由)
	 * 
	 * @param to
	 * @param subject
	 * @param text
	 */
	public void sendSimpleMail(String to) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom("no-reply@axsystem.com"); // 差出人のアドレスを指定
		message.setSubject("テストメール");
		message.setText("これはテストメールです。");

		mailSender.send(message);
	}

	/**
	 * システム障害通知メール送信
	 * 
	 * @param to 送信先のメールアドレス
	 */
	public void sendSystemFailureMail(String role, String emailBody) {
		List<Users> users = usersMapper.findUsersByRole(role);

		for (Users user : users) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setFrom("no-reply@axsystem.com"); // 差出人のアドレスを指定
			message.setSubject("【重要】システム障害が発生しました"); // システム障害の件名
			message.setText(emailBody); // メールの本文にエラー内容を設定

			mailSender.send(message);
		}
	}

	/**
	 * 勤怠・日報未提出の社員を翌朝にマネージャに通知するメール送信
	 * 
	 * @param employeeNames 未提出の社員のリスト
	 */
	public void sendAttendanceAndReportReminderMail(String role) {

		// メール送信先情報の取得
		List<Users> users = usersMapper.findUsersByRole(role);

		// 前日の日報未提出の社員リストを取得
		List<UsersDto> employeesWithoutReport = dailyReportMapper
				.findUsersWithoutReport(Date.valueOf(LocalDate.now().minusDays(1)));

		// 前日の勤怠未提出の社員リストを取得
		List<UsersDto> employeesWithoutAttendance = attendanceMapper
				.findUsersWithoutReport(Date.valueOf(LocalDate.now().minusDays(1)));

		// 前月の勤怠月次承認未申請の社員リストを取得
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		LocalDate lastDate = lastMonth.atDay(1);
		List<UsersDto> employeesWithoutMonthlyAttendance = monthlyAttendanceReqMapper
				.findUsersWithoutAttendance(Date.valueOf(lastDate));

		// ユーザごとの未提出項目を保持するマップ
		Map<Integer, StringBuilder> userSubmissionStatus = new HashMap<>();

		// 日報未提出者をマップに追加
		for (UsersDto user : employeesWithoutReport) {
			userSubmissionStatus.computeIfAbsent(user.getId(), k -> new StringBuilder()).append("日報、");
		}

		// 勤怠未提出者をマップに追加
		for (UsersDto user : employeesWithoutAttendance) {
			userSubmissionStatus.computeIfAbsent(user.getId(), k -> new StringBuilder()).append("勤怠、");
		}

		// 月次勤怠未承認者をマップに追加
		for (UsersDto user : employeesWithoutMonthlyAttendance) {
			userSubmissionStatus.computeIfAbsent(user.getId(), k -> new StringBuilder()).append("月次勤怠承認申請、");
		}

		// 最後の「、」を削除
		for (StringBuilder submissionStatus : userSubmissionStatus.values()) {
			if (submissionStatus.length() > 0) {
				submissionStatus.setLength(submissionStatus.length() - 1); // 最後の「、」を削除
			}
		}

		// ユーザIDでソート
		List<Map.Entry<Integer, StringBuilder>> sortedEntries = userSubmissionStatus.entrySet().stream()
				.sorted(Map.Entry.comparingByKey()).collect(Collectors.toList());

		// マネージャごとにメールを送信
		for (Users user : users) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setFrom("no-reply@axsystem.com"); // 差出人のアドレスを指定
			message.setSubject("【通知】未提出の勤怠・日報・月次承認の確認"); // 勤怠・日報未提出の件名

			// メール本文を構築
			StringBuilder emailContent = new StringBuilder("以下の社員に未提出の項目があります。\n\n");

			for (Map.Entry<Integer, StringBuilder> entry : sortedEntries) {
				// IDから名前を取得
				String userName = getUserNameById(entry.getKey());
				emailContent.append("■ " + userName) // 社員名
						.append(":\n").append(entry.getValue().toString()) // 未提出のリスト
						.append("\n");
			}

			// メールが空でない場合は送信
			if (userSubmissionStatus.isEmpty()) {
				message.setText("全員が勤怠・日報・月次承認申請を提出しています。");
			} else {
				message.setText(emailContent.toString() + "\n該当社員に確認をお願いします。");
			}

			mailSender.send(message);
		}
	}

	// IDからユーザ名を取得するためのメソッド
	private String getUserNameById(Integer id) {
		Users user = usersMapper.findByUserId(id);
		return user != null ? user.getName() : "不明";
	}

}
