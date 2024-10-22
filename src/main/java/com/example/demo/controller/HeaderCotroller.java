package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class HeaderCotroller {
	
	 // すべてのリクエストでModelにuserNameを追加
    @ModelAttribute
    public void addAttributes(Model model, HttpSession session) {
    	// loginUser を session から取得する例
    	Users loginUser = (Users) session.getAttribute("user"); 
        model.addAttribute("loginUser", loginUser);
    }
    
    //デザイン設定画面に遷移する
    @GetMapping("/common/design")
    public String showDesignPage() {
    	
        return "common/design"; // design.htmlというThymeleafテンプレートに遷移
    }

}
