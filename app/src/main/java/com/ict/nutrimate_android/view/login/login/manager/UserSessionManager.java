package com.ict.nutrimate_android.view.login.login.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    // SharedPreferences 파일 이름 및 키 상수 정의
    private static final String PREF_NAME = "UserSessionPrefs";
    private static final String KEY_USER_ID = "userId";

    // SharedPreferences 객체 선언
    private final SharedPreferences sharedPreferences;

    // 생성자: Context를 인수로 받아 SharedPreferences 초기화
    public UserSessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 사용자의 userId를 SharedPreferences에 저장하는 메서드
    public void saveUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply(); // 변경 사항을 즉시 적용
    }

    // SharedPreferences에서 사용자의 userId를 가져오는 메서드
    public int getUserIdFromSharedPreferences() {
        // KEY_USER_ID에 해당하는 값이 없으면 기본값으로 -1을 반환
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // 사용자 로그아웃 시 SharedPreferences에서 userId를 제거하는 메서드
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.apply();
    }
}