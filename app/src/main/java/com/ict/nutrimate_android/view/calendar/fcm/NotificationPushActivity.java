package com.ict.nutrimate_android.view.calendar.fcm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.RecordNotificationPushBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.calendar.fcm.item.PushItem;
import com.ict.nutrimate_android.view.login.LoginActivity;
import com.ict.nutrimate_android.view.login.login.LoginItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationPushActivity extends AppCompatActivity {

    private RecordNotificationPushBinding binding;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecordNotificationPushBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(this);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        // 현재 날짜/시간 설정
        Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M월 d일(E) a h:mm", Locale.KOREA);
        String formattedTime = dateFormat.format(currentTime);
        binding.fcmPushDateTime.setText(formattedTime);

        // 알림 전송 버튼 클릭 이벤트 처리
        binding.fcmPushBtn.setOnClickListener(v -> {
            String title = binding.fcmPushTitle.getText().toString();
            String body = binding.fcmPushBody.getText().toString();

            SpringService service = RetrofitClient.getSpringService();
            Call<PushItem> call = service.pushToPhone(title,body);

            new AlertDialog.Builder(v.getContext(), R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle("알람을 전송하시겠습니까?")
                    .setPositiveButton("예", (dialog, which)->{
                        call.enqueue(new Callback<PushItem>() {
                            @Override
                            public void onResponse(Call<PushItem> call, Response<PushItem> response) {
                                if (response.isSuccessful()) {
                                    Log.i("tag","FCM 알람 전송 완료");
                                }
                            }
                            @Override
                            public void onFailure(Call<PushItem> call, Throwable t) {}
                        });
                    })
                    .setNegativeButton("아니오",null)
                    .show();
        });
        binding.fcmPushTitle.setText("");
        binding.fcmPushBody.setText("");
    }

}