package com.example.demo.mapper;

import com.example.demo.entity.PushSubscription;

import java.util.List;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PushSubscriptionMapper {

	/**
	 * 指定されたユーザーIDリストに基づいてサブスクリプションを取得
	 *
	 * @param userId ユーザーIDのリスト
	 * @return 対応するサブスクリプションのリスト
	 */
	List<PushSubscription> findByUserId(@Param("list") List<Integer> userId);

	/**
	 * プッシュ通知のサブスクリプションをデータベースに保存
	 *
	 * @param subscription 保存するサブスクリプションのデータ
	 */
	void insertSubscription(PushSubscription subscription);

	/**
	 * 指定されたユーザーIDに対応するサブスクリプションを削除
	 *
	 * @param userId 削除するユーザーのID
	 */
	void deleteByUserId(@Param("userId") Integer userId);

	/**
	 * すべてのサブスクリプションを取得
	 *
	 * @return すべてのサブスクリプションのリスト
	 */
	List<PushSubscription> findAll();
}
