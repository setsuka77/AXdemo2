package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.UserNotificationsDto;
import com.example.demo.entity.UserNotifications;

@Mapper
public interface UserNotificationsMapper {
	
	/**
	 * お知らせ情報登録
	 * 
	 * @param userNotifications 登録するお知らせ情報
	 */
	 void insert(UserNotifications userNotifications);
	 
	 /**
	  *	該当ユーザーのお知らせがあるか確認
	  * 
	  * @param userId ユーザーのID。
	  * @param date 対象の日付。
	  * @param notificationType 通知のタイプ。
	  * @return 該当ユーザーの通知情報のリスト
	  */
	   List<UserNotificationsDto> findUserNotification(Integer userId,java.util.Date date,String notificationType);

	 /**
	  * お知らせ情報を更新する。
	  * 
	  * @param id 更新する通知のID。
	  */
	   void update(Long id);

}
