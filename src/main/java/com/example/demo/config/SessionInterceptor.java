package com.example.demo.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * セッション管理のためのインターセプタークラス
 * ユーザーのセッションが有効かどうかを確認し、無効な場合はログイン画面にリダイレクトする
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    /**
     * コントローラーの処理前に実行される
     * ユーザーのセッションが有効であるかを確認し、無効な場合はログイン画面にリダイレクトする
     *
     * @param request  クライアントからのリクエスト
     * @param response サーバーからのレスポンス
     * @param handler  呼び出されるハンドラー (コントローラー)
     * @return セッションが有効であればtrue、それ以外の場合はfalse
     * @throws Exception 例外が発生した場合
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // セッションを取得、存在しない場合はnull
        HttpSession session = request.getSession(false);
        if (session != null) {
            Users user = (Users) session.getAttribute("user");
            // セッションにユーザーが存在しない場合、エラーメッセージをセットしてログイン画面にリダイレクト
            if (user == null) {
                session.setAttribute("error", "セッションが切れました。再度ログインしてください。");
                response.sendRedirect("/login");
                return false; // リクエストを処理しない
            }
        } else {
            // セッションが存在しない場合、ログイン画面にリダイレクト
            response.sendRedirect("/login");
            return false; // リクエストを処理しない
        }
        return true; // リクエストを続行
    }
}
