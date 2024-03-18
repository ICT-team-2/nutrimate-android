package com.ict.nutrimate_android.view.mypage.profileedit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.LoginJoinpage5Binding;
import com.ict.nutrimate_android.databinding.MypageMyInfoEditBinding;
import com.ict.nutrimate_android.view.login.join.join5.JoinActivity5;
import com.ict.nutrimate_android.view.mypage.MyPageContent;

public class MyPageProfileEditActivity extends AppCompatActivity {

    private MypageMyInfoEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MypageMyInfoEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Spinner spinner = binding.mypageMyInfoEditDiet; //식단
        String[] items = new String[]{"일반 식단","운동 식단","키토 식단"};

        // ArrayAdapter를 사용하여 스피너에 값을 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinner1 = binding.mypageMyInfoEditSport; //일주일에 운동을 하는 횟수
        String[] items1 = new String[]{"적게(0-2회)","보통(3-4회)","많이(5회 이상)"};

        // ArrayAdapter를 사용하여 두 번째 스피너에 값을 설정
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        ImageView profile = binding.mypageMyInfoEditProfile; //프로필
        EditText intro = binding.mypageMyInfoEditIntro; //자기소개
        EditText email = binding.mypageMyInfoEditEmail; //이메일
        EditText kcal = binding.mypageMyInfoEditKcal; //일일 목표 칼로리
        EditText carbo = binding.mypageMyInfoEditCarbo; //탄수화물
        EditText protein = binding.mypageMyInfoEditProtein; //단백질
        EditText province = binding.mypageMyInfoEditProvince; //지방
        RadioGroup radioGroup = binding.radioGroup; //라디오그룹
        RadioButton radioMale = binding.radioMale; //남자
        RadioButton radioFemale = binding.radioFemale; //여자
        EditText tall = binding.mypageMyInfoEditTall; //키
        EditText weight = binding.mypageMyInfoEditWeight; //몸무게

        binding.editJoin5.setOnClickListener(v -> {
            Intent intent = new Intent(this, JoinActivity5.class);
            startActivity(intent);
            finish(); // 이전 액티비티를 종료하여 스택에서 제거
        });

        binding.joinComplete.setOnClickListener(v -> {
            onBackPressed(); // 백 버튼을 클릭한 효과를 줌
        });

    }
}