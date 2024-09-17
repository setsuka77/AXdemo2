package com.example.demo.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDto;
import com.example.demo.entity.Users;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.mapper.UsersMapper;

@Service
public class SendMailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private DailyReportMapper  dailyReportMapper;

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
	public void sendSystemFailureMail(String role) {
		List<Users> users = usersMapper.findUsersByRole(role);
		
		for (Users user : users) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setFrom("no-reply@axsystem.com"); // 差出人のアドレスを指定
		message.setSubject("【重要】システム障害が発生しました"); // システム障害の件名
		message.setText("現在、一時的なシステム障害が発生しています。至急、対応してください。"); // システム障害の本文

		mailSender.send(message);
		}
	}

	/**
	 * 勤怠・日報未提出の社員を翌朝にマネージャに通知するメール送信
	 * 
	 * @param to            送信先のマネージャのメールアドレス
	 * @param employeeNames 未提出の社員のリスト
	 */
	public void sendAttendanceAndReportReminderMail(String role) {
		List<Users> users = usersMapper.findUsersByRole(role);

		// 未提出社員のリストを作成
		// DailyreportServiceから未提出の社員リストを取得
	    List<UsersDto> employeesWithoutReport = dailyReportMapper.findUsersWithoutReport(Date.valueOf(LocalDate.now().minusDays(1)));
		
		StringBuilder employeeList = new StringBuilder();
		for (UsersDto user : employeesWithoutReport) {
			employeeList.append("【日報】\n" + user.getName()).append("\n");
		}

		// マネージャごとにメールを送信
		for (Users user : users) {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setFrom("no-reply@axsystem.com"); // 差出人のアドレスを指定
			message.setSubject("【通知】未提出の勤怠・日報の確認"); // 勤怠・日報未提出の件名
		
		// メールの本文
		message.setText("以下の社員が前日の勤怠・日報を提出していません。\n\n" + employeeList.toString() + "\n該当社員に確認をお願いします。");

		mailSender.send(message);
		}
	}

}
