package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.UserNotifications;

@Mapper
public interface UserNotificationsMapper {
	
	/**
	 * お知らせ情報登録
	 * 
	 * @param userNotifications 登録するお知らせ情報
	 */
	 void insert(UserNotifications userNotifications);
	 
	// 該当ユーザーのお知らせがあるか確認
	    UserNotifications findUserNotification(Integer userId,java.util.Date date,String notificationType);

	    // お知らせ情報を更新する
	    void update(Long id);

}
