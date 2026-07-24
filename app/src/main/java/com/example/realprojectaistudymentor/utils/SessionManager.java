package com.example.realprojectaistudymentor.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Lưu thông tin đăng nhập
    public void saveLoginSession(int userId, String email, String name) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putInt(Constants.KEY_USER_ID, userId);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.apply();
    }

    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    // Lấy userId
    public int getUserId() {
        return prefs.getInt(Constants.KEY_USER_ID, -1);
    }

    // Lấy email
    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, "");
    }

    // Lấy tên
    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "");
    }

    // Đăng xuất — xoá toàn bộ session
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
