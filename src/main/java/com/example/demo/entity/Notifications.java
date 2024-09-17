package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Notifications {
	
	/** お知らせID */
    private Long id;
    /** お知らせ本文 */
    private String content;
    /** 通知の種類 */
    private String notificationType;
    /** 作成日時 */
    private LocalDateTime createdAt;	

}
