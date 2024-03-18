package com.ict.nutrimate_android.view.info.recommend.nutri;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ict.nutrimate_android.databinding.InfoRecommendNutriBinding;
import com.ict.nutrimate_android.retrofit.FlaskService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoRecommendNutriFragment extends Fragment {
    private InfoRecommendNutriAdapter adapter;
    private InfoRecommendNutriBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        binding = InfoRecommendNutriBinding.inflate(inflater, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FlaskService service = RetrofitClient.getFlaskService();
        Call<List<InfoRecommendNutriItem>> call = service.nutrientsinfo();
        call.enqueue(new Callback<List<InfoRecommendNutriItem>>() {
            @Override
            public void onResponse(Call<List<InfoRecommendNutriItem>> call, Response<List<InfoRecommendNutriItem>> response) {
                if(response.isSuccessful()) {
                    //서버에서 [{},{},...] JSON배열로 응답]
                    List<InfoRecommendNutriItem> foodItems = response.body();
                    if (foodItems != null) {
                        adapter = new InfoRecommendNutriAdapter(getActivity(), foodItems);
                        if (binding != null && binding.recyclerViewNutri != null) {
                            binding.recyclerViewNutri.setAdapter(adapter);
                            binding.recyclerViewNutri.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<InfoRecommendNutriItem>> call, Throwable t) {}
        });
    }///////////////////////////////////

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}
