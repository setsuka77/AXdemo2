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

    /**
     * テストメール用
     * http://localhost:8080/login/sendMail?to=受信するメアド
     * 上記URLを直打ちで送信
     * @param to
     * @return メール送信可否表示
     */
    @GetMapping("/sendMail")
    public String sendMail(
        @RequestParam("to") String to) {
        
        try {
            sendMailService.sendSimpleMail(to);
            return "メールが正常に送信されました。";
        } catch (Exception e) {
            e.printStackTrace();
            return "メールの送信に失敗しました。";
        }
    }
}
