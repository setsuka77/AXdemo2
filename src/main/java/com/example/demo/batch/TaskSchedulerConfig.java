package com.example.demo.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.service.AttendanceService;
import com.example.demo.service.DailyReportService;
import com.example.demo.service.SendMailService;

@Component
public class TaskSchedulerConfig {

	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private LogService logService; // ログサービス

	@Autowired
	private PushNotificationService pushNotificationService;

	/**
	 * 毎日18時10分にバッチ処理を実行、日報勤怠未提出者にプッシュ通知を送信
	 */
	// @Scheduled(cron = "0 10 18 * * ?")
	 @Scheduled(cron = "0 */2 * * * ?")
	public void sendNotifications() {
		try {
			//dailyReportService.checkDailyReport();
			//attendanceService.checkAttendance();
			// 未提出者にプッシュ通知を送信
			pushNotificationService.sendNotificationToUnsubmittedUsers("未提出通知", "本日の日報を提出してください", "/icon.png");
			logService.logInfo("プッシュ通知送信完了");
		} catch (Exception e) {
			logService.logError("プッシュ通知送信エラー", e);
		}
	}

	/**
	 * 毎日午前9時半に日報勤怠未提出者通知メールをマネージャに送信するバッチ処理
	 */
	// @Scheduled(cron = "0 30 9 * * ?") // 毎日午前9時半に実行
	// @Scheduled(cron = "0 */2 * * * ?")
	public void performAttendanceAndReportSendMail() {
		try {
			// 未提出者の確認を行い、権限を指定してメール送信
			sendMailService.sendAttendanceAndReportReminderMail("2");
			// ログ出力
			logService.logInfo("メール送信完了");
		} catch (Exception e) {
			// エラーログ出力
			logService.logError("エラー発生", e);
		}
	}
}
