package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PushSubscription;
import com.example.demo.mapper.PushSubscriptionMapper;

@Service
public class PushSubscriptionService {

	@Autowired
	private PushSubscriptionMapper pushSubscriptionMapper;

	public List<PushSubscription> findByUserId(List<Integer> userId) {
		return pushSubscriptionMapper.findByUserId(userId);
	}

	public void saveSubscription(PushSubscription subscription) {

		// サブスクリプションを保存
		pushSubscriptionMapper.insertSubscription(subscription);
	}

	public void deleteSubscription(Integer id) {
		// サブスクリプションを削除
		pushSubscriptionMapper.deleteByUserId(id);
	}

}
