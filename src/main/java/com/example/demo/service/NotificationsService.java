package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDto;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.UserNotifications;
import com.example.demo.mapper.NotificationsMapper;
import com.example.demo.mapper.UserNotificationsMapper;
import com.example.demo.mapper.UsersMapper;

@Service
public class NotificationsService {
	
	@Autowired
    private NotificationsMapper notificationsMapper;
    @Autowired
    private UserNotificationsMapper userNotificationsMapper;
    @Autowired
    private UsersMapper usersMapper;
	
    public void createNotificationForUser(Integer userId, String content) {
    	System.out.println(userId);
        // notificationsテーブルに新規レコード作成
        Notifications notifications = new Notifications();
	        notifications.setContent(content);
	        notifications.setNotificationType("日報未提出通知");
	        notifications.setCreatedAt(LocalDateTime.now());
        
	        notificationsMapper.insert(notifications);
	        System.out.println("各ユーザーへ:" + notifications);
	        //ここまではきてる

        // user_notificationsテーブルに通知を紐付け
        UserNotifications userNotifications = new UserNotifications();
        //System.out.println(userId);
        //System.out.println(notifications.getId());
        	userNotifications.setUserId(userId);
        	userNotifications.setNotificationId(notifications.getId());
        	userNotifications.setIsRead(false);
        	userNotifications.setCreatedAt(LocalDateTime.now());
        	
        userNotificationsMapper.insert(userNotifications);
        System.out.println("一般通知:" + userNotifications);
    }

    
    // マネージャー向けの通知を作成
    public void createManagerNotification(String content) {
        // マネージャーユーザーを取得
        List<UsersDto> managers = usersMapper.findManagers();

        // 各マネージャーに通知を作成
        managers.forEach(manager -> {
            Notifications notifications = new Notifications();
	            notifications.setContent(content);
		        notifications.setNotificationType("日報未提出通知");
		        notifications.setCreatedAt(LocalDateTime.now());
		        
		        notificationsMapper.insert(notifications);
		        System.out.println("各ユーザーへ:" + notifications);
            
         // user_notificationsテーブルに通知を紐付け
            UserNotifications userNotifications = new UserNotifications();
	            userNotifications.setUserId(manager.getId());
	        	userNotifications.setNotificationId(notifications.getId());
	        	userNotifications.setIsRead(false);
	        	userNotifications.setCreatedAt(LocalDateTime.now());
        	
            userNotificationsMapper.insert(userNotifications);
            System.out.println("マネ通知:" + userNotifications);
        });
    }

    
}
