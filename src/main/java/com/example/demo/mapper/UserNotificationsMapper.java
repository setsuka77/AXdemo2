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

}
