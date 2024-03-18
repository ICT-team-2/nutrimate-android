package com.ict.nutrimate_android.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.MainActivity;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.login.join.JoinActivity1;
import com.ict.nutrimate_android.databinding.LoginLoginpageBinding;
import com.ict.nutrimate_android.view.login.login.LoginItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.test.map.GoogleMapActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginLoginpageBinding binding;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginLoginpageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // UserSessionManager 인스턴스 생성
        userSessionManager = new UserSessionManager(getApplicationContext());

        // 테스트 용
        binding.testBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, GoogleMapActivity.class);
            startActivity(intent);
        });
        binding.kakaoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });


        // 로그인 버튼 클릭 이벤트 처리
        binding.idLoginBotton.setOnClickListener(v -> {
            // 사용자가 입력한 아이디와 비밀번호 가져오기
            String userUid = binding.idEdittext.getText().toString();
            String userPwd = binding.passwordEdittext.getText().toString();
            Map<String, String> loginData = new HashMap<>();
            loginData.put("userUid", userUid);
            loginData.put("userPwd", userPwd);

            // Retrofit을 사용하여 서버에 로그인 요청 보내기
            SpringService service = RetrofitClient.getSpringService();
            Call<LoginItem> call = service.login(loginData);

            call.enqueue(new Callback<LoginItem>() {
                @Override
                public void onResponse(Call<LoginItem> call, Response<LoginItem> response) {
                    if (response.isSuccessful()) {
                        // 로그인 성공
                        LoginItem loginItem = response.body();
                        // userId를 추출하여 SharedPreferences에 저장
                        int userId = Integer.parseInt(loginItem.getUserId());
                        userSessionManager.saveUserId(userId);
                        // 다음 화면으로 이동 등의 처리
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // 로그인 실패 (서버에서 오류 응답을 받은 경우)
                        Log.e("TAG", "로그인 실패: " + response.message());
                        Toast.makeText(LoginActivity.this, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<LoginItem> call, Throwable t) {
                    // 통신 실패
                    Log.e("TAG", "통신 실패: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "통신에 실패했습니다. 인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 회원가입 창으로 이동
        binding.joinBotton.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, JoinActivity1.class);
            Intent intent = new Intent(LoginActivity.this, JoinActivity1.class);
            startActivity(intent);
        });
    }//////////////////////////////////onCreate

}
