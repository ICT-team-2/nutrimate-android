package com.ict.nutrimate_android.view.board;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ict.nutrimate_android.databinding.BoardMainBinding;
import com.ict.nutrimate_android.view.board.challenge.BoardChallengeContent;
import com.ict.nutrimate_android.view.board.feed.BoardFeedContent;
import com.ict.nutrimate_android.view.board.Info.BoardFoodSportContent;

public class BoardMainContent extends Fragment {
    private BoardMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BoardMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        replaceFragment(new BoardFoodSportContent());

        // 정보공유 버튼 클릭 이벤트 처리
        binding.button1.setOnClickListener(v -> {
            binding.button1.setTypeface(null, Typeface.BOLD);
            binding.button2.setTypeface(null, Typeface.NORMAL);
            binding.button3.setTypeface(null, Typeface.NORMAL);
            // 밑줄의 너비 변경
            setIndicatorWidth(95);
            // 프래그먼트 교체
            replaceFragment(new BoardFoodSportContent());
            // 밑줄 이동
            animateIndicator(0);
        });
        // 피드 버튼 클릭 이벤트 처리
        binding.button2.setOnClickListener(v -> {
            binding.button1.setTypeface(null, Typeface.NORMAL);
            binding.button2.setTypeface(null, Typeface.BOLD);
            binding.button3.setTypeface(null, Typeface.NORMAL);
            // 밑줄의 너비 변경
            setIndicatorWidth(50);
            // 프래그먼트 교체
            replaceFragment(new BoardFeedContent());
            // 밑줄 이동
            animateIndicator(1);
        });
        // 챌린지 버튼 클릭 이벤트 처리
        binding.button3.setOnClickListener(v -> {
            binding.button1.setTypeface(null, Typeface.NORMAL);
            binding.button2.setTypeface(null, Typeface.NORMAL);
            binding.button3.setTypeface(null, Typeface.BOLD);
            // 밑줄의 너비 변경
            setIndicatorWidth(75);
            // 프래그먼트 교체
            replaceFragment(new BoardChallengeContent());
            // 밑줄 이동
            animateIndicator(2);
        });

        return view;
    }

    // 프래그먼트 교체 메서드
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(binding.boardContainer.getId(), fragment);
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
                translationX = calculateTranslationX(binding.button2, binding.button1);
                break;
            case 2:
                translationX = calculateTranslationX(binding.button3, binding.button1);
                break;
        }
        buttonIndicator.animate().translationX(translationX).setDuration(200).start();
    }

    // 버튼 간 이동 거리 계산
    private float calculateTranslationX(TextView button, TextView firstButton) {
        float buttonX = button.getX();
        float firstButtonX = firstButton.getX();
        // 첫 번째 버튼부터 현재 버튼까지의 거리를 구하여 반환합니다.
        return buttonX - firstButtonX - 7; // 여기서 7은 임의로 설정한 값입니다.
    }

    // 밑줄의 너비를 변경
    private void setIndicatorWidth(int widthDp) {
        ViewGroup.LayoutParams params = binding.buttonIndicator.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, getResources().getDisplayMetrics());
        binding.buttonIndicator.setLayoutParams(params);
    }

}/////class
