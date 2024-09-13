package com.example.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private BatchLogMapper batchLogMapper;
    
    public void logInfo(String message) {
        logger.info(message);
        saveLogToDB("INFO", message, null);
    }

    public void logError(String message, Exception e) {
        logger.error(message, e);
        saveLogToDB("ERROR", message, e.getMessage());
    }
    
    private void saveLogToDB(String status, String message, String errorMessage) {
        BatchLog log = new BatchLog();
        log.setJobName("DailyReportBatch");  // 例: 固定のバッチ名。状況に応じて動的に設定可能
        log.setStatus(status);
        log.setStartTime(LocalDateTime.now());
        log.setEndTime(LocalDateTime.now());
        log.setErrorMessage(errorMessage);
        log.setCreatedAt(LocalDateTime.now());

        batchLogMapper.insertBatchLog(log);
    }

    public void logWarn(String message) {
        logger.warn(message);
    }

    public void logDebug(String message) {
        logger.debug(message);
    }
}
