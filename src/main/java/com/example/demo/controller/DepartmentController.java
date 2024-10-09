package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.dto.UserManagementDto;
import com.example.demo.entity.Users;
import com.example.demo.form.DepartmentForm;
import com.example.demo.service.DepartmentService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 部署管理画面 初期表示
     * 
     * @param session
     * @param model
     * @param redirectAttributes
     * @return 部署管理画面
     */
    @GetMapping("/department/manage")
    public String userManage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // ユーザー情報の取得
        Users loginUser = (Users) session.getAttribute("user");

        // 全ての部署情報を取得(プルダウン用)
        List<DepartmentDto> departments = departmentService.findAllDepartments();

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
        System.out.println("検索をかけている=" + name);
        // 部署名で前方一致検索を行い、結果を返す
        return departmentService.searchDepartmentsByName(name);
    }
    
    /**
     * 所属社員一覧取得
     * 
     * @param 部署名
     */
    @ResponseBody
	@RequestMapping(value = "/department/worker", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, Object>> searchWorker(@RequestParam String selectedDepartment) {
        System.out.println( selectedDepartment);
        // 部署名で社員検索を行い、所属社員リストを返す
        List<UserManagementDto> users = departmentService.searchDepartmentWorker(selectedDepartment);
        
        Map<String, Object> response = new HashMap<>();
		response.put("users", users);
        
        return ResponseEntity.ok(response);
    }
    

    /**
     * 部署管理画面 新規部署登録処理
     * 
     * @param departmentForm
     * @param redirectAttributes
     * @param model
     * @return
     */
    @PostMapping(path = "/department/manage", params = "register")
    public String registerDepartment(@Valid DepartmentForm departmentForm, BindingResult result,
            RedirectAttributes redirectAttributes, Model model) {

        // 文字数チェック
        if (result.hasErrors()) {
            // エラーメッセージをビューに渡す
            model.addAttribute("errorMessage", result.getFieldError("newDepartment").getDefaultMessage());
            // 全ての部署情報を取得(プルダウン用)
            List<DepartmentDto> departments = departmentService.findAllDepartments();
            model.addAttribute("departments", departments);
            return "department/manage";
        }

        // 新規部署登録処理
        String errorMessage = departmentService.registerDepartment(departmentForm);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            List<DepartmentDto> departments = departmentService.findAllDepartments();
            model.addAttribute("departments", departments);
            return "department/manage";
        }

        redirectAttributes.addFlashAttribute("successMessage", departmentForm.getNewDepartment() + "を登録しました。");
        return "redirect:/department/manage";
    }

    /**
     * 部署管理画面 既存部署名更新処理
     * 
     * @param departmentForm
     * @param redirectAttributes
     * @param model
     * @return
     */
    @PostMapping(path = "/department/manage", params = "update")
    public String updateDepartment(@Valid @ModelAttribute DepartmentForm departmentForm, BindingResult result,
            RedirectAttributes redirectAttributes, Model model) {
        // エラーチェック
        if (result.hasErrors()) {
            // エラーメッセージをビューに渡す
            model.addAttribute("errorMessage", result.getFieldError("newDepartment").getDefaultMessage());
            // 全ての部署情報を取得(プルダウン用)
            List<DepartmentDto> departments = departmentService.findAllDepartments();
            model.addAttribute("departments", departments);
            return "department/manage";
        }

        // 部署名変更更新処理
        String errorMessage = departmentService.updateDepartment(departmentForm);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            List<DepartmentDto> departments = departmentService.findAllDepartments();
            model.addAttribute("departments", departments);
            return "department/manage";
        }

        redirectAttributes.addFlashAttribute("successMessage", departmentForm.getCurrentDepartment() + "を" + departmentForm.getNewDepartment() + "に更新しました。");
        return "redirect:/department/manage";
    }

    /**
     * 部署管理画面 既存部署停止 論理削除処理
     * 
     * @param currentDepartment
     * @param departmentForm
     * @param redirectAttributes
     * @return
     */
    @PostMapping(path = "/department/manage", params = "deactivate")
    public String deactivateDepartment(@RequestParam("currentDepartment") String currentDepartment,
            RedirectAttributes redirectAttributes) {
        // 停止状態に変更
        departmentService.deactivateDepartment(currentDepartment);
        redirectAttributes.addFlashAttribute("successMessage", currentDepartment + "を停止しました。");
        return "redirect:/department/manage";
    }
    
    /**
     * 部署管理画面 停止部署再開 更新処理
     *
     * @param currentDepartment
     * @param departmentForm
     * @param redirectAttributes
     * @return
     */
    @PostMapping(path = "/department/manage", params = "restart")
    public String restartDepartment(@RequestParam("currentDepartment") String currentDepartment, RedirectAttributes redirectAttributes) {
    	// 再開状態に変更し、更新後の部署名を取得
    	String newName = departmentService.restartDepartment(currentDepartment);
        if (newName != null) {
        redirectAttributes.addFlashAttribute("successMessage", newName + "を再開しました。");
        }
        return "redirect:/department/manage";
    }
    
    

}
