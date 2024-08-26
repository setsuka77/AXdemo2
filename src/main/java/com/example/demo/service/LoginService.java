package com.example.demo.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.example.demo.entity.Users;
import com.example.demo.mapper.UsersMapper;

@Service
public class LoginService {

	@Autowired
	private UsersMapper usersMapper;

	/**
	 * ログイン画面 ユーザ検索、パスワード判定ログイン機能
	 * 
	 * @param id ユーザーID
	 * @param password パスワード
	 * @return ログインに成功したユーザー情報、失敗した場合はnull
	 */
	public Users login(Integer id, String password) {
		Users user = usersMapper.findByUserId(id);
		if (user != null && user.getPassword().equals(password)) {
			return user;
		}
		return null;
	}
	
	/**
	 * バインディング結果からエラーメッセージをフォーマット
	 * 
	 * @param bindingResult
	 * @return フォーマットされたエラーメッセージ
	 */
	public static String formatErrors(BindingResult bindingResult) {
	    // エラーメッセージを保持するセット
	    Set<String> errorMessages = new LinkedHashSet<>();

	    // エラーメッセージをセットに追加
	    bindingResult.getFieldErrors().stream()
	            .map(error -> error.getDefaultMessage())
	            .distinct()  // 重複を排除
	            .forEach(errorMessages::add);

	    // エラーメッセージを HTML に合成
	    if (!errorMessages.isEmpty()) {
	        StringBuilder errorMessage = new StringBuilder();
	        errorMessages.forEach(message -> errorMessage.append(message).append("<br>"));
	        return errorMessage.toString();
	    }
	    return "";
	}
}
