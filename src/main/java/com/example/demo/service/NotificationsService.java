package com.example.demo.service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
	
	/**
	 * 前日が土日かチェック
	 * 
	 * @param date チェックする日付
	 * @return 土日であればtrue、そうでなければfalse
	 */
	boolean isWeekend(LocalDate date) {
	    // 土曜日または日曜日かどうか
	    if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
	        return false;
	    }
	    return true;
	}

	/**
	 * 未提出通知 作成(Role=3と4用)
	 * 
	 * @param users 前日提出していないユーザーのリスト
	 * @param previousDay 前日の日付
	 */
	public void createNotificationForUsers(List<UsersDto> users, LocalDate previousDay,
			String notificationType,String content,Date targetDate) {
        Long notificationId = createNotification(content,notificationType,targetDate);

		users.forEach(user -> linkNotificationToUser(user.getId(), notificationId,notificationType));
		System.out.println("社員通知");
	}
	
	/**
     * マネージャーに通知　作成
     * 
     * @param content 通知の内容
     */
    public void createManagerNotification(String content,String notificationType,Date targetDate) {
        List<UsersDto> managers = usersMapper.findManagers();
        Long notificationId = createNotification(content,notificationType,targetDate);
        

        managers.forEach(manager -> linkNotificationToUser(manager.getId(), notificationId,notificationType));
        System.out.println("マネ通知");
    }

	/**
	* 新しい通知 作成
	* 
	* @param content 通知の内容
	* @return 作成された通知のID
	*/
	public Long createNotification(String content,String notificationType,Date targetDate) {
		Notifications notifications = new Notifications();
		notifications.setContent(content);
		notifications.setNotificationType(notificationType);
		notifications.setCreatedAt(LocalDateTime.now());
		notifications.setTargetDate(targetDate);

		notificationsMapper.insert(notifications);
		
		return notifications.getId();
	}

	/**
	 * ユーザーと通知を紐付ける
	 * 
	 * @param userId 紐付けるユーザーのID
	 * @param notificationId 紐付ける通知のID
	 */
	public void linkNotificationToUser(Integer userId, Long notificationId,String notificationType) {
		UserNotifications userNotifications = new UserNotifications();
		userNotifications.setUserId(userId);
		userNotifications.setNotificationId(notificationId);
		userNotifications.setIsRead(false);
		userNotifications.setIsVisible(true);

		userNotificationsMapper.insert(userNotifications);
	}
	
	/*
	 * 既存の通知がある場合はフラグを更新する
	 */
	public void checkNotifications(Integer userId, java.util.Date date,String notificationType) {
		UserNotifications existingNotification = userNotificationsMapper.findUserNotification(userId,date,notificationType);
		// 既存の通知がある場合はフラグを更新
	    if (existingNotification != null) {
	    	 userNotificationsMapper.update(existingNotification.getId());
	    }
		
	}
	
}
