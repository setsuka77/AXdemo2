package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
	    // フィールド順を定義 (ログインフォームの順番)
	    List<String> fieldOrder = Arrays.asList("id", "password");

	    // フィールドごとのエラーメッセージを保持するMap
	    Map<String, List<String>> errors = new LinkedHashMap<>();

	    // 追加済みのエラーメッセージを保持するセット
	    Set<String> addedMessages = new HashSet<>();

	    // フィールド順に従ってエラーメッセージをソート
	    bindingResult.getFieldErrors().stream()
	            .sorted(Comparator.comparing(error -> fieldOrder.indexOf(error.getField())))
	            .forEach(error -> {
	                String message = error.getDefaultMessage();

	                // すべてのフィールドで重複したエラーメッセージを1つのみ表示
	                if (!addedMessages.contains(message)) {
	                    errors.computeIfAbsent(error.getField(), k -> new ArrayList<>()).add(message);
	                    addedMessages.add(message);
	                }
	            });

	    // エラーメッセージを HTML に合成
	    if (!errors.isEmpty()) {
	        StringBuilder errorMessage = new StringBuilder("ログインに失敗しました。<br>");
	        errors.forEach((field, messages) -> {
	            messages.forEach(message -> errorMessage.append(message).append("<br>"));
	        });
	        return errorMessage.toString();
	    }
	    return "";
	}
}
