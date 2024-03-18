package com.ict.nutrimate_android.view.calendar.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.MainActivity;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.RecordNotificationMessageBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.login.LoginActivity;
import com.ict.nutrimate_android.view.mypage.profile.MyPageProfileItem;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private RecordNotificationMessageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecordNotificationMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M월 d일(E) a h:mm", Locale.KOREA);
        String formattedTime = dateFormat.format(currentTime);
        binding.fcmPullDateTime.setText(formattedTime);

        Intent intent = getIntent();
        int pushUserId = intent.getIntExtra("userId",-1);
        if (pushUserId!=-1){
            binding.fcmPullTitle.setText(intent.getStringExtra("title"));
            binding.fcmPullBody.setText(intent.getStringExtra("body"));
            SpringService service = RetrofitClient.getSpringService();
            Call<MyPageProfileItem> call = service.mypageprofile(pushUserId);
            call.enqueue(new Callback<MyPageProfileItem>() {
                @Override
                public void onResponse(Call<MyPageProfileItem> call, Response<MyPageProfileItem> response) {
                    if (response.isSuccessful()){
                        MyPageProfileItem items = response.body();
                        binding.fcmPullNickname.setText(items.getUserNick());
                        binding.fcmPullIntro.setText(items.getUserIntro());
                        Picasso.get().load(RetrofitClient.NUTRI_SPRING + items.getUserProfile())
                                .placeholder(R.drawable.ic_mypage_profile) // 기본 이미지 지정
                                .into(binding.fcmPullProfile);
                    }
                }
                @Override
                public void onFailure(Call<MyPageProfileItem> call, Throwable t) {}
            });
        }
        binding.fcmPullBtn.setOnClickListener(v -> {
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        });
    }/////////////onCreate
}