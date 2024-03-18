package com.ict.nutrimate_android.view.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ict.nutrimate_android.databinding.HomeMainBinding;
import com.ict.nutrimate_android.view.home.record.MainRecordContent;
import com.ict.nutrimate_android.view.home.statistics.MainStatisticsContent;

public class MainHomeContent extends Fragment {

    private HomeMainBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomeMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 프래그먼트 교체
        replaceFragment(new MainRecordContent());

        // 기록 버튼 클릭 이벤트 처리
        binding.button1.setOnClickListener(v -> {
            binding.button1.setTypeface(null, Typeface.BOLD);
            binding.button2.setTypeface(null, Typeface.NORMAL);
            replaceFragment(new MainRecordContent());
            // 밑줄 이동
            animateIndicator(0);
        });
        // 통계 버튼 클릭 이벤트 처리
        binding.button2.setOnClickListener(v -> {
            binding.button1.setTypeface(null, Typeface.NORMAL);
            binding.button2.setTypeface(null, Typeface.BOLD);
            replaceFragment(new MainStatisticsContent());
            // 밑줄 이동
            animateIndicator(1);
        });

        return view;
    }

    // 프래그먼트 교체 메서드
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(binding.homeContainer.getId(), fragment);
        transaction.addToBackStack(null); // 백 스택에 추가하여 뒤로 가기 버튼으로 이전 화면으로 이동할 수 있도록 함
        transaction.commit();
    }

    // 밑줄 이동 애니메이션
    private void animateIndicator(int selectedTab) {
        View buttonIndicator = binding.buttonIndicator;
        float translationX = 0;

        switch (selectedTab) {
            case 0:
                translationX = 0;
                break;
            case 1:
                translationX = binding.button2.getX() - binding.button1.getX() - 7;
                break;
        }
        buttonIndicator.animate().translationX(translationX).setDuration(200).start();
    }

}
