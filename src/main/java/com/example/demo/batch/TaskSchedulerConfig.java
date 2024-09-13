package com.example.demo.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.service.DailyReportService;

@Component
public class TaskSchedulerConfig {
	
	@Autowired
    private DailyReportService dailyReportService;  // バッチ処理サービス

    @Autowired
    private LogService logService;  // ログサービス

    // 毎日午前18時にバッチ処理を実行
    //@Scheduled(cron = "0 0 18 * * ?")
    @Scheduled(cron = "0 */5 * * * ?")
    public void performDailyReportCheck() {
        try {
            dailyReportService.checkDailyReport();
            logService.logInfo("チェックできたよ");
        } catch (Exception e) {
            logService.logError("エラーが出てるよ", e);
        }
    }
}
