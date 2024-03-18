package com.ict.nutrimate_android.view.login.join;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.databinding.LoginJoinpage6Binding;
import com.ict.nutrimate_android.view.login.LoginActivity;

public class JoinActivity6 extends AppCompatActivity {
    private LoginJoinpage6Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginJoinpage6Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 다음 버튼 클릭 이벤트 처리
        binding.joinComplete.setOnClickListener(v -> {

            // EditText에서 텍스트를 가져옵니다.
            String name = binding.idEdittext.getText().toString().trim();
            String password = binding.passwordEdittext.getText().toString().trim();
            String password2 = binding.passwordEdittext2.getText().toString().trim();

            // 필수 정보가 입력되었는지 확인합니다.
            if (name.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                // 필수 정보가 누락된 경우 사용자에게 알립니다.
                Toast.makeText(this, "모든 필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();

                // 사용자가 입력을 누락한 첫 번째 필드에 포커스를 맞춥니다.
                if (name.isEmpty()) {
                    binding.idEdittext.requestFocus();
                } else if (name.isEmpty()) {
                    binding.passwordEdittext.requestFocus();
                } else if (password2.isEmpty()) {
                    binding.passwordEdittext2.requestFocus();
                }
            } else {
                //
                if (!password.equals(password2)){
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else{
                // 모든 필수 정보가 입력되었을 경우 다음 화면으로 이동합니다.
                // 가져온 정보를 조합하여 메시지를 생성합니다.
                String message = "아이디: " + name + "\n" +
                        "비밀번호: " + password + "\n";

                // 생성된 메시지를 토스트로 표시합니다.
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                // 다음 화면으로 이동합니다.
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                }
            }
        });
    }///////onCreate

}

