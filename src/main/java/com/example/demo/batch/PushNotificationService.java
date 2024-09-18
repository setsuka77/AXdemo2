package com.example.demo.batch;

import com.example.demo.dto.UsersDto;
import com.example.demo.entity.PushSubscription;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.service.PushSubscriptionService;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class PushNotificationService {

	@Value("${push.vapid.public-key}")
    private String publicKey;

    @Value("${push.vapid.private-key}")
    private String privateKey;

    @Value("${push.vapid.subject}")
    private String subject;
    
    @Autowired
    private PushSubscriptionService pushSubscriptionService;

    @Autowired
	private DailyReportMapper dailyReportMapper;

	@Autowired
	private AttendanceMapper attendanceMapper;
	
	private PushService pushService;

    @PostConstruct
    public void setup() throws GeneralSecurityException, IOException {
    	
    	Security.addProvider(new BouncyCastleProvider());
    	
    	pushService = new PushService()
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .setSubject(subject);
    }

    public void sendPushNotification(Subscription subscription, String message) throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {
    		Notification notification = new Notification(
        	    subscription.endpoint,  // subscriptionのエンドポイントが取得できるフィールド名
        	    subscription.keys.p256dh,  // P256dhキー
        	    subscription.keys.auth,  // Authキー
        	    message.getBytes()  // メッセージのバイトデータ
        	);
        pushService.send(notification);
    }

    public void sendNotificationToUnsubmittedUsers(String title, String body, String icon) {
        // 本日の日報未提出の社員リストを取得
        List<UsersDto> dailyReportUnsubmitted = dailyReportMapper.findUsersWithoutReport(Date.valueOf(LocalDate.now()));
        // 本日の勤怠未提出の社員リストを取得
        List<UsersDto> attendanceUnsubmitted = attendanceMapper.findUsersWithoutReport(Date.valueOf(LocalDate.now()));

        // 日報と勤怠の未提出者リストを統合
        List<UsersDto> unsubmittedUsers = dailyReportUnsubmitted.stream()
                .distinct()  // 重複を排除
                .collect(Collectors.toList());
        unsubmittedUsers.addAll(attendanceUnsubmitted.stream()
                .distinct()  // 重複を排除
                .collect(Collectors.toList()));

        // UserIDのリストを作成
        List<Integer> userId = unsubmittedUsers.stream()
                .map(UsersDto::getId)
                .distinct()  // 重複を排除
                .collect(Collectors.toList());

        // UserIDリストを基にサブスクリプションを一括で取得
        List<PushSubscription> subscriptions = pushSubscriptionService.findByUserId(userId);

        // 通知メッセージを作成
        String message = String.format("{\"title\": \"%s\", \"body\": \"%s\", \"icon\": \"%s\"}", title, body, icon);

        // サブスクリプションを基に通知を送信
        for (PushSubscription subscription : subscriptions) {
            if ("active".equals(subscription.getStatus())) {
                Subscription webPushSubscription = new Subscription(
                        subscription.getEndpoint(),
                        new Subscription.Keys(subscription.getP256dh(), subscription.getAuth())
                );
                try {
                    sendPushNotification(webPushSubscription, message);
                } catch (GeneralSecurityException | IOException | JoseException | ExecutionException | InterruptedException e) {
                    // エラーをログに出力
                    e.printStackTrace();
                }
            }
        }
    }
	
}
