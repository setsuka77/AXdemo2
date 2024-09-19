package com.example.demo.controller;

import com.example.demo.dto.PushSubscriptionDto;
import com.example.demo.entity.PushSubscription;
import com.example.demo.entity.Users;
import com.example.demo.service.PushSubscriptionService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PushSubscriptionController {

    @Autowired
    private PushSubscriptionService pushSubscriptionService;

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody PushSubscriptionDto dto, HttpSession session) {
    	
    	// ユーザー情報の取得
    	Users loginUser = (Users) session.getAttribute("user");
    	
    	// サブスクリプション情報をログに出力して確認
        System.out.println("Received subscription data: " + dto);
    	
    	PushSubscription subscription = new PushSubscription();
    	subscription.setUserId(loginUser.getId());
        subscription.setEndpoint(dto.getEndpoint());
        subscription.setP256dh(dto.getKeys().getP256dh());
        subscription.setAuth(dto.getKeys().getAuth());
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setStatus("active");
        pushSubscriptionService.saveSubscription(subscription);
    }
}
