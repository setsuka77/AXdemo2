package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.Notifications;

@Mapper
public interface NotificationsMapper {
	
	/**
	 * お知らせ情報登録
	 * 
	 * @param お知らせID
	 */
	int insert (Notifications notifications);
}
