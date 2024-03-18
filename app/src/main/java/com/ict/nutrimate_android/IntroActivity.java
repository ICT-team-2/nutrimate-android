package com.ict.nutrimate_android;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.ict.nutrimate_android.databinding.ActivityIntroBinding;
import com.ict.nutrimate_android.view.login.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
http://ajaxloadingimages.net/
https://www.unscreen.com/  배경제거
gif 이미지를 ImageView에 로드 하려면
1. gradle파일에 implementation 'com.github.bumptech.glide:glide:4.10.0' 추가
https://github.com/bumptech/glide
2. 다음 코드 작성
ImageView loading=findViewById(R.id.loading);
Glide.with(this).load(R.drawable.ajax_loader 혹은 원격이미지 주소).into(loading);
 */
public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Picasso.get().load(R.drawable.loading).into(binding.loading); // 로딩 프로그래스 이미지

        //2초 지연후 화면전환
        //스레드 실행을 위한 ScheduledExecutorService객체 생성
        ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
        //스레드 정의
        Runnable runnable = () ->{
            Intent intent = new Intent(IntroActivity.this, MainPageActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//매니페스트에 android:noHistory="true"설정과 같다
            startActivity(intent);
        };
        //3초후에 스레드 실행
        worker.schedule(runnable,2, TimeUnit.SECONDS);


    }
}