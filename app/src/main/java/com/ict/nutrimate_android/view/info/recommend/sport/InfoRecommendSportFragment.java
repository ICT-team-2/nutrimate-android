package com.ict.nutrimate_android.view.info.recommend.sport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ict.nutrimate_android.databinding.InfoRecommendSportBinding;
import com.ict.nutrimate_android.retrofit.FlaskService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoRecommendSportFragment extends Fragment {

    private InfoRecommendSportAdapter adapter;
    private InfoRecommendSportBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        binding = InfoRecommendSportBinding.inflate(inflater, parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FlaskService service = RetrofitClient.getFlaskService();
        Call<List<InfoRecommendSportItem>> call = service.exerciseinfo();
        call.enqueue(new Callback<List<InfoRecommendSportItem>>() {
            @Override
            public void onResponse(Call<List<InfoRecommendSportItem>> call, Response<List<InfoRecommendSportItem>> response) {
                if(response.isSuccessful()) {
                    //서버에서 [{},{},...] JSON배열로 응답]
                    List<InfoRecommendSportItem> sportItems = response.body();
                    adapter = new InfoRecommendSportAdapter(getActivity(),sportItems);
                    if (binding != null && binding.recyclerViewSport != null) {
                        binding.recyclerViewSport.setAdapter(adapter);
                        binding.recyclerViewSport.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<InfoRecommendSportItem>> call, Throwable t) {}
        });
    }///////////////////////////////////

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

}
