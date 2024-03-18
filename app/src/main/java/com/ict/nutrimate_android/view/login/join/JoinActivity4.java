package com.ict.nutrimate_android.view.login.join;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.databinding.LoginJoinpage4Binding;
import com.ict.nutrimate_android.view.login.join.join5.JoinActivity5;

public class JoinActivity4 extends AppCompatActivity {

    private LoginJoinpage4Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginJoinpage4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 다음 버튼 클릭 이벤트 처리
        binding.joinBtn.setOnClickListener(v -> {

            boolean isFoodChecked = false;
            boolean isExerciseChecked = false;

            // 음식 관련 체크박스 중 하나 이상 선택되었는지 확인
            CheckBox[] foodCheckboxes = {
                    binding.joinFoodCheck1,
                    binding.joinFoodCheck2,
                    binding.joinFoodCheck3,
                    binding.joinFoodCheck4,
                    binding.joinFoodCheck5,
                    binding.joinFoodCheck6
            };
            for (CheckBox checkBox : foodCheckboxes) {
                if (checkBox.isChecked()) {
                    isFoodChecked = true;
                    break;
                }
            }

            // 운동 관련 라디오버튼 중 하나 선택되었는지 확인
            int selectedExerciseId = binding.radioGroup.getCheckedRadioButtonId();
            if (selectedExerciseId != -1) {
                isExerciseChecked = true;
            }

            StringBuilder info = new StringBuilder();

            // 식생활 정보 가져오기
            for (int i = 0; i < binding.signupLayout.getChildCount(); i++) {
                if (binding.signupLayout.getChildAt(i) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) binding.signupLayout.getChildAt(i);
                    if (checkBox.isChecked()) {
                        info.append(checkBox.getText()).append("\n");
                    }
                }
            }

            // 일주일에 운동 횟수 정보 가져오기
            int selectedId = binding.radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton radioButton = findViewById(selectedId);
                info.append("일주일에 운동 횟수: ").append(radioButton.getText());
            }

            // 음식과 운동 중 하나라도 선택되지 않은 경우에 대한 처리
            if (!isFoodChecked || !isExerciseChecked) {
                Toast.makeText(this, "음식과 운동을 모두 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Toast.makeText(this, info.toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(JoinActivity4.this, JoinActivity5.class);
                startActivity(intent);
            }
        });

    }

}
