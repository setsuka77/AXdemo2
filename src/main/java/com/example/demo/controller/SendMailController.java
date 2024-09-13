package com.example.demo.controller;

import com.example.demo.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMailController {

    @Autowired
    private SendMailService sendMailService;

    @GetMapping("/sendMail")
    public String sendMail(
        @RequestParam("to") String to,
        @RequestParam("subject") String subject,
        @RequestParam("text") String text) {
        
        try {
            sendMailService.sendSimpleMail(to, subject, text);
            return "メールが正常に送信されました。";
        } catch (Exception e) {
            e.printStackTrace();
            return "メールの送信に失敗しました。";
        }
    }
}
