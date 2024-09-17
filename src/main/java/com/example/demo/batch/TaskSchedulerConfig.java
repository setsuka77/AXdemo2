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

	/**
	 *  毎日18時10分にバッチ処理を実行、プッシュ通知を送信
	 */
	// @Scheduled(cron = "0 10 18 * * ?")
	// @Scheduled(cron = "0 */2 * * * ?")
	public void sendNotifications() {
		try {
			dailyReportService.checkDailyReport();
			attendanceService.checkAttendance();
			logService.logInfo("チェックできたよ");
		} catch (Exception e) {
			logService.logError("エラーが出てるよ", e);
		}
	}

	/**
     * 毎日午前9時半に日報未提出の社員にメールを送信するバッチ処理
     */
    // @Scheduled(cron = "0 30 9 * * ?") // 毎日午前9時半に実行
	// @Scheduled(cron = "0 */2 * * * ?")
    public void performDailyReportSendMail() {
        try {
            // 役職に基づいてメール送信
            sendMailService.sendAttendanceAndReportReminderMail("2"); // ここで適切なロールを指定

            // ログ出力
            logService.logInfo("チェックできたよ");
        } catch (Exception e) {
            // エラーログ出力
            logService.logError("エラーが出てるよ", e);
        }
    }
}
