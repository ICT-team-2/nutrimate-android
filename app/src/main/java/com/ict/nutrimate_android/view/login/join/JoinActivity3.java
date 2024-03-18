package com.ict.nutrimate_android.view.login.join;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ict.nutrimate_android.databinding.LoginJoinpage3Binding;

public class JoinActivity3 extends AppCompatActivity {

    private LoginJoinpage3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginJoinpage3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 사용자 정의
        binding.joinDiet4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // join_diet4 체크박스가 선택되었을 때만 실행
            if (isChecked) {
                // join_diet_custom1, join_diet_custom2, join_diet_custom3의 enabled 속성을 true로 변경
                binding.joinDietCustom1.setEnabled(true);
                binding.joinDietCustom2.setEnabled(true);
                binding.joinDietCustom3.setEnabled(true);
            } else {
                // join_diet4 체크박스가 선택되지 않았을 때는 enabled 속성을 다시 false로 변경
                binding.joinDietCustom1.setEnabled(false);
                binding.joinDietCustom2.setEnabled(false);
                binding.joinDietCustom3.setEnabled(false);
            }
        });

        // 다음 버튼 클릭 이벤트 처리
        binding.joinBtn.setOnClickListener(v -> {

            StringBuilder dietInfo = new StringBuilder();

            // 선택된 식단 종류 확인
            int selectedDietId = binding.radioGroup.getCheckedRadioButtonId();
            if (selectedDietId != -1) {
                RadioButton selectedDietRadioButton = findViewById(selectedDietId);
                dietInfo.append("선택한 식단: ").append(selectedDietRadioButton.getText()).append("\n");
            }

            // joinDiet4가 선택되었을 때에만 토스트로 탄수화물, 단백질, 지방 정보를 띄웁니다.
            if (binding.joinDiet4.isChecked()) {
                // 사용자 정의 식단 정보 가져오기
                EditText[] customDietEditTexts = {
                        binding.joinDietCustom1,
                        binding.joinDietCustom2,
                        binding.joinDietCustom3
                };

                String[] nutrients = {"탄수화물", "단백질", "지방"};
                for (int i = 0; i < customDietEditTexts.length; i++) {
                    String value = customDietEditTexts[i].getText().toString();
                    if (!value.isEmpty()) {
                        dietInfo.append(nutrients[i]).append(": ").append(value).append("\n");
                    }
                }
            }

            // 정보가 입력되지 않은 경우 처리
            if (dietInfo.toString().isEmpty()) {
                Toast.makeText(JoinActivity3.this, "식단 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 토스트로 출력
                Toast.makeText(JoinActivity3.this, dietInfo.toString(), Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(JoinActivity3.this, JoinActivity4.class);
            startActivity(intent);
        });

    }

}
