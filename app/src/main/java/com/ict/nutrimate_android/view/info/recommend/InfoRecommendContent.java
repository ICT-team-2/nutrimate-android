package com.ict.nutrimate_android.view.info.recommend;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ict.nutrimate_android.databinding.InfoRecommendBinding;
import com.ict.nutrimate_android.view.info.recommend.food.InfoRecommendFoodFragment;
import com.ict.nutrimate_android.view.info.recommend.nutri.InfoRecommendNutriFragment;
import com.ict.nutrimate_android.view.info.recommend.sport.InfoRecommendSportFragment;

public class InfoRecommendContent extends Fragment {

    private InfoRecommendBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = InfoRecommendBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //프래그먼트 생성
        InfoRecommendFoodFragment infoRecommendFoodFragment = new InfoRecommendFoodFragment();
        InfoRecommendNutriFragment infoRecommendNutriFragment = new InfoRecommendNutriFragment();
        InfoRecommendSportFragment infoRecommendSportFragment = new InfoRecommendSportFragment();

        // 화면 로드시 첫번째 프래그먼트로 화면 설정
        getChildFragmentManager().beginTransaction().replace(binding.container.getId(), infoRecommendFoodFragment).commit();


        // 식단 추천 버튼 이벤트 처리
        binding.recommendFood.setOnClickListener(v -> {
            getChildFragmentManager().beginTransaction().replace(binding.container.getId(), infoRecommendFoodFragment).commit();
            binding.recommendFood.setTypeface(null, Typeface.BOLD);
            binding.recommendSport.setTypeface(null, Typeface.NORMAL);
            binding.recommendNutri.setTypeface(null, Typeface.NORMAL);
        });
        // 운동 추천 버튼 이벤트 처리
        binding.recommendSport.setOnClickListener(v -> {
            getChildFragmentManager().beginTransaction().replace(binding.container.getId(), infoRecommendSportFragment).commit();
            binding.recommendFood.setTypeface(null, Typeface.NORMAL);
            binding.recommendSport.setTypeface(null, Typeface.BOLD);
            binding.recommendNutri.setTypeface(null, Typeface.NORMAL);
        });
        // 영양제 추천 버튼 이벤트 처리
        binding.recommendNutri.setOnClickListener(v -> {
            getChildFragmentManager().beginTransaction().replace(binding.container.getId(), infoRecommendNutriFragment).commit();
            binding.recommendFood.setTypeface(null, Typeface.NORMAL);
            binding.recommendSport.setTypeface(null, Typeface.NORMAL);
            binding.recommendNutri.setTypeface(null, Typeface.BOLD);
        });
        
        return view;
    }
}