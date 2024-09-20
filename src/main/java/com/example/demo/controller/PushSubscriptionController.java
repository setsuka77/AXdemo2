package com.example.demo.controller;

import com.example.demo.dto.PushSubscriptionDto;
import com.example.demo.entity.PushSubscription;
import com.example.demo.entity.Users;
import com.example.demo.service.PushSubscriptionService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * プッシュ通知のサブスクリプションに関するAPIエンドポイントを提供 ユーザーからのサブスクリプションデータを受け取って保存
 */
@RestController
public class PushSubscriptionController {

	@Autowired
	private PushSubscriptionService pushSubscriptionService;

	/**
	 * ユーザーから受け取ったプッシュ通知のサブスクリプション情報を登録
	 * 
	 * @param dto     クライアントから送信されたサブスクリプション情報（JSON形式）
	 * @param session 現在のセッション情報からユーザー情報を取得するために使用
	 */
	@PostMapping("/subscribe")
	public void subscribe(@RequestBody PushSubscriptionDto dto, HttpSession session) {

		// ログイン中のユーザー情報をセッションから取得
		Users loginUser = (Users) session.getAttribute("user");

		// サブスクリプション情報をログに出力して確認
		System.out.println("Received subscription data: " + dto);

		// PushSubscriptionエンティティを作成し、受け取ったデータを設定
		PushSubscription subscription = new PushSubscription();
		subscription.setUserId(loginUser.getId());
		subscription.setEndpoint(dto.getEndpoint());
		subscription.setP256dh(dto.getKeys().getP256dh());
		subscription.setAuth(dto.getKeys().getAuth());
		subscription.setCreatedAt(LocalDateTime.now());
		subscription.setStatus("active");

		// サブスクリプションをDBに保存
		pushSubscriptionService.saveSubscription(subscription);
	}
}
