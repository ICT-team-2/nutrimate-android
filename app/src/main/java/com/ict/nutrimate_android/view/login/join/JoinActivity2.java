package com.ict.nutrimate_android.view.login.join;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.databinding.LoginJoinpage2Binding;

public class JoinActivity2 extends AppCompatActivity {

    private LoginJoinpage2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginJoinpage2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 체크박스의 상태가 변경될 때마다 호출될 리스너를 설정합니다.
        binding.joinReasonCheck6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // joinReasonCheck6이 선택된 경우 joinReasonEdit을 활성화합니다.
            binding.joinReasonEdit.setEnabled(isChecked);
            // joinReasonEdit의 포커스를 설정합니다.
            if (isChecked) {
                binding.joinReasonEdit.requestFocus();
            } else {
                binding.joinReasonEdit.clearFocus();
            }
        });

        // 다음 버튼 클릭 이벤트 처리
        binding.joinBtn.setOnClickListener(v -> {

            StringBuilder reasons = new StringBuilder();

            // 사용자가 선택한 이유들을 StringBuilder에 추가합니다.
            if (binding.joinReasonCheck1.isChecked()) {
                reasons.append("A. 질병으로 인한 관리 목적\n");
            }
            if (binding.joinReasonCheck2.isChecked()) {
                reasons.append("B. 다이어트(미용 목적)\n");
            }
            if (binding.joinReasonCheck3.isChecked()) {
                reasons.append("C. 벌크업(근육량 증가)\n");
            }
            if (binding.joinReasonCheck4.isChecked()) {
                reasons.append("D. 면역력을 높이기 위해\n");
            }
            if (binding.joinReasonCheck5.isChecked()) {
                reasons.append("E. 불규칙한 식사 패턴을 바꾸기 위해\n");
            }
            if (binding.joinReasonCheck6.isChecked()) {
                // joinReasonCheck6이 선택된 경우 joinReasonEdit을 활성화합니다.
                reasons.append("F. 또 다른 이유가 있어요!\n");
            }

            // 사용자가 이유를 직접 입력한 경우를 고려하여 추가합니다.
            String reasonText = binding.joinReasonEdit.getText().toString().trim();
            if (!reasonText.isEmpty()) {
                reasons.append(reasonText).append("\n");
            }

            // 선택된 이유가 없는 경우에 대한 처리
            if (reasons.length() == 0) {
                Toast.makeText(JoinActivity2.this, "이유를 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 선택된 이유를 토스트로 출력
                Toast.makeText(JoinActivity2.this, reasons.toString(), Toast.LENGTH_SHORT).show();

                // 다음 화면으로 이동
                Intent intent = new Intent(JoinActivity2.this, JoinActivity3.class);
                startActivity(intent);
            }
        });
    }
}