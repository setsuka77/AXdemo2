package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.NotificationsDto;
import com.example.demo.entity.Notifications;

@Mapper
public interface NotificationsMapper {
	
	/**
	 * お知らせ情報登録
	 * 
	 * @param notifications 登録するお知らせの情報
	 * @return 登録されたお知らせのID
	 */
	int insert (Notifications notifications);
	
	/**
	 * ユーザーIDに基づいてお知らせ情報を検索する。
	 * 
	 * @param userId ユーザーID。
	 * @return 指定されたユーザーIDに関連するお知らせのリスト
	 */
	List<NotificationsDto> findByUserId(Integer userId);
}
