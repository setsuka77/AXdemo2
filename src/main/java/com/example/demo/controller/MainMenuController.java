package com.example.demo.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.NotificationsDto;
import com.example.demo.entity.Users;
import com.example.demo.service.MainMenuService;
import com.example.demo.service.NotificationsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainMenuController {

	@Autowired
	private NotificationsService notificationsService;
	@Autowired
	private MainMenuService mainMenuService;

	@Value("${push.vapid.public-key}")
	private String publicVapidKey;

	/**
	 * 通知をクリアするメソッド
	 * @param session
	 */
	private void clearOldNotifications(HttpSession session) {
		Instant timestamp = (Instant) session.getAttribute("notificationTimestamp");
		if (timestamp != null) {
			Instant now = Instant.now();

			// 5分経過したかどうか確認
			if (now.isAfter(timestamp.plusSeconds(300))) {
				session.removeAttribute("managerNotifications");
				session.removeAttribute("notificationTimestamp");
			}
		}
	}

	/**
	 * 処理メニュー画面 初期表示
	 *
	 * @param model
	 * @param session
	 * @return 処理メニュー画面
	 */
	@GetMapping("/index")
	public String userManage(HttpSession session, Model model) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		//権限の判定
		String role = mainMenuService.checkRole(loginUser);

		model.addAttribute("loginUser", loginUser);
		// ロールに基づいてModelにメニュー情報を追加
		model.addAttribute("role", role);

		// ユーザーIDに基づいてお知らせを取得
		Integer userId = loginUser.getId();
		List<NotificationsDto> notifications = notificationsService.getUserNotifications(userId);

		//マネ権限時のお知らせを取得
		if ("manager".equals(role)) {
			// 5分経過した通知の削除処理
			clearOldNotifications(session);
			// セッションからmanagerNotificationsを取得
			List<NotificationsDto> existingNotifications = (List<NotificationsDto>) session
					.getAttribute("managerNotifications");
			if (existingNotifications != null) {
				// 既存の通知リストにマネージャー用の通知を追加
				notifications.addAll(existingNotifications);
			}
		}

		//出勤済みだった場合のステータス情報取得
		Integer status = mainMenuService.checkStatus(loginUser);
		model.addAttribute("status", status);

		// モデルに通知を追加
		model.addAttribute("notifications", notifications);
		model.addAttribute("publicVapidKey", publicVapidKey); // VAPID公開キーをモデルに追加

		return "menu/index";
	}

	/**
	 * 日報管理ボタンを押下時の処理
	 */
	@GetMapping("/menu/report")
	public String chooseReport(HttpSession session, Model model) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		//権限の判定
		String role = mainMenuService.checkRole(loginUser);

		model.addAttribute("loginUser", loginUser);
		model.addAttribute("role", role);
		return "menu/report";
	}

	/**
	 * 「出勤」ボタン押下時の処理
	 *
	 * @param session セッション情報
	 * @param status 出勤状態
	 * @param model モデル
	 * @return 処理メニュー画面
	 */
	@PostMapping(path = "/menu/index", params = "start")
	public String attendanceStart(HttpSession session, Integer status, Model model) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		//権限の判定
		String role = mainMenuService.checkRole(loginUser);

		model.addAttribute("loginUser", loginUser);
		model.addAttribute("role", role);

		// ユーザーIDに基づいてお知らせを取得
		Integer userId = loginUser.getId();
		List<NotificationsDto> notifications = notificationsService.getUserNotifications(userId);

		// モデルに通知を追加
		model.addAttribute("notifications", notifications);
		model.addAttribute("publicVapidKey", publicVapidKey); // VAPID公開キーをモデルに追加

		//ステータスのnullチェック
		if (status == null) {
			String message = "勤務状況を選択してください";
			model.addAttribute("message", message);
			return "menu/index";
		}

		//出勤登録
		String message = mainMenuService.registStartAttendance(loginUser, status);
		model.addAttribute("message", message);
		model.addAttribute("status", status);
		return "menu/index";
	}

	/**
	 * 「退勤」ボタン押下時の処理
	 *
	 * @param session セッション情報
	 * @param model モデル
	 * @param status 出勤状態
	 * @return 処理メニュー画面
	 */
	@PostMapping(path = "/menu/index", params = "end")
	public String attendanceEnd(HttpSession session, Model model, Integer status) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		//権限の判定
		String role = mainMenuService.checkRole(loginUser);

		model.addAttribute("loginUser", loginUser);
		model.addAttribute("role", role);

		// ユーザーIDに基づいてお知らせを取得
		Integer userId = loginUser.getId();
		List<NotificationsDto> notifications = notificationsService.getUserNotifications(userId);
		// モデルに通知を追加
		model.addAttribute("notifications", notifications);
		model.addAttribute("publicVapidKey", publicVapidKey); // VAPID公開キーをモデルに追加

		//退勤登録
		String message = mainMenuService.registEndAttendance(loginUser);
		model.addAttribute("message", message);
		model.addAttribute("status", status);
		return "menu/index";
	}

}
