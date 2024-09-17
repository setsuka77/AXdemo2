package com.example.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.service.SendMailService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class LogService {

	private static final Logger logger = LoggerFactory.getLogger(LogService.class);

	@Autowired
	private BatchLogMapper batchLogMapper;

	@Autowired
	private SendMailService sendMailService;

	/**
	 * 情報レベルのログを記録し、データベースに保存する
	 * 
	 * @param message ログに記録するメッセージ
	 */
	public void logInfo(String message) {
		logger.info(message);
		saveLogToDB("INFO", message, null);
	}

	/**
	 * ワーニングレベルのログを記録、データベースに保存する
	 *
	 * @param message ログに記録するメッセージ
	 */
	public void logWarn(String message) {
		logger.warn(message);
		saveLogToDB("WARN", message, null);
	}

	// エラーメッセージのバッファ
	private List<String> errorBuffer = new ArrayList<>();

	// 一定時間後にメールを送信するためのタイマー
	private Timer timer = new Timer();

	/**
	 * エラーレベルのログを記録し、バッファに追加して一定時間後にメールを送信
	 *
	 * @param message ログに記録するメッセージ
	 * @param e       発生した例外
	 */
	public void logError(String message, Exception e) {
		logger.error(message, e);
		saveLogToDB("ERROR", message, e.getMessage());
		addToBuffer("【種別】" + message + "\n【詳細】" + e.getMessage());
	}

	/**
	 * エラーメッセージをバッファに追加し、タイマーで一定時間後にメールを送信
	 *
	 * @param logMessage バッファに追加するログメッセージ
	 */
	private void addToBuffer(String logMessage) {
		synchronized (errorBuffer) {
			errorBuffer.add(logMessage);
		}

		// タイマーをリセットして、メール送信を遅延させる
		timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				sendBufferedMessages();
			}
		}, 30000); // 30秒後にメール送信
	}

	/**
	 * バッファ内のメッセージをまとめて1通のメールとして送信
	 */
	private void sendBufferedMessages() {
		StringBuilder emailBody = new StringBuilder("システムに障害が発生しました。\n\n");

		synchronized (errorBuffer) {
			for (String log : errorBuffer) {
				emailBody.append(log).append("\n\n");
			}
			errorBuffer.clear(); // 送信後にバッファをクリア
		}

		sendMailService.sendSystemFailureMail("1", emailBody.toString());
	}

	/**
	 * デバッグレベルのログを記録 このログはデータベースには保存しない
	 * 
	 * @param message ログに記録するメッセージ
	 */
	public void logDebug(String message) {
		logger.debug(message);
	}

	/**
	 * ログ情報をデータベースに保存するためのメソッド
	 * 
	 * @param status       ログのステータス (例: INFO, ERROR, WARN)
	 * @param message      ログメッセージ
	 * @param errorMessage エラーメッセージ（エラーが発生した場合のみ）
	 */
	private void saveLogToDB(String status, String message, String errorMessage) {
		BatchLog log = new BatchLog();
		log.setJobName("DailyReportBatch"); // 例: 固定のバッチ名。状況に応じて動的に設定可能
		log.setStatus(status);
		log.setStartTime(LocalDateTime.now());
		log.setEndTime(LocalDateTime.now());
		log.setErrorMessage(errorMessage);
		log.setCreatedAt(LocalDateTime.now());

		batchLogMapper.insertBatchLog(log);
	}

}
