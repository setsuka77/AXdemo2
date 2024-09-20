package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PushSubscription;
import com.example.demo.mapper.PushSubscriptionMapper;

/**
 * プッシュ通知のサブスクリプションに関する操作を提供
 * サブスクリプションの保存、削除、ユーザーIDに基づくサブスクリプションの取得など
 * 
 */
@Service
public class PushSubscriptionService {

	@Autowired
	private PushSubscriptionMapper pushSubscriptionMapper;

	/**
     * 指定されたユーザーIDに基づいて、対応するプッシュ通知のサブスクリプションを取得
     * @param userId ユーザーIDのリスト
     * @return 指定されたユーザーIDに対応するサブスクリプションのリスト
     */
	public List<PushSubscription> findByUserId(List<Integer> userId) {
		return pushSubscriptionMapper.findByUserId(userId);
	}

	/**
     * 新しいプッシュ通知のサブスクリプションを保存
     *
     * @param subscription 保存するプッシュ通知のサブスクリプション
     */
	public void saveSubscription(PushSubscription subscription) {
		pushSubscriptionMapper.insertSubscription(subscription);
	}

	/**
     * 指定されたIDに対応するプッシュ通知のサブスクリプションを削除
     *
     * @param id 削除するサブスクリプションのユーザーID
     */
	public void deleteSubscription(Integer id) {
		pushSubscriptionMapper.deleteByUserId(id);
	}

}
