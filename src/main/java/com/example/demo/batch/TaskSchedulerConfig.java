package com.example.demo.batch;

import org.springframework.beans.factory.annotation.Autowired;
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
	private LogService logService;

	@Autowired
	private PushNotificationService pushNotificationService;

	/**
	 * 毎日18時10分にバッチ処理を実行、日報勤怠未提出者にプッシュ通知を送信
	 */
	// @Scheduled(cron = "0 10 18 * * MON-FRI", zone = "Asia/Tokyo")
	//  @Scheduled(cron = "0 */1 * * * ?")
	public void sendNotifications() {
		try {
			 //dailyReportService.checkDailyReport();
			 //attendanceService.checkAttendance();
			throw new RuntimeException("テスト用のエラーです");
			/*// 未提出者にプッシュ通知を送信
			pushNotificationService.sendNotificationToUnsubmittedUsers("AX社内管理システム", "未提出のものがあります。メニューで確認してください。",
					"/icon.png");
			logService.logInfo("PushNotificationBatch", "プッシュ通知送信完了");*/
		} catch (Exception e) {
			logService.logError("PushNotificationBatch", "プッシュ通知送信エラー", e);
		}
	}

	/**
	 * 毎日午前9時半に日報勤怠未提出者通知メールをマネージャに送信するバッチ処理
	 */
	// @Scheduled(cron = "0 30 9 * * ?", zone = "Asia/Tokyo") // 毎日午前9時半に実行
	// @Scheduled(cron = "0 */2 * * * ?")
	public void performAttendanceAndReportSendMail() {
		try {
			// 未提出者の確認を行い、権限を指定してメール送信
			sendMailService.sendAttendanceAndReportReminderMail("2");
			// ログ出力
			logService.logInfo("AttendanceReportBatch", "メール送信完了");
		} catch (Exception e) {
			// エラーログ出力
			logService.logError("AttendanceReportBatch", "エラー発生", e);
		}
	}
}
