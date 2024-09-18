package com.example.demo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class PushSubscription {

    @Id
    /** サブスクリプションID */
    private Long id;
    /** ユーザID */
    private Integer userId;
    /** サブスクリプションエンドポイントのURL */
    private String endpoint;
    /** Web Push暗号化キー */
    private String p256dh;
    /** Web Push認証キー */
    private String auth;
    /** サブスクリプション登録日時 */
    private LocalDateTime createdAt;
    /** サブスクリプションの状態 'active' (アクティブ), 'inactive' (非アクティブ) */
    private String status;
    
}
