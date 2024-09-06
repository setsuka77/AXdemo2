package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Department;
import com.example.demo.entity.Users;
import com.example.demo.form.DepartmentForm;
import com.example.demo.mapper.DepartmentMapper;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
	 * @return 部署管理画面
	 */
	@GetMapping("/department/manage")
	public String userManage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		// ユーザー情報の取得
		Users loginUser = (Users) session.getAttribute("user");

		// 全ての部署情報を取得(プルダウン用)
		List<DepartmentDto> departments = departmentMapper.findAll();

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
		return departmentMapper.findByNameLike(name + "%");
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
			List<DepartmentDto> departments = departmentMapper.findAll();
			model.addAttribute("departments", departments);
			return "department/manage";
		}

		// 入力された部署名が既に登録されているかチェック
		Department existingDepartment = departmentMapper.findByName(departmentForm.getNewDepartment());

		if (existingDepartment != null) {
			// 部署名が既に存在する場合、エラーメッセージをモデルに追加して返す
			model.addAttribute("errorMessage", "この部署名は既に登録されています。新たな部署名を入力してください。");
			// 全ての部署情報を取得(プルダウン用)
			List<DepartmentDto> departments = departmentMapper.findAll();
			model.addAttribute("departments", departments);
			return "department/manage";
		}

		// 新規部署登録処理
		departmentMapper.insert(departmentForm.toEntity());
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
		String newDepartment = departmentForm.getNewDepartment();
		String currentDepartment = departmentForm.getCurrentDepartment();

		// エラーチェック
		if (result.hasErrors()) {
			// エラーメッセージをビューに渡す
			model.addAttribute("errorMessage", result.getFieldError("newDepartment").getDefaultMessage());
			// 全ての部署情報を取得(プルダウン用)
			List<DepartmentDto> departments = departmentMapper.findAll();
			model.addAttribute("departments", departments);
			return "department/manage";
		}

		if (currentDepartment.equals(newDepartment)) {
			// 部署名が既に存在する場合、エラーメッセージをモデルに追加して返す
			model.addAttribute("errorMessage", "この部署名は既に登録されています。新たな部署名を入力してください。");
			// 全ての部署情報を取得(プルダウン用)
			List<DepartmentDto> departments = departmentMapper.findAll();
			model.addAttribute("departments", departments);
			return "department/manage";
		}

		// 部署名変更更新処理
		// 入力された新部署名が既に登録されているかチェック
		Department searchDepartment = departmentMapper.findByName(currentDepartment);
		System.out.println("探してきたやつ:" + searchDepartment);
		Department department = new Department();
		department.setDepartmentId(searchDepartment.getDepartmentId());
		department.setName(newDepartment);
		department.setIsActive((byte) 1);
		System.out.println(department);

		departmentMapper.update(department);
		redirectAttributes.addFlashAttribute("successMessage", currentDepartment + "を" + newDepartment + "に更新しました。");

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
		Department searchDepartment = departmentMapper.findByName(currentDepartment);
		System.out.println(searchDepartment);

		Department department = new Department();
		department.setDepartmentId(searchDepartment.getDepartmentId());
		department.setName(currentDepartment + " [停止中]");
		department.setIsActive((byte) 0);
		System.out.println(department);
		// 停止状態に変更
		departmentMapper.update(department);
		System.out.println("停止できた");
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
    	Department searchDepartment = departmentMapper.findByName(currentDepartment);
    	System.out.println(searchDepartment);
    	
    	Department department = new Department();
    	department.setDepartmentId(searchDepartment.getDepartmentId());
    	// 部署名の「 [停止中]」を取り除く
        String newName = currentDepartment.replace(" [停止中]", "");      
    	department.setName(newName);
    	department.setIsActive((byte) 1);
        System.out.println(department);
        
        // 再開状態に変更
		departmentMapper.update(department);
        System.out.println("再開できた");
        redirectAttributes.addFlashAttribute("successMessage", newName + "を再開しました。");
        
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
