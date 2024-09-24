package com.example.demo.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class NotificationsDto {
	
	/** お知らせID */
    private Long id;
    /** お知らせ本文 */
    private String content;
    /** 通知の種類 */
    private String notificationType;
    /** 作成日時 */
    private LocalDateTime createdAt;	
    /**　対象日 */
    private Date targetDate;
    
    // 日付を yyyy.MM.dd 形式で表示するためのメソッド
    public String createdAtFormatted() {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return createdAt.format(formatter);
    }
    
    //未提出ユーザー名前のリスト
    private List<UsersDto> users;

}
