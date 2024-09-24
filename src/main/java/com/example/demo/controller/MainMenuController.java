package com.example.demo.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dto.NotificationsDto;
import com.example.demo.entity.Users;
import com.example.demo.service.NotificationsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainMenuController {
	
	@Autowired
	private NotificationsService notificationsService;
	
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
		
		//ログインユーザの権限判定
		String role = loginUser.getRole();
		if ("1".equals(role)) {
			role = "admin";
		} else if ("2".equals(role)) {
			role = "manager";
		} else if ("3".equals(role) || "4".equals(role)) {
			role = "regular";
		}
		
		model.addAttribute("loginUser", loginUser);
		// ロールに基づいてModelにメニュー情報を追加
        model.addAttribute("role", role);
        
        // ユーザーIDに基づいてお知らせを取得
        Integer userId = loginUser.getId();
        List<NotificationsDto> notifications = notificationsService.getUserNotifications(userId);
        
        //マネ権限時のお知らせを取得
        if ("manager".equals(role)){
        	// 5分経過した通知の削除処理
            clearOldNotifications(session);
        	// セッションからmanagerNotificationsを取得
            List<NotificationsDto> existingNotifications = (List<NotificationsDto>) session.getAttribute("managerNotifications");       
            System.out.println("マネ権限:"+ existingNotifications);
        	if(existingNotifications != null) {
        	// 既存の通知リストにマネージャー用の通知を追加
        	notifications.addAll(existingNotifications);
        	}
        }
        // モデルに通知を追加
        model.addAttribute("notifications", notifications);
        model.addAttribute("publicVapidKey", publicVapidKey);  // VAPID公開キーをモデルに追加

		return "menu/index";
	}
	
	
}
