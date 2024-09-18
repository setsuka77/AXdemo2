package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserNotificationsDto {

	/** お知らせID */
	private Long id;
	/** 対象ユーザーID */
    private Integer userId;
    /** 対象通知ID */
    private Long notificationId; 
    /** 既読/未読フラグ */
    private Boolean isRead;
    /** 受信日時 */
    private LocalDateTime createdAt;
    /** お知らせ表示フラグ */
    private Boolean isVisible;
}
