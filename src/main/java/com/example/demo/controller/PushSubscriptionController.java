package com.example.demo.controller;

import com.example.demo.entity.PushSubscription;
import com.example.demo.service.PushSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PushSubscriptionController {

    @Autowired
    private PushSubscriptionService pushSubscriptionService;

    @GetMapping("/subscribe")
    public void subscribe(@RequestBody PushSubscription subscription) {
        pushSubscriptionService.saveSubscription(subscription);
    }
}
