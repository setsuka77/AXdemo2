package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Users;
import com.example.demo.form.DepartmentForm;
import com.example.demo.mapper.DepartmentMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class DepartmentController {
	
    @Autowired
    private DepartmentMapper departmentMapper;

	/**
	 * 部署管理画面 初期表示
	 * 
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @return ユーザ管理画面
	 */
	@GetMapping("/department/manage")
	public String userManage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");
		
	    // 全ての部署情報を取得(プルダウン用)
	    List<DepartmentDto> departments = departmentMapper.findAllWork();
		
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("departments", departments);
		return "department/manage";
	}
	
	
	/**
	 * 部署管理画面 検索非同期処理
	 * 
	 * @return 前方一致検索結果
	 */
    @GetMapping("/department/search")
    @ResponseBody
    public List<DepartmentDto> searchDepartment(@RequestParam String name) {
    	System.out.println("検索をかけている" + name);
        // 部署名で前方一致検索を行い、結果を返す
        return departmentMapper.findByNameLike(name + "%");
    }
    
	
    @PostMapping(path = "/department/manage", params = "register")
    public String registerDepartment(DepartmentForm departmentForm, RedirectAttributes redirectAttributes) {
        // 新規部署登録処理
    	departmentForm.setIsActive((byte) 1);
        departmentMapper.insert(departmentForm.toEntity());
        return "redirect:/department/manage";
    }

    @PostMapping(path = "/department/manage", params = "update")
    public String updateDepartment(DepartmentForm departmentForm, RedirectAttributes redirectAttributes) {
        // 部署名変更処理
        departmentMapper.update(departmentForm.toEntity());
        return "redirect:/department/manage";
    }

    @PostMapping(path = "/department/manage", params = "deactivate")
    public String deactivateDepartment(DepartmentForm departmentForm, RedirectAttributes redirectAttributes) {
        // 部署停止処理
        departmentForm.setIsActive((byte) 0);  // 停止状態に変更
        departmentMapper.update(departmentForm.toEntity());
        return "redirect:/department/manage";
    }

	
	/**
	 * 「メニュー」ボタン押下
	 * 
	 * @param redirectAttributes
	 * @return 処理メニュー画面
	 */
	@PostMapping(path = "/department/manage", params = "back")
	public String backMenu(RedirectAttributes redirectAttributes) {

		return "redirect:/index";
	}
	
	
}
