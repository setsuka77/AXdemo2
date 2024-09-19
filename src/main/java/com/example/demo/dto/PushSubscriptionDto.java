package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PushSubscriptionDto {

	/** サブスクリプションID */
    private Long id;
    /** ユーザID */
    private Integer userId;
    /** サブスクリプションエンドポイントのURL */
	private String endpoint;
	/** p256dhとauth */
    private SubscriptionKeys keys;
    /** サブスクリプション登録日時 */
    private LocalDateTime createdAt;
    /** サブスクリプションの状態 'active' (アクティブ), 'inactive' (非アクティブ) */
    private String status;

    @Data
    public static class SubscriptionKeys {
    	/** Web Push暗号化キー */
        private String p256dh;
        /** Web Push認証キー */
        private String auth;
    }
	
}
