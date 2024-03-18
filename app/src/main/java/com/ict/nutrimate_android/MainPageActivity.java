package com.ict.nutrimate_android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.databinding.MainPageBinding;
import com.ict.nutrimate_android.view.login.LoginActivity;
import com.ict.nutrimate_android.view.login.join.JoinActivity1;

// 처음 앱 실행시 보여줄 메인화면 (로그인 전)
public class MainPageActivity extends AppCompatActivity {

    private MainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setDarkBackground(binding.mainImage);

        // 로그인 버튼 클릭 이벤트 처리
        binding.loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        // 회원가입 버튼 클릭 이벤트 처리
        binding.joinBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, JoinActivity1.class);
            startActivity(intent);
        });

    }///////////////onCreate

    public static void setDarkBackground(LinearLayout linearLayout) {
        Drawable background = linearLayout.getBackground(); // 현재 배경 이미지 가져오기
        background.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#66000000"), PorterDuff.Mode.SRC_OVER)); // 어둡게 만들기 위해 컬러 필터 적용
        linearLayout.setBackground(background); // 어둡게 만든 배경으로 설정
    }

}
