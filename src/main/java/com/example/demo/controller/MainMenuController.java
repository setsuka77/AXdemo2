package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dto.NotificationsDto;
import com.example.demo.entity.Users;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.DailyReportService;
import com.example.demo.service.NotificationsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainMenuController {
	
	@Autowired
	private NotificationsService notificationsService;
	@Autowired
	private DailyReportService dailyReportService;
	@Autowired
	private AttendanceService attendanceService;
	

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
        
        // セッションからmanagerNotificationsを取得
        List<NotificationsDto> existingNotifications = (List<NotificationsDto>) session.getAttribute("managerNotifications");

        if ("manager".equals(role) && existingNotifications == null){
        	List<NotificationsDto> managerNotifications = new ArrayList<>();
        	managerNotifications.addAll(dailyReportService.checkManagerDailyReport());
        	managerNotifications.addAll(attendanceService.checkManagerAttendance());
            
            // 既存の通知リストにマネージャー用の通知を追加
        	notifications.addAll(managerNotifications);
            //セッションに追加
            session.setAttribute("managerNotifications", managerNotifications);
        }else if(existingNotifications != null) {
            // 既存の通知リストにマネージャー用の通知を追加
        	notifications.addAll(existingNotifications);
        }
        // モデルに通知を追加
        model.addAttribute("notifications", notifications);

		return "menu/index";
	}
	


}
