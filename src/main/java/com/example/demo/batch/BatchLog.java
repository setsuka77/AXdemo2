package com.example.demo.batch;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BatchLog {

	/** バッチログのID（主キー） */
	private Long id;
	/** バッチ処理の名前 */
    private String jobName;
    /** バッチ処理のステータス (STARTED, COMPLETED, FAILEDなど) */
    private String status;
    /** バッチ処理の開始時間 */
    private LocalDateTime startTime;
    /** バッチ処理の終了時間 */
    private LocalDateTime endTime;
    /** エラーメッセージ（エラーが発生した場合のみ） */
    private String errorMessage;
    /** レコード作成日時 */
    private LocalDateTime createdAt; 
}
