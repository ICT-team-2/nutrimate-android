package com.ict.nutrimate_android.view.login.join;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.databinding.LoginJoinpage1Binding;
import com.ict.nutrimate_android.view.login.LoginActivity;

public class JoinActivity1 extends AppCompatActivity {

    private LoginJoinpage1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginJoinpage1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 다음 버튼 클릭 이벤트 처리
        binding.joinBtn.setOnClickListener(v -> {

            // EditText에서 텍스트를 가져옵니다.
            String name = binding.joinName.getText().toString().trim(); // trim()을 통해 앞뒤 공백을 제거합니다.
            String birth = binding.joinBath.getText().toString().trim();
            RadioButton selectedGenderRadioButton = findViewById(binding.radioGroup.getCheckedRadioButtonId());
            String gender = selectedGenderRadioButton.getText().toString();
            String height = binding.joinTall.getText().toString().trim();
            String weight = binding.joinWeight.getText().toString().trim();

            // 필수 정보가 입력되었는지 확인합니다.
            if (name.isEmpty() || birth.isEmpty() || gender.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                // 필수 정보가 누락된 경우 사용자에게 알립니다.
                Toast.makeText(JoinActivity1.this, "모든 필수 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();

                // 사용자가 입력을 누락한 첫 번째 필드에 포커스를 맞춥니다.
                if (name.isEmpty()) {
                    binding.joinName.requestFocus();
                } else if (birth.isEmpty()) {
                    binding.joinBath.requestFocus();
                } else if (gender.isEmpty()) {
                    // 라디오 버튼에는 포커스를 맞출 수 없으므로 처리하지 않습니다.
                } else if (height.isEmpty()) {
                    binding.joinTall.requestFocus();
                } else if (weight.isEmpty()) {
                    binding.joinWeight.requestFocus();
                }
            } else {
                // 모든 필수 정보가 입력되었을 경우 다음 화면으로 이동합니다.

                // 가져온 정보를 조합하여 메시지를 생성합니다.
                String message = "이름: " + name + "\n" +
                        "생년월일: " + birth + "\n" +
                        "성별: " + gender + "\n" +
                        "키: " + height + "cm\n" +
                        "몸무게: " + weight + "kg";

                // 생성된 메시지를 토스트로 표시합니다.
                Toast.makeText(JoinActivity1.this, message, Toast.LENGTH_SHORT).show();

                // 다음 화면으로 이동합니다.
                Intent intent = new Intent(this, JoinActivity2.class);
                startActivity(intent);
            }
        });
    }///////onCreate

}
