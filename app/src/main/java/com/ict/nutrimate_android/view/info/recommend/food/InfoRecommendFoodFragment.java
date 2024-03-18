package com.ict.nutrimate_android.view.info.recommend.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ict.nutrimate_android.databinding.InfoRecommendFoodBinding;
import com.ict.nutrimate_android.retrofit.FlaskService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoRecommendFoodFragment extends Fragment {
    private InfoRecommendFoodAdapter adapter;
    private InfoRecommendFoodBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        binding = InfoRecommendFoodBinding.inflate(inflater, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FlaskService service = RetrofitClient.getFlaskService();
        Call<List<InfoRecommendFoodItem>> call = service.recipeinfo();
        call.enqueue(new Callback<List<InfoRecommendFoodItem>>() {
            @Override
            public void onResponse(Call<List<InfoRecommendFoodItem>> call, Response<List<InfoRecommendFoodItem>> response) {
                if(response.isSuccessful()) {
                    //서버에서 [{},{},...] JSON배열로 응답]
                    List<InfoRecommendFoodItem> foodItems = response.body();
                    if (binding != null && binding.recyclerViewFood != null) {
                        adapter = new InfoRecommendFoodAdapter(getActivity(), foodItems);
                        binding.recyclerViewFood.setAdapter(adapter);
                        binding.recyclerViewFood.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<InfoRecommendFoodItem>> call, Throwable t) {}
        });
    }///////////////////////////////////

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}
