package com.example.demo.batch;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDto;
import com.example.demo.entity.PushSubscription;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.mapper.DailyReportMapper;
import com.example.demo.mapper.MonthlyAttendanceReqMapper;
import com.example.demo.service.PushSubscriptionService;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

@Service
public class PushNotificationService {

	// VAPIDの公開鍵を設定ファイルから取得
	@Value("${push.vapid.public-key}")
	private String publicKey;

	// VAPIDの秘密鍵を設定ファイルから取得
	@Value("${push.vapid.private-key}")
	private String privateKey;

	// VAPIDの通知送信者情報を設定ファイルから取得
	@Value("${push.vapid.subject}")
	private String subject;

	@Autowired
	private PushSubscriptionService pushSubscriptionService;

	@Autowired
	private DailyReportMapper dailyReportMapper;

	@Autowired
	private AttendanceMapper attendanceMapper;
	
	@Autowired
	private MonthlyAttendanceReqMapper monthlyAttendanceReqMapper;

	private PushService pushService;

	/**
	 * プッシュ通知設定 初期化処理
	 * 
	 * VAPIDの公開鍵、秘密鍵、送信者情報を使用してPushServiceを設定 BouncyCastleProviderをセキュリティプロバイダとして追加
	 * 
	 * @throws GeneralSecurityException セキュリティ関連の例外
	 * @throws IOException              入出力例外
	 */
	@PostConstruct
	public void setup() throws GeneralSecurityException, IOException {

		// セキュリティプロバイダにBouncyCastleを追加
		Security.addProvider(new BouncyCastleProvider());

		// VAPID情報を設定してPushServiceを初期化
		pushService = new PushService().setPublicKey(publicKey).setPrivateKey(privateKey).setSubject(subject);
	}

	/**
	 * WebPush通知を送信
	 * 
	 * @param subscription ユーザーのWebPushサブスクリプション情報
	 * @param message      通知メッセージ
	 * @throws GeneralSecurityException セキュリティ関連の例外
	 * @throws IOException              入出力例外
	 * @throws JoseException            WebPushの署名に関連する例外
	 * @throws ExecutionException       非同期処理関連の例外
	 * @throws InterruptedException     スレッド割り込み例外
	 */
	public void sendPushNotification(Subscription subscription, String message)
			throws GeneralSecurityException, IOException, JoseException, ExecutionException, InterruptedException {

		// 通知の作成
		Notification notification = new Notification(subscription.endpoint, // subscriptionのエンドポイントが取得できるフィールド名
				subscription.keys.p256dh, // P256dhキー
				subscription.keys.auth, // Authキー
				message.getBytes() // メッセージのバイトデータ
		);
		// WebPush通知の送信
		pushService.send(notification);
	}

	/**
	 * 日報または勤怠の未提出ユーザーに通知を送信
	 * 
	 * @param title 通知のタイトル
	 * @param body  通知の本文
	 * @param icon  通知のアイコンURL
	 */
	public void sendNotificationToUnsubmittedUsers(String title, String body, String icon) {
		// 本日の日報未提出の社員リストを取得
		List<UsersDto> dailyReportUnsubmitted = dailyReportMapper.findUsersWithoutReport(Date.valueOf(LocalDate.now()));
		// 本日の勤怠未提出の社員リストを取得
		List<UsersDto> attendanceUnsubmitted = attendanceMapper.findUsersWithoutReport(Date.valueOf(LocalDate.now()));
		System.out.println(attendanceUnsubmitted);
		System.out.println(dailyReportUnsubmitted);
		// 前月の勤怠月次承認未申請の社員リストを取得
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		LocalDate lastDate = lastMonth.atDay(1);
		List<UsersDto> monthlyAttendanceUnsubmitted = monthlyAttendanceReqMapper
				.findUsersWithoutAttendance(Date.valueOf(lastDate));

		// 日報と勤怠の未提出者リストを統合
		List<UsersDto> unsubmittedUsers = dailyReportUnsubmitted.stream().distinct() // 重複を排除
				.collect(Collectors.toList());
		unsubmittedUsers.addAll(attendanceUnsubmitted.stream().distinct() // 重複を排除
				.collect(Collectors.toList()));
		unsubmittedUsers.addAll(monthlyAttendanceUnsubmitted.stream().distinct() // 重複を排除
				.collect(Collectors.toList()));

		// 未提出者のUserIDのリストを作成
		List<Integer> userId = unsubmittedUsers.stream().map(UsersDto::getId).distinct() // 重複を排除
				.collect(Collectors.toList());

		// 未提出者のUserIDリストを基にサブスクリプションを一括で取得
		List<PushSubscription> subscriptions = pushSubscriptionService.findByUserId(userId);

		// 通知メッセージを作成
		String message = String.format("{\"title\": \"%s\", \"body\": \"%s\", \"icon\": \"%s\"}", title, body, icon);

		// サブスクリプションを基に通知を送信
		for (PushSubscription subscription : subscriptions) {
			// サブスクリプションがアクティブな場合のみ通知を送信
			if ("active".equals(subscription.getStatus())) {
				Subscription webPushSubscription = new Subscription(subscription.getEndpoint(),
						new Subscription.Keys(subscription.getP256dh(), subscription.getAuth()));
				try {
					// 通知を送信
					sendPushNotification(webPushSubscription, message);
				} catch (Exception e) {
					// エラーをログに出力
					e.printStackTrace();
				}
			}
		}
	}

}
